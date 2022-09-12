#!/bin/sh
   cd current
   wget https://s3.amazonaws.com/dl.ncsbe.gov/data/ncvoter92.zip
   unzip ncvoter92.zip

   csvfile=ncvoter92.txt
   cmd="cat $csvfile | cut -f 35 | sort | uniq | grep '-' | tr -d '\"'"
   echo $cmd
   for fn in `bash -c "$cmd"`; do
      echo "Separating out precinct $fn"
      cat $csvfile | grep $fn > $fn.tsv
   done
   cd ../
