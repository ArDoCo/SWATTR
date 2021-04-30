# Reimplementation of the approach by Zhang et al. (2016) for TR between Text and (Palladio Component) Models

This is a reimplementation of the mentioned paper (see below) for the use case of creating tracelinks between Software Architecture Documentation texts and Models in the form of Palladio Component Models.

The reimplementation was done by Janek Speit, adaptations were performed by Jan Keim.

## How to use
The programs needs a few parameters to operate correctly:
1. Path to the input text document
2. Path to repository-file (file ending ".repository") of a PCM 

An example execution may contain the following parameters: <br>
`"../../case_studies/teammates/text/TeammatesForEvalText.txt" "../../case_studies/teammates/model/teammates.repository""`

The output will be saved as CSV files within the [output-folder](./output).

## Original paper
> Y. Zhang, C. Wan and B. Jin, "An empirical study on recovering requirement-to-code links," 2016 17th IEEE/ACIS International Conference on Software Engineering, Artificial Intelligence, Networking and Parallel/Distributed Computing (SNPD), Shanghai, China, 2016, pp. 121-126, doi: 10.1109/SNPD.2016.7515889.

> Abstract: Requirements traceability provides support for critical software engineering activities such as change impact analysis and requirements validation. Unfortunately many organizations have ineffective traceability practices in place, largely because of poor communication and time pressure problems. Therefore researchers have proposed various approaches to automatically recover requirement-to-code links. Typically, these approaches are based on Information Retrieval techniques, and use various features such as synonyms, verb-object phrases, and structural information. Although many links are thus recovered, the effectiveness of individual features is not fully evaluated, and it is rather difficult to combine different features to produce better results. In this paper, we implement a tool, called R2C, that combines various features to recover requirement-to-code links. With the support of R2C, we conduct an empirical study to understand the effectiveness of these features in recovering requirement-to-code links. Our results show that verb-object phrase is the most effective feature in recovering such links. A preliminary case study indicates that our tuning combines different features to produce better results than IR-based technique using a single feature. 

> URL: https://doi.org/10.1109/SNPD.2016.7515889