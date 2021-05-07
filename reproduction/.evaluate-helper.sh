#!/bin/bash

# Install BC
apt update && apt install bc -y

# Similarity
SIMILARITY=$(bc -l <<< "$1 / 100")
echo "similarityPercentage=$SIMILARITY" > /config.properties

