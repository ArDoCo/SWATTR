
import entity.Document;
import entity.VerbObjectPhrase;
import org.junit.Assert;
import org.junit.Test;
import util.MetricCalculator;

import java.util.ArrayList;
import java.util.List;

public class MetricCalculatorTest {

    public List<Document> generateIdenticalDocsWithDistinctWords(int numDocs, int numTerms){
        List<Document> testDocs = new ArrayList<>();
        for(int i=0; i<numDocs; i++){
            Document testDoc = new Document("testDoc"+i);
            for(int j=0; j<numTerms; j++){
                testDoc.addWordToBag("word"+j);
                testDoc.addPhrase(new VerbObjectPhrase("verb"+j, "obj"+j));
            }
            testDocs.add(testDoc);
        }
        return testDocs;
    }

    public List<Document> generateDisjointDocuments(int numDocs, int numTerms){
        List<Document> testDocs = new ArrayList<>();
        for(int i=0; i<numDocs; i++){
            Document testDoc = new Document("testDoc"+i);
            for(int j=0; j<numTerms; j++){
                testDoc.addWordToBag("word"+i+"-"+j);
                testDoc.addPhrase(new VerbObjectPhrase("verb"+i+"-"+j, "obj"+i+"-"+j));
            }
            testDocs.add(testDoc);
        }
        return testDocs;
    }


    @Test
    public void tfTest(){
        Document testDoc = generateIdenticalDocsWithDistinctWords(1,20).get(0);

        for(int j=0; j<20; j++){
            String word = "word"+j;
            Assert.assertEquals(0.05, MetricCalculator.tf(testDoc, word), 0);
        }
    }

    @Test
    public void tfWordNotExistsTest(){
        Document testDoc = generateIdenticalDocsWithDistinctWords(1,20).get(0);

        Assert.assertEquals(0.0, MetricCalculator.tf(testDoc, "testWordX"), 0);
    }

    @Test
    public void idfMinTest(){
        List<Document> testDocs = generateIdenticalDocsWithDistinctWords(20,20);

        for(int j=0; j<20; j++){
            String word = "word"+j;
            Assert.assertEquals(0.0, MetricCalculator.idf(testDocs, word), 0.0);
        }


    }

    @Test
    public void idfMaxTest(){

        List<Document> testDocs = generateDisjointDocuments(20,20);

        double expected =  Math.log(20.0);

        for(int i=0; i<20; i++) {
            for (int j = 0; j < 20; j++) {
                String word = "word" +i+"-"+j;
                Assert.assertEquals(expected, MetricCalculator.idf(testDocs, word), 0.0);
            }
        }

    }

    @Test
    public void idfWordNotExistsTest(){
        List<Document> testDocs = generateIdenticalDocsWithDistinctWords(5,5);

        Assert.assertEquals(0.0, MetricCalculator.idf(testDocs, "testWordX"), 0);
    }


}
