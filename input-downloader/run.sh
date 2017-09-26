#!/bin/bash

mvn install
java -jar Downloader.jar $1 $2 $3

exit $?
