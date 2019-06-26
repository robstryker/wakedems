#!/bin/sh
mvn clean install
cd distribution/target
unzip *-bin.zip
cd wake.voter.reports.distribution-0.1.0-SNAPSHOT/
cd wakeVoter/bin/
./downloadData.sh
./runAllReports.sh
