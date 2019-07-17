# Objective
Its purpose is to download the input data, including the Landsat image and other information useful for processing, e.g., station data.

# Parameters
The template used by the Inputdownloader component requires a `run.sh` script that receives the following information:
- `Dataset` is landsat collection to which the image belongs. For instance:
  - TM - Landsat 5
  - ETM - Landsat 7
  - LC - Landsat 8
- `Region` is the identifier of the scene (geographic location) to which an image belongs, i.e. the path / row of the scene. In [LandLook](https://landlook.usgs.gov/viewer.html) available from the USGS, in the `Map Overlay > WRS2 Path / Row Overlay` option it is possible to obtain this identifier in `path/row` format. For instance:
  -  215 / 65 (path is 215 and row is 65)
  -  217 / 66 (path is 217 and row is 66)
- `Date` is the day the image was taken by the Landsat satelite, usually the catch is made periodically every two weeks. For instance:
  - 2011-08-29 (year is 2011, month is 08 and day is 29)
- `Output path` is the path to the directory that should contain all the useful information extracted, e.g., input data (Landsat image) and other primordial information that will be used in the preprocessing and/or algorithm execution steps.
- `Metadata path` is the path to the directory that should contain all metadata information for this step. For instance:
  - Execution log
  - Processing date

This data is passed as a parameter to the `run.sh` script that will accomplish its purpose (as described above).

# Usage
The `run.sh` script will receive the default parameters below:
```
./run.sh landsat_X PPPRRR YYYY-MM-DD output_path / home / ubuntu / metadata
```
At where:
- `landsat_X` is the dataset that can come from the 3 possible values: landsat_5, landsat_7 and landsat_8.
- `PPPRRR` is the path / row of the image, in which, PPP is the path (with 3 digits) and RRR is the row (with 3 digits).
- `YYYY-MM-DD` is the date of the image, where YYYY is the year (with 4 digits), MM is the month (with 2 digits) and DD is the day (with 2 digits).
- `output_path` is the output directory path of the inputdownloader
- `metadata_path` is the path of the metadata directory of the inputdownloader

# Exit code
The `run.sh` script should have the following return pattern:
- `0` represents a successful execution.
- `3` represents that the Landsat image was not found.

And any other exit code will be considered failed.