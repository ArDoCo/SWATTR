#!/bin/bash
find .

case_studies="MS TS TM"

for cs in $case_studies; do
	mkdir $cs
done

for filename in *.tgz; do
	name=$(echo $filename | cut -d'.' -f 1)
	# $filename/$filename because github actions extracts the tgz into a folder called like the tgz
	mkdir $name && tar -xzf $filename/$filename -C $name --strip-components=1
	
	for cs in $case_studies; do
		if [[ "$name" == *"$cs"* ]]; then
			mv $name $cs/
		fi
	done
done

ls -l
