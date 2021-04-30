package process;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import entity.Identifier;
import entity.VerbObjectPhrase;

public class NLProcessor {

    private Dictionary dict;

    public NLProcessor(String dictionaryPath) throws MalformedURLException {
        dict = dictionary(dictionaryPath);
    }

    public NLProcessor(File dictionary) throws MalformedURLException {
        dict = dictionary(dictionary);
    }

    private Dictionary dictionary(String dictionaryPath) throws MalformedURLException {
        URL url = new File(dictionaryPath).toURI().toURL();
        return new Dictionary(url);
    }

    private Dictionary dictionary(File dictionary) throws MalformedURLException {
        URL url = dictionary.toURI().toURL();
        return new Dictionary(url);
    }

    public List<String> stem(String term, POS type) {

        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WordnetStemmer stemmer = new WordnetStemmer(dict);

        List<String> stems = stemmer.findStems(term, type);

        List<String> result = new ArrayList<>(new HashSet<>(stems));

        if (result.isEmpty()) {
            result.add(term);
        }

        return result;
    }

    public List<String> stem(String term) {

        POS[] types = new POS[] { POS.NOUN, POS.ADVERB, POS.ADJECTIVE, POS.ADVERB };

        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WordnetStemmer stemmer = new WordnetStemmer(dict);

        List<String> stems = new ArrayList<>();
        for (POS type : types) {
            stems.addAll(stemmer.findStems(term, type));
        }

        return new ArrayList<>(new HashSet<>(stems));
    }

    public List<String> stemAll(Collection<String> words) {
        List<String> stemmedWords = new ArrayList<>();
        for (String word : words) {
            stemmedWords.addAll(stem(word));
        }
        return stemmedWords;
    }

    private Annotation annotateDocument(Annotation doc) {
        // Create the Stanford CoreNLP pipeline
        Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate an example document.
        pipeline.annotate(doc);
        return doc;
    }

    private CoreDocument getPOSTaggedDocument(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        CoreDocument document = pipeline.processToCoreDocument(text);

        return document;
    }

    private POS toPOS(String tag) {
        if (tag.length() < 2) {
            return null;
        }

        if (tag.substring(0, 2).equals("NN")) {
            return POS.NOUN;
        }

        if (tag.substring(0, 2).equals("VB")) {
            return POS.VERB;
        }

        if (tag.substring(0, 2).equals("JJ")) {
            return POS.ADJECTIVE;
        }

        if (tag.substring(0, 2).equals("RB")) {
            return POS.ADVERB;
        }

        return null;
    }

    public List<String> extractIdentifierFromText(String text) {
        List<String> words = Arrays.asList(text.split(" "));
        List<String> idents = new ArrayList<>();

        Pattern p = Pattern.compile("\\B[A-Z]\\B");

        for (String w : words) {
            // if there is an upper case in the middle of the word, it is likely that this word describes an identifier
            Matcher m = p.matcher(w);
            if (m.find()) {
                idents.add(w);
            }
        }
        return idents;
    }

    public List<String> getBagOfStemmedWords(String text) {
        List<String> bagOfWords = new ArrayList<>();
        CoreDocument taggedDocument = getPOSTaggedDocument(text);

        for (CoreLabel tok : taggedDocument.tokens()) {
            POS pos = toPOS(tok.tag());
            String word = tok.word();
            if (pos != null) {
                List<String> stems = stem(word, pos);

                bagOfWords.addAll(stems);
            }
        }

        return bagOfWords;
    }

    public VerbObjectPhrase stemVOPhrase(VerbObjectPhrase voPhrase) {

        String sVerb = voPhrase.getVerb();
        String sObject = voPhrase.getObject();

        // get actual verb and object
        if (sVerb.contains(" ")) {
            CoreDocument tagged = getPOSTaggedDocument(sVerb);
            for (CoreLabel label : tagged.tokens()) {
                if (label.tag().substring(0, 2).equals("VB")) {
                    sVerb = label.word();
                }
            }
        }

        if (sObject.contains(" ")) {
            CoreDocument tagged = getPOSTaggedDocument(sObject);
            for (CoreLabel label : tagged.tokens()) {
                if (label.tag().substring(0, 2).equals("NN")) {
                    sObject = label.word();
                }
            }
        }

        // stem
        try {
            sVerb = stem(sVerb, POS.VERB).get(0);
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            sObject = stem(sObject, POS.NOUN).get(0);
        } catch (IndexOutOfBoundsException e) {
        }
        return new VerbObjectPhrase(sVerb, sObject);
    }

    private Set<VerbObjectPhrase> getVoPhrasesFromRelationTriple(Annotation doc) {
        Set<VerbObjectPhrase> voPhrases = new HashSet<>();
        Set<RelationTriple> triple = new HashSet<>();
        annotateDocument(doc);

        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            triple.addAll(triples);
        }

        for (RelationTriple t : triple) {
            voPhrases.add(new VerbObjectPhrase(t.relationGloss(), t.objectGloss()));
        }
        return voPhrases;
    }

    public Set<VerbObjectPhrase> getVoPhrases(Annotation doc) {
        Set<VerbObjectPhrase> voSet = new HashSet<>();

        annotateDocument(doc);

        int sentNo = 0;
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {

            // logging
            // System.out.println("Sentence #" + ++sentNo + ": " + sentence.get(CoreAnnotations.TextAnnotation.class));

            SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            List<IndexedWord> verbs = sg.getAllNodesByPartOfSpeechPattern("VB");
            verbs.addAll(sg.getAllNodesByPartOfSpeechPattern("VBD"));
            verbs.addAll(sg.getAllNodesByPartOfSpeechPattern("VBG"));
            verbs.addAll(sg.getAllNodesByPartOfSpeechPattern("VBN"));
            verbs.addAll(sg.getAllNodesByPartOfSpeechPattern("VBP"));
            verbs.addAll(sg.getAllNodesByPartOfSpeechPattern("VBZ"));

            for (IndexedWord v : verbs) {
                // find related nodes with obj relation
                LinkedList<IndexedWord> wordList = new LinkedList<>();
                wordList.add(v);
                IndexedWord relatedObj = getRelatedObj(wordList, sg);

                if (relatedObj != null) {
                    voSet.add(new VerbObjectPhrase(v.value(), relatedObj.value()));
                }
            }
        }

        // use openIE implementation if no phrases have been found
        if (voSet.isEmpty()) {
            voSet.addAll(getVoPhrasesFromRelationTriple(doc));
        }

        return voSet;
    }

    private IndexedWord getRelatedObj(LinkedList<IndexedWord> nextWords, SemanticGraph sg) {
        IndexedWord currentWord = nextWords.removeFirst();
        List<SemanticGraphEdge> outEdges = sg.getOutEdgesSorted(currentWord);

        // check if one of the edges is already the obj relation:
        for (SemanticGraphEdge o : outEdges) {
            if (o.getRelation().getShortName().equals("obj")) {
                return o.getTarget();
            }
        }

        // if no obj-relation found, add neighbors and go on with recursively
        for (SemanticGraphEdge o : outEdges) {
            nextWords.add(o.getTarget());
        }

        if (nextWords.isEmpty()) {
            return null;
        }

        return getRelatedObj(nextWords, sg);
    }

    public List<VerbObjectPhrase> getStemmedVOPhrases(String raw) {

        List<VerbObjectPhrase> voPhrases = new LinkedList<>();
        Set<VerbObjectPhrase> unstemmed = getVoPhrases(new Annotation(raw));

        for (VerbObjectPhrase voPhrase : unstemmed) {
            voPhrases.add(stemVOPhrase(voPhrase));
        }

        return voPhrases;
    }

    public String splitIdentifierName(String identifierName) {
        StringBuilder splittedName = new StringBuilder("" + identifierName.charAt(0));
        char pre = identifierName.toCharArray()[0];
        // split "makeSomething" --> "make Something"
        for (char c : identifierName.substring(1).toCharArray()) {

            if (Character.isUpperCase(c) && !(Character.isUpperCase(pre))) {
                splittedName.append(" ");
            }
            splittedName.append(c);
            pre = c;
        }

        splittedName = new StringBuilder(splittedName.toString().replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ").toLowerCase());

        return splittedName.toString();

    }

    public Set<String> stemIdentifier(Identifier identifier) {
        Set<String> stems = new HashSet<>();
        String split = splitIdentifierName(identifier.getName());
        stems.addAll(getBagOfStemmedWords(split));

        if (stems.isEmpty()) {
            List<String> words = Arrays.asList(split.split(" "));
            stems.addAll(stemAll(words));
        }

        return stems;
    }

}
