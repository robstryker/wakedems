#!/bin/sh

previousData=`{ cat historical_filenames | cut -d "_" -f 3 &  date --date="-6 month" "+%Y%m%d"; } | sort | grep -v ".zip" -C 1 | head -n 1`
previousData2="VR_Snapshot_${previousData}"
previousData3=https://s3.amazonaws.com/dl.ncsbe.gov/data/Snapshots/${previousData2}

mkdir previous
cd previous
wget $previousData3
unzip $previousData2

unzippedfname=`ls -1 *.txt`
iconv -f UTF-16LE -t UTF-8 $unzippedfname > $unzippedfname.tsv.utf16
rm *.txt

# Now remove the bom
tail -c +4 $unzippedfname.tsv.utf16 > $unzippedfname.tsv


cat ../current/01-01.tsv | cut -f 3 | head -n -1  | sed 's/"//g' > 01-01.pattern
echo -e "WAKE" >> 01-01.pattern


