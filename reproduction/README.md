# Reproduction of Results
This directory helps you to reproduce the results

## Compile SWATTR
You have multiple options to compile SWATTR

1. You can use the compiled version of SWATTR that is stored in the referenced Zenodo.
2. You can execute `mvn clean package` in `../swattr_core` and obtain the bundle (location after build: `../swattr_core/pipeline/target/ardoco-core-pipeline.jar`
3. If you have docker you can simply run `bash compile.sh` to build SWATTR from sources.