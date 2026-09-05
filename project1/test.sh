#!/bin/sh

# requires java, fpc
# run from project1 dir

# compile java
# for each test case in ./inputs/
# copy to ./generated_tests/
# run java -scan
# for each (identifier, colon_equals) pair, add a var declaration after the program identifier

# for each generated test, run fpc
# run and the binary, write to expected output file

# for each of the original tests, run -execute
# compare actual vs. expected outputs

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

    # find all variables followed by an assignment operator
    # hardcode types to integer (type deduction is TODO)
    vars=$(awk '
        pending && /COLON_EQUALS :/ {
            print "    " value " : Integer;"
        }

        { pending = 0 }

        /IDENTIFIER : / {
            value = $0
            sub(/[[:blank:]]*IDENTIFIER : /, "", value)
            
            pending = 1
        }
    ' scan.txt | sort | uniq)

    # inject variable declarations
    if [ -n "$vars" ]; then
        INSERT_TEXT="$vars" awk '
            { print }
            /(program|PROGRAM) .*;/ {
                print "var"
                printf "%s", ENVIRON["INSERT_TEXT"]
            }
        ' $test_file > $GEN_DIR/$filename
    fi

	expected_output=$GEN_DIR/${file_stem}.exp
	actual_output=$GEN_DIR/${file_stem}.out

    compiler_output=compiler_output.txt
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
	    echo "❌ FAIL: bad exit code: $test_status"
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
