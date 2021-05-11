# SoftWare Architecture Text Tracelink Recovery (SWATTR)
[![DOI](https://zenodo.org/badge/359732572.svg)](https://zenodo.org/badge/latestdoi/359732572)
[![SWATTR Reproduction](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction.yml/badge.svg)](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction.yml)
[![Rodriguez20 Reproduction](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction-rodriguez.yml/badge.svg)](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction-rodriguez.yml)
[![Zhang16 Reproduction](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction-zhang.yml/badge.svg)](https://github.com/ArDoCo/SWATTR/actions/workflows/reproduction-zhang.yml)

## Structure
The current structure of this repository is currently as follows:
* [case_studies](case_studies): contains the models, texts, and gold standards of our used case studies,
* [eval](eval): contains the evaluation script, and the results of the evaluation,
* [swattr_core](swattr_core): contains a snapshot of [ArDoCo Core](https://github.com/ArDoCo/Core),
* [reproduction](reproduction): contains code to build and evaluate the SWATTR approach.

## Reproduction
As stated above, you can either use the instructions of the [reproduction directory](reproduction) to build SWATTR from sources or you can use GitHub Actions to reproduce the results.
If you want to use GitHub Actions, simply take a look at the [runs](https://github.com/ArDoCo/SWATTR/actions).
