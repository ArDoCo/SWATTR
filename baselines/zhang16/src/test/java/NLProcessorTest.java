
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.mit.jwi.item.POS;
import edu.stanford.nlp.pipeline.Annotation;
import entity.Identifier;
import entity.IdentifierType;
import entity.VerbObjectPhrase;
import process.NLProcessor;

public class NLProcessorTest {

    private NLProcessor nlp;

    private NLProcessor nlProcessor() {
        if (nlp == null) {
            try {
                nlp = new NLProcessor("./src/main/resources/wordnet-database/dict");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return nlp;
    }

    @Test
    public void getVoPhrases_properExtraction() {

        String testString1 = "The user can download and edit files.";
        String testString2 = "download and upload";
        String testString3 = "Peter eats cake and slurps tea.";

        Set<VerbObjectPhrase> result = nlProcessor().getVoPhrases(new Annotation(testString2));

        Assertions.assertEquals(2, nlProcessor().getVoPhrases(new Annotation(testString1)).size());
        Assertions.assertEquals(0, nlProcessor().getVoPhrases(new Annotation(testString2)).size());
        Assertions.assertEquals(2, nlProcessor().getVoPhrases(new Annotation(testString3)).size());

    }

    @Test
    public void getStemmedVoPhrases_properExtracionAndStemming() {
        String testString1 = "The user is downloading files.";
        String testString2 = "Peter eats cake and slurps tea.";

        VerbObjectPhrase expected1 = new VerbObjectPhrase("download", "file");
        VerbObjectPhrase expected2 = new VerbObjectPhrase("slurp", "tea");

        List<VerbObjectPhrase> result1 = nlProcessor().getStemmedVOPhrases(testString1);
        List<VerbObjectPhrase> result2 = nlProcessor().getStemmedVOPhrases(testString2);

        Assertions.assertTrue(result1.contains(expected1));
        Assertions.assertTrue(result2.contains(expected2));

    }

    @Test
    public void getStemmedBagOfWords_properStemming() {
        String testString1 = "edited";
        String testString2 = "opening the window leads to an critical error.";

        List<String> result1 = nlProcessor().getBagOfStemmedWords(testString1);
        List<String> result2 = nlProcessor().getBagOfStemmedWords(testString2);

        Assertions.assertTrue(result1.contains("edit"));
        Assertions.assertTrue(result2.contains("open"));
    }

    @Test
    public void getStemmedBagOfWords_ignoreIrrelevantWords() {
        String testString1 = "The administrator and the user.";
        List<String> result1 = nlProcessor().getBagOfStemmedWords(testString1);

        Assertions.assertFalse(result1.contains("the"));
        Assertions.assertFalse(result1.contains("The"));
        Assertions.assertFalse(result1.contains("and"));
    }

    @Test
    public void stem_keepUnstemmedWordIfNoStemFound() {
        String testString1 = "monkas";
        Assertions.assertTrue(nlProcessor().stem(testString1, POS.ADJECTIVE).contains("monkas"));
    }

    @Test
    public void stem_stemUnknownType() {
        String testString1 = "women";
        Assertions.assertTrue(nlProcessor().stem(testString1).contains("woman"));
    }

    @Test
    public void stemIdentifier_properSplittingAndStemming() {
        Identifier testIdentifier = new Identifier(IdentifierType.OTHER, "downloadFilesAndConfiguration");
        Set<String> result = nlProcessor().stemIdentifier(testIdentifier);
        Assertions.assertTrue(result.size() == 3);
        Assertions.assertTrue(result.contains("download") && result.contains("file") && result.contains("configuration"));
    }

    @Test
    public void splitIdentifierName_properSplitting() {
        String testString1 = "thatIsWhatSheSaid";
        Assertions.assertEquals(nlProcessor().splitIdentifierName(testString1), "that is what she said");
    }

    @Test
    public void splitIdentifierName_multipleUpperCases() {
        String testString1 = "saveCSVToFile";
        String result = nlProcessor().splitIdentifierName(testString1);
        Assertions.assertTrue(result.split(" ").length < 5);
    }
}
