#!/bin/sh

java -jar ./antlr-4.13.2-complete.jar -o ./src/intermediate/antlr4/ -visitor ./Pcl_P2.g4

javac $(find . -name "*.java") -cp ./antlr-4.13.2-complete.jar -d bin
