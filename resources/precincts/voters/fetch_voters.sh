#!/bin/sh
wget http://msweb03.co.wake.nc.us/bordelec/downloads/data/vrdb.zip
unzip vrdb.zip
for fn in `ls -1 *.xlsx`; do
   cmd1="ssconvert $fn $fn.database.csv"
   `$cmd1`
done

for csvfile in `ls -1 *.database.csv`; do
   echo $csvfile
   cmd="cat $csvfile | cut -d ',' -f 32 | sort | uniq | grep '-'"
   echo $cmd
   for fn in `bash -c "$cmd"`; do
      cat $csvfile | grep $fn > $fn.csv
   done
done


