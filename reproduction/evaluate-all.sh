#!/bin/bash

PWD=$(pwd)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$PWD" != "$DIR" ]
then
        echo "This script does not support execution from other directories"
        exit 1
fi

## Check Docker support
if ! command -v docker &> /dev/null
then
    echo "docker could not be found"
    exit 1
fi

docker ps > /dev/null

if [ $? -ne 0 ]
then
    echo "you are not allowed to run docker"
    exit 1
fi

if [ ! -f "./ardoco-core-pipeline.jar" ]
then
	echo "ardoco-core-pipeline.jar not found"
	exit 1
fi

# MediaStore
./evaluate.sh MS 50
./evaluate.sh MS 75
./evaluate.sh MS 100

# Teammates
./evaluate.sh TM 50
./evaluate.sh TM 75
./evaluate.sh TM 100

# TeaStore
./evaluate.sh TS 50
./evaluate.sh TS 75
./evaluate.sh TS 100