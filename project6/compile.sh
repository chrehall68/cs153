#!/bin/sh

java -jar ./antlr-4.13.2-complete.jar -o ./src/intermediate/antlr4/ -visitor ./Pcl_C2.g4
java -jar ./antlr-4.13.2-complete.jar -o ./src/backend/interpreter/antlr4/ ./Commander.g4

javac $(find . -name "*.java") -cp ./antlr-4.13.2-complete.jar -d bin
