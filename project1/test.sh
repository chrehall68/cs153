#!/bin/sh

# requires java, fpc
# run from project1 dir

# compile java
# for each test case in ./inputs/
# copy to ./generated_tests/
# run java -scan
# for each (identifier, colon_equals) pair, 
# add a var declaration after the program identifier

# for each generated test, run fpc
# run and the binary, write to expected output file

# for each of the original tests, run -execute
# compare actual vs. expected outputs


# map_get map_name key
# $ret will have the value
map_get() {
    local map=$1
    local key=$2
    ret=`eval echo '$'$map$key`
}

# map_set map_name key value
map_set() {
    local map=$1
    local key=$2
    local value=$3
    eval "$map$key='$value'"
}

# map_del map_name key
map_del() {
    local map=$1
    local key=$2
    eval "unset $map$key"
}


src_files=$(find . -name *.java)
javac $src_files -cp ./ajs.printutils.jar -d bin

GEN_DIR=generated_tests

touch $GEN_DIR
rm -rf $GEN_DIR
mkdir $GEN_DIR

failures=0
test_count=0

for test_file in inputs/*.txt; do
    test_count=$((test_count+1))
    echo $test_file
    filename=$(basename $test_file)
    file_stem=${filename%.*}
    
	cp "$test_file" "$GEN_DIR/$filename"
	
	java -cp bin:ajs.printutils.jar Simple -scan "$test_file" > scan.txt

    # find all assignmentment statements
    # the type is greedily chosen
    # var names are converted to lowercase since pascal ignores capitalization
    vars=$(awk '
        assignStatement && /(INTEGER|REAL|STRING) :/ {
            type = $0
            sub(/ : .*/, "", type)
            sub(/^[[:blank:]]*/, "", type)
            
            printf "%s:%s;", var_name, type
            assignStatement = 0
        }
        assignStatement && /IDENTIFIER :/ {
            src_name = $0
            sub(/[[:blank:]]*IDENTIFIER : /, "", src_name)
            
            printf "%s:IDENTIFIER:%s;", var_name, src_name
            assignStatement = 0
        }
        
        pending && /COLON_EQUALS :/ {
            assignStatement = 1
        }

        { pending = 0 }

        /IDENTIFIER : / {
            var_name = $0
            sub(/[[:blank:]]*IDENTIFIER : /, "", var_name)
            var_name = tolower(var_name)
            
            pending = 1
        }
    ' scan.txt)
    
    IFS=';' # split ($vars) by semicolon for iteration
    
    # search for explicit types
    for decl in $vars; do
        var_name=$(echo $decl | cut -d ":" -f 1)
        var_type=$(echo $decl | cut -d ":" -f 2)

        if [ "$var_type" = "IDENTIFIER" ]; then
            continue
        fi

        map_get "var_map_" "$var_name"
        value=$ret
        if [ -z ${value} ]; then
            map_set "var_map_" "$var_name" "$var_type"
        else
            map_get "var_map_" $var_name
            found_type=$ret

            # override integer types with floating point
            if [ "$found_type" = "INTEGER" ] && [ "$var_type" = "REAL" ]; then
                map_set "var_map_" $var_name $var_type
            fi
        fi
    done

    # resolve the type if RHS is a variable
    # occurrence of IDENTIFIER declarations should already be sorted (from the scanner)
    # therefore, we don't need to perform topo-sort or multiple passes
    for decl in $vars; do
        var_name=$(echo $decl | cut -d ":" -f 1)
        var_type=$(echo $decl | cut -d ":" -f 2)

        if [ "$var_type" != "IDENTIFIER" ]; then
            continue
        fi

        src_var=$(echo $decl | cut -d ":" -f 3)

        map_get "var_map_" $var_name
        value=$ret
        if [ -z ${value} ]; then
            map_get "var_map_" $src_var
            src_type=$ret
            map_set "var_map_" $var_name $src_type
        else
            map_get "var_map_" $var_name
            found_type=$ret

            map_get "var_map_" $src_var
            src_type=$ret

            if [ "$found_type" = "INTEGER" ] && [ "$src_type" = "REAL" ]; then
                map_set "var_map_" $var_name $src_type
            fi
        fi
    done

    # format the statements for output
    typed_vars=""
    for decl in $vars; do
        var_name=$(echo $decl | cut -d ":" -f 1)

        map_get "var_map_" $var_name
        var_type=$ret

        # default to Integer if no type is found
        # this can happen if a variable is assigned to itself
        if [ -z $var_type ]; then
            var_type="Integer"
        fi

        typed_vars="$typed_vars\n    $var_name : $var_type;"
    done

    # clear the map
    for decl in $vars; do
        var_name=$(echo $decl | cut -d ":" -f 1)
        map_del "var_map_" $var_name
    done

    typed_vars=$(echo "$typed_vars" | sort | uniq)

    # inject variable declarations
    if [ -n "$typed_vars" ]; then
        INSERT_TEXT="$typed_vars" awk '
            { print }
            /(program|PROGRAM) .*;/ {
                printf "var%s\n", ENVIRON["INSERT_TEXT"]
            }
        ' $test_file > $GEN_DIR/$filename
    fi

	expected_output=$GEN_DIR/${file_stem}.exp
	actual_output=$GEN_DIR/${file_stem}.out

    compiler_output="$GEN_DIR/$file_stem"-compiler_output.txt
	touch $compiler_output
	fpc $GEN_DIR/$filename > $compiler_output 2>&1

	binary=$GEN_DIR/$file_stem
	compiled=false
	if [ -f $binary ]; then
	    compiled=true
	    ./$binary > $expected_output 2>&1
	fi

    java -cp bin:ajs.printutils.jar Simple -execute $test_file > $actual_output 2>&1
    test_status=$?
	if [ $test_status -ne 0 ]; then
	    echo "❌ FAIL: Interpreter exit code: $test_status"
		failures=$((failures+1))
		echo "Interpreter output:"
		echo "================================================================================"
		cat $actual_output
		echo "================================================================================"
		echo ""
        continue
	fi

	syntax_error=false
	if grep -qe "^SYNTAX ERROR" $actual_output; then
	    syntax_error=true
	fi
	interpreter_error=$syntax_error

	if [ "$compiled" = "false" ] && [ "$interpreter_error" = "true" ]; then
        echo "✅ PASS"
        echo ""
        continue
	fi
	if [ "$compiled" = "false" ]; then
	    echo "❌ FAIL: reference pascal compilation failed with successful interpreter run"
		failures=$((failures+1))
		echo "Interpreter output:"
		echo "================================================================================"
		cat $actual_output
		echo "================================================================================"
		echo ""
		echo "Compiler output:"
		echo "================================================================================"
		cat $compiler_output
		echo "================================================================================"
		echo ""
        continue
	fi
	if [ "$interpreter_error" = "true" ]; then
        echo "❌ FAIL: interpreter syntax errors with successful reference pascal compile"
        failures=$((failures+1))
        echo "Parser output:"
		echo "================================================================================"
		cat $actual_output
		echo "================================================================================"
		echo ""
        continue
	fi
	

	if cmp --silent $expected_output $actual_output; then
	    echo "✅ PASS"
	else
	    echo "❌ FAIL: wrong output"
		failures=$((failures+1))

		echo "Interpreter (actual) output:"
		echo "================================================================================"
		cat $actual_output
		echo "================================================================================"
		echo ""
		echo "FPC (expected) output:"
		echo "================================================================================"
		cat $expected_output
		echo "================================================================================"
		echo ""
		echo "diff:"
		echo "================================================================================"
		diff --color $actual_output $expected_output
		echo "================================================================================"
	fi
	echo ""
done

echo ""
echo ""
echo "Passed $((test_count-failures)) / $test_count tests"

if [ $failures -ne 0 ]; then
    echo "Failed $failures tests"
    exit 1
fi
