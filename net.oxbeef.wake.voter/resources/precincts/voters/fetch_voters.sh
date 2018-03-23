#!/bin/sh
wget http://msweb03.co.wake.nc.us/bordelec/downloads/data/vrdb.zip
unzip vrdb.zip
ssconvert vrdb02_20.xlsx database2_20.csv
ssconvert vrdb01.xlsx database1.csv

for fn in `cat database2_20.csv | cut -d "," -f 32  | sort  | uniq | grep "-"`; do
    echo "the next file is $fn"
    cat database2_20.csv | grep $fn > $fn.csv
done


for fn in `cat database1.csv | cut -d "," -f 32  | sort  | uniq | grep "-"`; do
    echo "the next file is $fn"
    cat database1.csv | grep $fn > $fn.csv
done


