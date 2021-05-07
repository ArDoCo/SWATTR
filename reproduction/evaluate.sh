#!/bin/bash

PWD=$(pwd)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$PWD" != "$DIR" ]
then
        echo "This script does not support execution from other directories"
        exit 1
fi

## Check bc support
if ! command -v bc &> /dev/null
then
    echo "bc could not be found"
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

## Parse Parameters

if [ "$#" -ne 2 ]
then
	echo "Illegal number of arguments. We need Case Study and Similarity [0,100]"
	echo "Usage: bash evaluate.sh [MS|TS|TM] [0-100]"
	exit 1
fi

# Parse Case Study

SWATTR_DIR="$( cd .. && pwd )"

CASE_STUDY="NONE"
CASE_STUDY_TEXT=""
CASE_STUDY_MODEL=""

case "$1" in
   TM)   CASE_STUDY="TM"
		 CASE_STUDY_MODEL="/teammates/model/teammates.owl"
		 CASE_STUDY_TEXT="/teammates/text/TeammatesForEvalText.txt" ;;
   
   TS)   CASE_STUDY="TS"
		 CASE_STUDY_MODEL="/teastore/model/teastore.owl"
		 CASE_STUDY_TEXT="/teastore/text/EcsaText.txt" ;;
   
   MS)   CASE_STUDY="MS"
		 CASE_STUDY_MODEL="/mediastore/models/mediastore.owl"
		 CASE_STUDY_TEXT="/mediastore/text/Palladio_MediaStore_Text.txt" ;;		 
esac

if [ "$CASE_STUDY" == "NONE" ]
then
	echo "Invalid Case Study Selection"
	exit 2
fi
# echo "$CASE_STUDY $CASE_STUDY_MODEL $CASE_STUDY_TEXT"

# Parse Similarity

if [ "$2" -lt "0" ] || [ "$2" -gt "100" ]
then
	echo "Similarity must be in [0,100]"
	exit 2
fi

#SIMILARITY=$(bc -l <<< "$2 / 100")
#echo "Similarity: $SIMILARITY"

SIMILARITY_INT=$2


## Execute
echo "similarityPercentage=$SIMILARITY" > /tmp/config.properties
mkdir -p $PWD/swattr-results

docker run --rm -it \
	-v "$SWATTR_DIR/case_studies:/case_studies:ro" \
	-v "$PWD/ardoco-core-pipeline.jar:/pipeline.jar:ro" \
	-v "$PWD/swattr-results:/results" \
	-v "$PWD/.evaluate-helper.sh:/eval-helper.sh:ro" \
	openjdk:16-buster \
	sh -c "bash /eval-helper.sh $SIMILARITY_INT && java --illegal-access=permit -jar /pipeline.jar -n $CASE_STUDY -m /case_studies$CASE_STUDY_MODEL -t /case_studies$CASE_STUDY_TEXT -c /config.properties -o /results_raw && cp -f /results_raw/$CASE_STUDY\_trace_links.csv /results/$CASE_STUDY\_Links_Sim_$SIMILARITY_INT.csv"
