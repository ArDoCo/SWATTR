package entity;

import java.util.*;

public class Document {

    private String name;

    private List<VerbObjectPhrase> voPhrases;
    private List<String> bagOfWords;

    private Map<Integer, Double> verbObjectWeightVector;
    private Map<Integer, Double> wordWeightVector;
    private Map<Integer, Double> tuningFactors;

    private double defaultVOPhraseTuningFactor = 3.0;
    private double defaultWordTuningFactor = 1.0;

    public Document(String name) {
        this.voPhrases = new ArrayList<>();
        this.name = name;
        this.bagOfWords = new ArrayList<>();
        this.tuningFactors = new HashMap<>();
    }

    public double getTuningFactor(VerbObjectPhrase phrase){
        return tuningFactors.get(phrase.hashCode());
    }

    public double getTuningFactor(String word){
        return tuningFactors.get(word.hashCode());
    }

    public void addPhrase(VerbObjectPhrase phrase){
        voPhrases.add(phrase);
        updateTuningFactor(phrase.hashCode(), defaultVOPhraseTuningFactor);
    }

    public void addPhrase(VerbObjectPhrase phrase, double tuningFactor){
        voPhrases.add(phrase);
        updateTuningFactor(phrase.hashCode(), tuningFactor);
    }

    public void addAllPhrases(Collection<VerbObjectPhrase> phrases){
        voPhrases.addAll(phrases);
        for(VerbObjectPhrase p: phrases){
            updateTuningFactor(p.hashCode(), defaultVOPhraseTuningFactor);
        }
    }

    public void addAllPhrases(Collection<VerbObjectPhrase> phrases, double tuningFactor){
        voPhrases.addAll(phrases);
        for(VerbObjectPhrase p: phrases){
            updateTuningFactor(p.hashCode(), tuningFactor);
        }
    }

    private void updateTuningFactor(Integer hashCode, double tuningFactor){
        if(tuningFactors.containsKey(hashCode)){
            if(tuningFactors.get(hashCode)<tuningFactor){
                tuningFactors.put(hashCode, tuningFactor);
            }
        } else {
            tuningFactors.put(hashCode, tuningFactor);
        }
    }

    public List<VerbObjectPhrase> getVoPhrases() {
        return voPhrases;
    }

    public void addAllWordsToBag(Collection<String> words){
        this.bagOfWords.addAll(words);
        for(String w: words){
            updateTuningFactor(w.hashCode(), defaultWordTuningFactor);
        }
    }

    public void addAllWordsToBag(Collection<String> words, double tuningFactor){
        this.bagOfWords.addAll(words);
        for(String w: words){
            updateTuningFactor(w.hashCode(), tuningFactor);
        }
    }

    public void addWordToBag(String word){
        this.bagOfWords.add(word);
        updateTuningFactor(word.hashCode(), defaultWordTuningFactor);
    }

    public void addWordToBag(String word, double tuningFactor){
        this.bagOfWords.add(word);
        updateTuningFactor(word.hashCode(), tuningFactor);
    }

    public Set<VerbObjectPhrase> getDistinctVOPhrases(){
        Set<VerbObjectPhrase> dPhrases = new HashSet<>();
        dPhrases.addAll(this.voPhrases);
        return dPhrases;
    }

    public Set<String> getDistinctWords(){
        Set<String> dWords = new HashSet<>();
        dWords.addAll(this.bagOfWords);
        return dWords;
    }

    public int countOccurrence(VerbObjectPhrase phrase){
        int occ = 0;
        for(VerbObjectPhrase p: voPhrases){
            if(phrase.equals(p)){
                occ++;
            }
        }
        return occ;
    }

    public int countOccurrence(String word) {
        int occ = 0;
        for(String w: bagOfWords){
            if(word.equals(w)){
                occ++;
            }
        }
        return occ;
    }

    public double getDefaultVOPhraseTuningFactor() {
        return defaultVOPhraseTuningFactor;
    }

    public double getDefaultWordTuningFactor() {
        return defaultWordTuningFactor;
    }

    public Map<Integer, Double> getMergedWeightVector(){
        Map<Integer, Double> vector = new HashMap<>();
        vector.putAll(verbObjectWeightVector);
        vector.putAll(wordWeightVector);
        return  vector;
    }

    public Map<Integer, Double> getTuningFactors() {
        return tuningFactors;
    }

    public void setVerbObjectWeightVector(Map<Integer, Double> vector){
        this.verbObjectWeightVector = vector;
    }
    public void setWordWeightVector(Map<Integer, Double> vector){
        this.wordWeightVector = vector;
    }

    public Map<Integer, Double> getVerbObjectWeightVector() {
        return verbObjectWeightVector;
    }

    public Map<Integer, Double> getWordWeightVector() {
        return wordWeightVector;
    }

    public List<String> getBagOfWords() {
        return bagOfWords;
    }

    public boolean hasPhrase(VerbObjectPhrase phrase){
        return voPhrases.contains(phrase);
    }

    public boolean hasWord(String word){
        return bagOfWords.contains(word);
    }

    public String getName() {
        return name;
    }

    public boolean updateVerbObjectWeight(int hash, double value){
        if (verbObjectWeightVector.containsKey(hash)){
            verbObjectWeightVector.put(hash, value);
            return true;
        }
        return false;
    }

    public boolean updateWordWeight(int hash, double value){
        if (wordWeightVector.containsKey(hash)){
            wordWeightVector.put(hash, value);
            return true;
        }
        return false;
    }
}
