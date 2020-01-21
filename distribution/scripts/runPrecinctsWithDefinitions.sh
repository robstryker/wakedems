#!/bin/sh
rm -rf ../output
mkdir ../output

ls ../resources/precincts/definitions/ | grep -v template | cut -f 1 -d "." | awk '{ print "echo \"Running precinct " $0 "\"; java -jar reports-0.1.0-SNAPSHOT.jar " $0 " > ../output/" $0 ".txt";}' | sh
