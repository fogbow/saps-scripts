# Objective
Its purpose is to perform the algorithm for the input data downloaded and/or preprocessed in the steps of Inputdownloader and Preprocessing respectively. For example, monitoring algorithms.

# Parameters
The template used by Worker requires a `run.sh` script that receives the following information:
- `Input path` is the path to the directory containing the data downloaded by Inputdownlaoder.
- `Output path` is the path to the directory that should contain all results of the execution of the algorithm that they wish to be persisted.
- `Preprocessing Path` is the path to the directory containing the data preprocessed by Preprocessing.
- `Metadata path` is the path to the directory that should contain all the metadata information for this step. For instance:
  - Execution log
  - Processing date

This data is passed as a parameter to the `run.sh` script that will accomplish its purpose (as described above).

# Usage
The `run.sh` script will receive the default parameters below:
```
./run.sh input_path output_path preprocessing_path metadata_path
```
At where:
- `input_path` is the path to the directory containing the data downloaded by Inputdownloader.
- `output_path` is the output directory path of the Worker.
- `preprocessing_path` is the path to the directory containing the data preprocessed by Preprocessing.
- `metadata_path` is the path of the metadata directory of the Worker.

# Exit code
The `run.sh` script should have the following return pattern:
- `0` represents a successful execution.

And any other exit code will be considered failed.