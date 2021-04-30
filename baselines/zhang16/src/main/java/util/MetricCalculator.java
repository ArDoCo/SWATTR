package util;

import entity.Document;
import entity.VerbObjectPhrase;

import java.util.Collection;
import java.util.Map;

public class MetricCalculator {

    public static double tf(Document document, VerbObjectPhrase phrase){
        double occ = document.countOccurrence(phrase);
        double totalNumPhrases = document.getVoPhrases().size();
        if(totalNumPhrases == 0){
            return 0.0;
        }

        return (occ / totalNumPhrases);
    }

    public static double tf(Document document, String word){
        double occ = document.countOccurrence(word);
        double totalNumWords = document.getBagOfWords().size();
        if(totalNumWords == 0){
            return 0.0;
        }
        return (occ / totalNumWords);
    }

    public static double idf(Collection<Document> documents, VerbObjectPhrase term){
        int occ = 0;
        for(Document d: documents){
            occ += d.hasPhrase(term)? 1 : 0;
        }
        if(occ==0){
            return 0.0;
        }
        return Math.log((double)documents.size()/(double)occ);
    }

    public static double idf(Collection<Document> documents, String word){
        int occ = 0;
        for(Document d: documents){
            occ += d.hasWord(word)? 1 : 0;
        }
        if(occ==0){
            return 0.0;
        }
        return Math.log((double)documents.size()/(double)occ);
    }

    public static double calcSim(Document doc1, Document doc2){

        Map<Integer, Double> v1 = doc1.getMergedWeightVector();
        Map<Integer, Double> v2 = doc2.getMergedWeightVector();

        double dotProduct = 0.0;
        double euclNorm1 = 0.0;
        double euclNorm2 = 0.0;

        for(Integer i: v1.keySet()){
            dotProduct += (v1.get(i) * v2.get(i));
            euclNorm1 += Math.pow(v1.get(i), 2);
            euclNorm2 += Math.pow(v2.get(i), 2);
        }

        euclNorm1 = Math.sqrt(euclNorm1);
        euclNorm2 = Math.sqrt(euclNorm2);

        if((euclNorm1 * euclNorm2)>0.0) {
            return (dotProduct / (euclNorm1 * euclNorm2));
        }
        return 0.0;

    }

}
