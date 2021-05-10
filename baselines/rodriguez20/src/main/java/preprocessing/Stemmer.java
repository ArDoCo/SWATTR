package preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.TaggedWord;

public class Stemmer {

    private static Dictionary dict;
    static {
        try {
            File dir = copyData();
            dict = new Dictionary(dir);
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

    private static File copyData() {
        Object loader = new Object() {
        };
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File target = new File(tmp + File.separator + "swattr-rodriguez20-data");
        target.mkdirs();
        final String base = "dict";
        Set<String> files = new Reflections(base, new ResourcesScanner()).getResources(p -> true);
        try {
            for (String file : files) {
                String path = file.substring(base.length());
                FileUtils.copyURLToFile(loader.getClass().getResource("/" + file), new File(target + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Copied files to " + target.getAbsolutePath());
        return target;
    }
}
