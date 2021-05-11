#!/bin/bash
find .

mkdir results

echo "Evaluation MediaStore"
python ../eval/eval.py -g ../case_studies/mediastore/gold_standard/Eval_Ecsa_MediaStore.csv -t ./MS -o results/eval_results_MS.csv

echo "Evaluation Teammates"
python ../eval/eval.py -g ../case_studies/teammates/gold_standard/Goldstandard_Teammates.csv -t ./TM -o results/eval_results_TM.csv

echo "Evaluation TeaStore"
python ../eval/eval.py -g ../case_studies/teastore/gold_standards/Eval_Ecsa_TeaStore.csv -t ./TS -o results/eval_results_TS.csv

find .