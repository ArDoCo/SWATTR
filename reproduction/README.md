# Reproduction of Results
This directory helps you to reproduce the results

You will need **maven with JDK 16** or **docker** if you want to execute the approach yourself.
You also need **docker** to execute the evaluation.
If you don't want to execute the approach(es) yourself, you can also look at the [automatically executed workflows for reproduction](https://github.com/ArDoCo/SWATTR/actions) the get the results produced by GitHub Actions.

## 1. Compile SWATTR
You have multiple options to compile SWATTR

1. You can use the compiled version of SWATTR that is stored in the referenced Zenodo.
2. You can execute `mvn clean package` in `../swattr_core` and obtain the bundle (location after build: `../swattr_core/pipeline/target/ardoco-core-pipeline.jar` (you may have to setup github credentials to access the compiled maven packages (dependencies))
3. If you have docker you can simply run `bash compile.sh` to build SWATTR from sources.

## 2. Create Trace Links
You need docker to run the scripts.

To create a certain swattr result you can simply invoke `evaluate.sh` with the following parameters:

* Name of the case study: TM, TS, MS 
* Similarity Percentage as percentage, e.g. 75

A command could look like: `bash evaluate.sh TM 75`

Iff you want to create all evaluations simply invoke `bash evaluate-all.sh`
