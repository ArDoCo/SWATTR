package preprocessing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {
    private Object loader = new Object() {
    };
    private MaxentTagger tagger = new MaxentTagger(loader.getClass().getResourceAsStream("/tagger/models/english-left3words-distsim.tagger"));

    public List<TaggedWord> tagging(String inputString) {
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(inputString));

        List<TaggedWord> taggedWords = new ArrayList<>();
        for (List<HasWord> sentence : sentences) {
            taggedWords.addAll(tagger.tagSentence(sentence));
        }
        return taggedWords;
    }
}
