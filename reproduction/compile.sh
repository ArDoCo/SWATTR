#!/bin/bash

## Check Location

PWD=$(pwd)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$PWD" != "$DIR" ]
then
        echo "This script does not support execution from other directories"
        exit 1
fi

## Check GIT Support
if ! command -v git &> /dev/null
then
    echo "git could not be found"
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

cd ..

PARENT=$(pwd)/swattr_parent
SWATTR=$(pwd)/swattr_core
OUTPUT=$(pwd)/reproduction

echo "Building from sources"
docker run --rm -it -v "$PARENT:/root/swattr_parent_src:ro" -v "$SWATTR:/root/swattr_core_src:ro" -v "$OUTPUT:/root/swattr_out" maven:3-openjdk-16 sh -c "cp -r /root/swattr_parent_src /root/swattr_parent && cp -r /root/swattr_core_src /root/swattr_core && cd /root/swattr_parent && mvn clean install && cd /root/swattr_core && mvn clean package && cp -f /root/swattr_core/pipeline/target/ardoco-core-pipeline.jar /root/swattr_out/ardoco-core-pipeline.jar"

