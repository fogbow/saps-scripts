#!/bin/bash

## Objetive: Run the algorithm for the input data downloaded and/or preprocessed
## For example: $0 /tmp/input /tmp/output /tmp/preprocessing /tmp/metadata

## Checking args
if [ $# -ne 4 ]
then
  echo "Usage: $0 input_path output_path preprocessing_path metadata_path"
  exit 1
fi

## Capture args
INPUTPATH=$1
OUTPUTPATH=$2
PREPROCESSINGPATH=$3
METADATAPATH=$4





## Exit code
# The `run.sh` script should have the following return pattern:
# - `0` represents a successful execution.
# And any other exit code will be considered failed.