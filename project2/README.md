From the `project2` directory, you can compile by running
```sh
javac $(find . -name "*.java") -cp ./antlr-4.13.2-complete.jar -d bin 
```

Then, you can run the program by running
```sh
java -cp ./antlr-4.13.2-complete.jar:./bin SimpleA3 <input_file>
```