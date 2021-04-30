package preprocessing;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {
    private MaxentTagger tagger = new MaxentTagger(String.valueOf(Paths.get("src/main/resources/tagger/models/english-left3words-distsim.tagger")));

    public List<TaggedWord> tagging(String inputString) {
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(inputString));

        List<TaggedWord> taggedWords = new ArrayList<>();
        for (List<HasWord> sentence : sentences) {
            taggedWords.addAll(tagger.tagSentence(sentence));
        }
        return taggedWords;
    }
}
