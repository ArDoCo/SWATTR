import entity.Document;
import entity.VerbObjectPhrase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DocumentTest {

    private Document testDocument = null;

    private Document testDocument(){

        if((this.testDocument == null)){
            Document testDoc = new Document("randomPath");
            String testString = "Tendrils of coldness stretched from its home within her chest, testing her strength before subsiding into stillness once more.";
            List<String> testWords = Arrays.asList(testString.split(" "));
            testDoc.addAllWordsToBag(testWords);

            for(String w: testWords){
                testDoc.addPhrase(new VerbObjectPhrase(w, w));
            }

            this.testDocument = testDoc;
        }

       return this.testDocument;
    }

    @Test
    public void tuningFactorMapCompletenessTest() {
        Document testDocument = testDocument();

        int expectedSize = testDocument.getDistinctVOPhrases().size() + testDocument.getDistinctWords().size();
        Assert.assertEquals(expectedSize, testDocument.getTuningFactors().keySet().size());

        for(String w: testDocument.getBagOfWords()){
            Assert.assertTrue(testDocument.getTuningFactors().containsKey(w.hashCode()));
        }

        for(VerbObjectPhrase vop: testDocument.getDistinctVOPhrases()){
            Assert.assertTrue(testDocument.getTuningFactors().containsKey(vop.hashCode()));
        }
    }

    @Test
    public void testUpdateTuningFactor(){
        Document testDocument = testDocument();
        VerbObjectPhrase testPhrase = new VerbObjectPhrase("take", "this");
        String testWord = "fish";

        testDocument.addWordToBag(testWord);
        Assert.assertEquals(testDocument.getDefaultWordTuningFactor(), testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addWordToBag(testWord, 0.5);
        Assert.assertEquals(testDocument.getDefaultWordTuningFactor(), testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addWordToBag(testWord, 1.1);
        Assert.assertEquals(1.1, testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase);
        Assert.assertEquals(testDocument.getDefaultVOPhraseTuningFactor(), testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase, 0.5);
        Assert.assertEquals(testDocument.getDefaultVOPhraseTuningFactor(), testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase, 5.0);
        Assert.assertEquals(5.0, testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);
    }

}
