#!/bin/bash
find .

case_studies="MS TS TM"

for cs in $case_studies; do
	mkdir $cs
	mv *_$cs* $cs/
done

find .
