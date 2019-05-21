#!/bin/sh
cd ../resources/precincts/voters/
rm *.csv *.zip *.xlsx 
./fetch_voters.sh
cd ../../../bin

