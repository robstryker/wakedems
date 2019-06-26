#!/bin/sh
cd ../resources/precincts/voters/
rm current/* 
./fetch_ncboe.sh
cd ../../../bin

cd ../resources/partyChange/
./fetchPartyChange.sh
cd ../../bin

