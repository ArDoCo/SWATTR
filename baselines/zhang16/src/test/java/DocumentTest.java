import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import entity.Document;
import entity.VerbObjectPhrase;

class DocumentTest {

    private Document testDocument = null;

    private Document testDocument() {

        if ((testDocument == null)) {
            Document testDoc = new Document("randomPath");
            String testString = "Tendrils of coldness stretched from its home within her chest, testing her strength before subsiding into stillness once more.";
            List<String> testWords = Arrays.asList(testString.split(" "));
            testDoc.addAllWordsToBag(testWords);

            for (String w : testWords) {
                testDoc.addPhrase(new VerbObjectPhrase(w, w));
            }

            testDocument = testDoc;
        }

        return testDocument;
    }

    @Test
    void tuningFactorMapCompletenessTest() {
        Document testDocument = testDocument();

        int expectedSize = testDocument.getDistinctVOPhrases().size() + testDocument.getDistinctWords().size();
        Assertions.assertEquals(expectedSize, testDocument.getTuningFactors().keySet().size());

        for (String w : testDocument.getBagOfWords()) {
            Assertions.assertTrue(testDocument.getTuningFactors().containsKey(w.hashCode()));
        }

        for (VerbObjectPhrase vop : testDocument.getDistinctVOPhrases()) {
            Assertions.assertTrue(testDocument.getTuningFactors().containsKey(vop.hashCode()));
        }
    }

    @Test
    void testUpdateTuningFactor() {
        Document testDocument = testDocument();
        VerbObjectPhrase testPhrase = new VerbObjectPhrase("take", "this");
        String testWord = "fish";

        testDocument.addWordToBag(testWord);
        Assertions.assertEquals(testDocument.getDefaultWordTuningFactor(), testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addWordToBag(testWord, 0.5);
        Assertions.assertEquals(testDocument.getDefaultWordTuningFactor(), testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addWordToBag(testWord, 1.1);
        Assertions.assertEquals(1.1, testDocument.getTuningFactors().get(testWord.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase);
        Assertions.assertEquals(testDocument.getDefaultVOPhraseTuningFactor(), testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase, 0.5);
        Assertions.assertEquals(testDocument.getDefaultVOPhraseTuningFactor(), testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);

        testDocument.addPhrase(testPhrase, 5.0);
        Assertions.assertEquals(5.0, testDocument.getTuningFactors().get(testPhrase.hashCode()), 0.0);
    }

}
