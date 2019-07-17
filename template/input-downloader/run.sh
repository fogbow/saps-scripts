#!/bin/bash

#Objetive: Download the input data for processing
#For example: $0 landsat_5 215065 2004-12-01 /tmp/output /tmp/metadata

#Checking args
if [ $# -ne 5 ]
then
  echo "Usage: $0 landsat_X PPPRRR YYYY-MM-DD output_path metadata_path"
  exit 1
fi

#Capture args
DATASET=$1
PATHROW=$2
DATE=$3
OUTPUTPATH=$4
METADATAPATH=$5





#Exit code
This script should have the following return pattern:
- `0` represents a successful execution.
- `3` represents that the Landsat image was not found.
And any other exit code will be considered failed.