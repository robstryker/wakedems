#!/bin/sh
rm -rf ../output
mkdir ../output
ls -1 ../resources/precincts/voters/*.csv | grep -v vrdb | cut -d "/" -f 5 | cut -d "." -f 1 | awk '{ print "java -jar reports-0.1.0-SNAPSHOT.jar " $0 " > ../output/" $0 ".txt";}' | sh
