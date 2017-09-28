#!/bin/bash

java -Dlog4j.configuration=file:/home/ubuntu/execs/config/log4j.properties -jar /home/ubuntu/execs/USGS.jar $1 $2 $3

exit $?
