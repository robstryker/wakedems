# Wake Democrat Tools

## Summary

Java program + bash script to help parse voter records and discover likely contributors.

## Get the code

The easiest way to get started with the code is to [create your own fork](http://help.github.com/forking/), 
and then clone your fork:

    $ git clone git@github.com:<you>/wakedems.git
    $ cd wakedems
    $ git remote add upstream git://github.com/robstryker/wakedems.git
	
At any time, you can pull changes from the upstream and merge them onto your master:

    $ git checkout master               # switches to the 'master' branch
    $ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
    $ git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the
'upstream/master'.

## Building
Run the following commands to build a distribution

    $ cd wakedems
    $ mvn clean install

## Running / using the distribution
Run the following commands to extract, browse, and use the distribution

    $ cd distribution/target/
    $ unzip wake.voter.reports.distribution-0.1.0-SNAPSHOT-bin.zip
    $ cd wake.voter.reports.distribution-0.1.0-SNAPSHOT/wakeVoter/bin
    $ ./downloadData.sh
    $ ./runAllReports.sh

 
## Import the project into eclipse 
There's no clean maven build for this, so you'll just have to 
import it into eclipse and run the MyMain file for now. Sorry. 

