
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import entity.Document;
import entity.VerbObjectPhrase;
import util.MetricCalculator;

class MetricCalculatorTest {

    List<Document> generateIdenticalDocsWithDistinctWords(int numDocs, int numTerms) {
        List<Document> testDocs = new ArrayList<>();
        for (int i = 0; i < numDocs; i++) {
            Document testDoc = new Document("testDoc" + i);
            for (int j = 0; j < numTerms; j++) {
                testDoc.addWordToBag("word" + j);
                testDoc.addPhrase(new VerbObjectPhrase("verb" + j, "obj" + j));
            }
            testDocs.add(testDoc);
        }
        return testDocs;
    }

    List<Document> generateDisjointDocuments(int numDocs, int numTerms) {
        List<Document> testDocs = new ArrayList<>();
        for (int i = 0; i < numDocs; i++) {
            Document testDoc = new Document("testDoc" + i);
            for (int j = 0; j < numTerms; j++) {
                testDoc.addWordToBag("word" + i + "-" + j);
                testDoc.addPhrase(new VerbObjectPhrase("verb" + i + "-" + j, "obj" + i + "-" + j));
            }
            testDocs.add(testDoc);
        }
        return testDocs;
    }

    @Test
    void tfTest() {
        Document testDoc = generateIdenticalDocsWithDistinctWords(1, 20).get(0);

        for (int j = 0; j < 20; j++) {
            String word = "word" + j;
            Assertions.assertEquals(0.05, MetricCalculator.tf(testDoc, word), 0);
        }
    }

    @Test
    void tfWordNotExistsTest() {
        Document testDoc = generateIdenticalDocsWithDistinctWords(1, 20).get(0);

        Assertions.assertEquals(0.0, MetricCalculator.tf(testDoc, "testWordX"), 0);
    }

    @Test
    void idfMinTest() {
        List<Document> testDocs = generateIdenticalDocsWithDistinctWords(20, 20);

        for (int j = 0; j < 20; j++) {
            String word = "word" + j;
            Assertions.assertEquals(0.0, MetricCalculator.idf(testDocs, word), 0.0);
        }

    }

    @Test
    void idfMaxTest() {

        List<Document> testDocs = generateDisjointDocuments(20, 20);

        double expected = Math.log(20.0);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                String word = "word" + i + "-" + j;
                Assertions.assertEquals(expected, MetricCalculator.idf(testDocs, word), 0.0);
            }
        }

    }

    @Test
    void idfWordNotExistsTest() {
        List<Document> testDocs = generateIdenticalDocsWithDistinctWords(5, 5);

        Assertions.assertEquals(0.0, MetricCalculator.idf(testDocs, "testWordX"), 0);
    }

}
