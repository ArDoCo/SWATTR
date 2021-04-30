package preprocessing;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.javatuples.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class ObjFuncsOfSingleWordsCalculator {

	private Table<Integer, String, Double> tfIdfTable;
	public Table<Integer, Integer, Double> calculateJaccardiOfDirectories(
			List<Pair<Integer, List<Pair<Integer, String[]>>>> reqFileList,
			List<Pair<Integer, String[]>> srcCodeFilesContent) {

		Table<Integer, Integer, Double> jaccardiRelations;
		jaccardiRelations = HashBasedTable.create();
		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();

		for (Pair<Integer, List<Pair<Integer, String[]>>> file : reqFileList) {
			for (Pair<Integer, String[]> requirement : file.getValue1()) {
				String reqAsString = stringArrayToString(requirement.getValue1());
				for (Pair<Integer, String[]> sourceCodeOfFile : srcCodeFilesContent) {
					String srcCodeAsString = stringArrayToString(sourceCodeOfFile.getValue1());
					double jaccardiSimilarity = jaccardSimilarity.apply(reqAsString, srcCodeAsString);
					jaccardiRelations.put(requirement.getValue0(), sourceCodeOfFile.getValue0(), jaccardiSimilarity);
				}
			}
		}

		return jaccardiRelations;
	}

	public Table<Integer, String, Double> computeTfidf(
			List<Pair<Integer, List<Pair<Integer, String[]>>>> reqFileList) {
		Table<Integer, String, Double> tfTable = computeTermFrequency(reqFileList);
		Map<String, Double> idfMap = computeInverseDocumentFrequency(reqFileList);

		tfIdfTable = HashBasedTable.create();

		for (String key : tfTable.columnKeySet()) {
			for (int fileNo : tfTable.rowKeySet()) {
				if (tfTable.get(fileNo, key) != null) {
					double tfidf = tfTable.get(fileNo, key) * idfMap.get(key);
					tfIdfTable.put(fileNo, key, tfidf);
				}
			}
		}
		return tfIdfTable;
	}

	private HashMap<String, Double> computeInverseDocumentFrequency(
			List<Pair<Integer, List<Pair<Integer, String[]>>>> reqFileList) {
		HashMap<String, Integer> documentFrequencyMap = new HashMap<String, Integer>();
		ArrayList<String> alreadyProcessedWords = new ArrayList<String>();
		boolean foundInCurrentFile = false;

		for (int i = 0; i < reqFileList.size(); i++) {

			for (Pair<Integer, String[]> requirement : reqFileList.get(i).getValue1()) {
				String[] splittedReq = requirement.getValue1();
				for (String singleWord : splittedReq) {

					if (!isWordAlreadyProcessed(alreadyProcessedWords, singleWord)) {

						for (int j = i; j < reqFileList.size(); j++) {
							for (Pair<Integer, String[]> currentReq : reqFileList.get(j).getValue1()) {
								String[] currentReqSplitUp = currentReq.getValue1();
								foundInCurrentFile = false;
								for (String compareWord : currentReqSplitUp) {
									String compareWordLowerCase = compareWord.toLowerCase();
									if (compareWordLowerCase.equals(singleWord)) {
										foundInCurrentFile = true;
										boolean isKeyPresent = false;
										for (String key : documentFrequencyMap.keySet()) {

											if (key.equals(singleWord)) {
												isKeyPresent = true;
											}
										}

										if (isKeyPresent) {
											documentFrequencyMap.put(singleWord,
													documentFrequencyMap.get(singleWord) + 1);
										} else {
											documentFrequencyMap.put(singleWord, 1);
										}
										break;
									}

								}
								if (foundInCurrentFile) {
									foundInCurrentFile = false;
									break;
								}
							}
							if (foundInCurrentFile) {
								foundInCurrentFile = false;
								break;
							}
						}
						alreadyProcessedWords.add(singleWord);
					}
				}
			}
		}

		HashMap<String, Double> idfResultMap = new HashMap<String, Double>();

		for (String dfcounterName : documentFrequencyMap.keySet()) {

			int filesWithWord = documentFrequencyMap.get(dfcounterName);
			idfResultMap.put(dfcounterName, (double) reqFileList.size() / (double) filesWithWord);
		}

		return idfResultMap;
	}

	private boolean isWordAlreadyProcessed(ArrayList<String> listOfProcessedWords, String wordToCheck) {
		for (String word : listOfProcessedWords) {

			if (word.equalsIgnoreCase(wordToCheck)) {
				return true;
			}
		}
		return false;
	}

	private Table<Integer, String, Double> computeTermFrequency(
			List<Pair<Integer, List<Pair<Integer, String[]>>>> reqFileList) {

		Table<Integer, String, Double> tfTable = HashBasedTable.create();

		for (Pair<Integer, List<Pair<Integer, String[]>>> reqFile : reqFileList) {
			ArrayList<String> alreadyProcessedwordsInFile = new ArrayList<String>();

			int totalNrOfWordsInFile = 0;

			for (Pair<Integer, String[]> wordcounter : reqFile.getValue1()) {
				totalNrOfWordsInFile = totalNrOfWordsInFile + wordcounter.getValue1().length;
			}

			for (Pair<Integer, String[]> requirement : reqFile.getValue1()) {
				String[] singleWords = requirement.getValue1();

				for (int i = 0; i < singleWords.length; i++) {

					if (!alreadyProcessedwordsInFile.contains(singleWords[i])) {
						tfTable.put(reqFile.getValue0(), singleWords[i], countOccurence(singleWords[i], singleWords));
					} else {
						tfTable.put(reqFile.getValue0(), singleWords[i],
								tfTable.get(requirement.getValue0(), singleWords[i])
										+ countOccurence(singleWords[i], singleWords));
					}
				}

				for (int k = 0; k < singleWords.length; k++) {
					tfTable.put(reqFile.getValue0(), singleWords[k],
							tfTable.get(reqFile.getValue0(), singleWords[k]) / (double) totalNrOfWordsInFile);
				}
			}
		}
		return tfTable;
	}

	private String stringArrayToString(String[] inputStringArray) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inputStringArray.length; i++) {
			sb.append(inputStringArray[i]);
		}
		String resultString = sb.toString();
		return resultString;
	}

	private double countOccurence(String wordToCount, String[] stringArray) {
		double wordCount = 0.0;
		for (int i = 0; i < stringArray.length; i++) {
			if (wordToCount.equals(stringArray[i])) {
				wordCount++;
			}
		}

		return wordCount;
	}

	public Table<Integer, String, Double> getTfIdfTable() {
		return tfIdfTable;
	}

}
