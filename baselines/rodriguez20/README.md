# Reimplementation of the approach by Rodriguez and Carver (2020) for TR between Text and (Palladio Component) Models

This is a reimplementation of the mentioned paper (see below) for the use case of creating tracelinks between Software Architecture Documentation texts and Models in the form of Palladio Component Models.

The reimplementation was done by Claudius Kocher, adaptations were performed by Jan Keim.

## How to use
The programs needs a few parameters to operate correctly:
1. Path to the folder with the input text documents 
2. Path to the folder containing the ".repository" file of a PCM 
3. Number of iterations 
4. Number of genes per chromosome 
5. Number of chromosomes per population 
6. Number of iterations of the whole algorithm with given parameters (used for calculation means because of randomness)

An example execution may contain the following parameters: <br>
`"../../case_studies/teammates/text" "../../case_studies/teammates/model" 750 20 151 10`

The output will be saved as CSV files within the [output-folder](./output).

## Original paper
>D. V. Rodriguez and D. L. Carver, "Multi-Objective Information Retrieval-Based NSGA-II Optimization for Requirements Traceability Recovery," 2020 IEEE International Conference on Electro Information Technology (EIT), Chicago, IL, USA, 2020, pp. 271-280, doi: 10.1109/EIT48999.2020.9208233.

> Abstract: The recovery of requirements traceability links assists multiple software development activities, including analyzing impact of changes, validating requirements, maintenance and evolution of software. Poor communication and time pressure problems often have resulted in organizations being unsuccessful in implementing traceability practices. A number of solutions for automatic recovery of links across software artifacts have been developed, including the application of textual analysis and information retrieval techniques. Link recovery with information retrieval has seen some success, but there is a need for methods that further enable the automatic generation of links. We investigate the requirements traceability recovery problem as an optimization problem; we study it as a multi-objective search in which we assign one-to-many relationships between each requirement to a code element by considering the semantic similarity between the textual content of requirements and the source code. We adapt the Non-dominated Sorting Genetic Algorithm, NSGA-II, to seek the optimal solution between two objective functions based on textual similarity using the similarity measures of Jaccard similarity and term frequency (TF-idf) combined with cosine similarity. Our IR-based NSGA-II returned values on average above 75% for both precision and recall for the recovered links. High precision and recall are positive results for our approach to requirements traceability recovery. 

URL: https://doi.org/10.1109/EIT48999.2020.9208233