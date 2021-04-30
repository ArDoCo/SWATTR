# README for the evaluation script

Run with the following command(s)

`python eval.py -g PATH_TO_GOLD -t PATH_TO_RESULT_CSV_FOLDER -o OUTPUTNAME`


For example:
```
python eval.py -g ../case_studies/mediastore/gold_standard/Eval_Ecsa_MediaStore.csv -t results_swattr/mediastore/ -o eval_results/mediastore/ours.csv
python eval.py -g ../case_studies/mediastore/gold_standard/Eval_Ecsa_MediaStore.csv -t results_rodriguez20/mediastore -o eval_results/mediastore/rodriguez20.csv
python eval.py -g ../case_studies/mediastore/gold_standard/Eval_Ecsa_MediaStore.csv -t results_zhang16/mediastore -o eval_results/mediastore/zhang16.csv

python eval.py -g ../case_studies/teastore/gold_standards/Eval_Ecsa_TeaStore.csv -t results_swattr/teaStore/ -o eval_results/teastore/ours.csv
python eval.py -g ../case_studies/teastore/gold_standards/Eval_Ecsa_TeaStore.csv -t results_rodriguez20/teastore -o eval_results/teastore/rodriguez20.csv
python eval.py -g ../case_studies/teastore/gold_standards/Eval_Ecsa_TeaStore.csv -t results_zhang16/teastore -o eval_results/teastore/zhang16.csv

python eval.py -g ../case_studies/teammates/gold_standard/Goldstandard_Teammates.csv -t results_swattr/teammates/ -o eval_results/teammates/ours.csv
python eval.py -g ../case_studies/teammates/gold_standard/Goldstandard_Teammates.csv -t results_rodriguez20/teammates -o eval_results/teammates/rodriguez20.csv
python eval.py -g ../case_studies/teammates/gold_standard/Goldstandard_Teammates.csv -t results_zhang16/teammates -o eval_results/teammates/zhang16.csv
```


Similarities:

```
python ./eval.py -g ../case_studies/mediastore/gold_standard/Eval_Ecsa_MediaStore.csv -t ./results_swattr/mediastore/Similarities -o ./results_swattr/mediastore/Similarities-Comparison.csv
python ./eval.py -g ../case_studies/teammates/gold_standard/Goldstandard_Teammates.csv -t ./results_swattr/teammates/Similarities -o ./results_swattr/teammates/Similarities-Comparison.csv
python ./eval.py -g ../case_studies/teastore/gold_standards/Eval_Ecsa_TeaStore.csv -t ./results_swattr/teaStore/Similarities -o ./results_swattr/teaStore/Similarities-Comparison.csv
```
