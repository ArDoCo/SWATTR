#!/bin/bash
ls -l

case_studies="MS TS TM"

for cs in $case_studies; do
	mkdir $cs
done

for filename in *.tgz; do
	name=$(echo $filename | cut -d'.' -f 1)
	mkdir $name && tar -xzf $filename -C $name --strip-components=1
	
	for cs in $case_studies; do
		if [[ "$name" == *"$cs"* ]]; then
			mv $name $cs/
		fi
	done
done

ls -l
