package preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.TaggedWord;

public class Stemmer {

    // For some reasons, getResource bugs for me and does not find the file. Therefore, this explicit path for now.
    private static final String DICTIONARY_PATH = "src/main/resources/dict";
    private static Dictionary dict = new Dictionary(new File(DICTIONARY_PATH));
    static {
        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] stemming(List<TaggedWord> taggedWords) {
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        List<String> stemmedWords = new ArrayList<>();
        for (TaggedWord taggedWord : taggedWords) {
            String currentTag = taggedWord.tag();
            if (currentTag.contentEquals("NN") || currentTag.contentEquals("NNS") || currentTag.contentEquals("NNP")) {
                stemmedWords.addAll(stemmer.findStems(taggedWord.word(), POS.NOUN));
            } else if (currentTag.contentEquals("JJ")) {
                stemmedWords.addAll(stemmer.findStems(taggedWord.word(), POS.ADJECTIVE));
            } else if (currentTag.contentEquals("VBN") || currentTag.contentEquals("VBG")) {
                stemmedWords.addAll(stemmer.findStems(taggedWord.word(), POS.VERB));
            } else if (currentTag.contentEquals("RB")) {
                stemmedWords.addAll(stemmer.findStems(taggedWord.word(), POS.ADVERB));
            }
        }
        String[] finalString = new String[stemmedWords.size()];
        for (int i = 0; i < finalString.length; i++) {
            finalString[i] = stemmedWords.get(i);
        }
        return finalString;
    }
}
