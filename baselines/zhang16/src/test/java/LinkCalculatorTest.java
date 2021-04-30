import entity.Document;
import entity.ModelEntityDocument;
import entity.RelationGraph;
import entity.VerbObjectPhrase;
import org.checkerframework.checker.signature.qual.DotSeparatedIdentifiersOrPrimitiveType;
import org.junit.Assert;
import org.junit.Test;
import process.LinkCalculator;

import java.util.ArrayList;
import java.util.List;

public class LinkCalculatorTest{

    private LinkCalculator lc;

    private LinkCalculator linkCalculator(){
        if(this.lc==null){
            this.lc = new LinkCalculator(0.25);
        }
        return this.lc;
    }

    public List<VerbObjectPhrase> generateTestVoPhrases(String word, int n){
        List<VerbObjectPhrase> testVoPhrases = new ArrayList<>();
        for( int i = 0; i<n; i++){
            testVoPhrases.add(new VerbObjectPhrase("verb_"+word + Integer.toString(i), "object_"+word+Integer.toString(i)));
        }
        return testVoPhrases;
    }

    public List<String> generateTestWords(String word, int n){
        List<String> testWords = new ArrayList<>();
        for( int i = 0; i<n; i++){
            testWords.add(word + Integer.toString(i));
        }
        return testWords;
    }

    @Test
    public void initVectors_properWordsWeightVectorInitialization_DistinctWords(){

        List<Document> testDocuments = new ArrayList<>();

        Document testDoc1 = new Document("testDoc1");
        testDoc1.addAllWordsToBag(generateTestWords("word1", 10));
        testDocuments.add(testDoc1);

        Document testDoc2 = new Document("testDoc2");
        testDoc1.addAllWordsToBag(generateTestWords("word2", 10));
        testDocuments.add(testDoc2);

        Document testDoc3 = new Document("testDoc3");
        testDoc1.addAllWordsToBag(generateTestWords("word3", 10));
        testDocuments.add(testDoc3);

        linkCalculator().initVectors(testDocuments);

        Assert.assertEquals(testDoc1.getWordWeightVector().keySet().size(), 30);

        for(String w: testDoc1.getBagOfWords()){
            Assert.assertTrue(testDoc1.getWordWeightVector().containsKey(w.hashCode()));
        }

        Assert.assertEquals(testDoc1.getWordWeightVector(), testDoc2.getWordWeightVector());
        Assert.assertEquals(testDoc2.getWordWeightVector(), testDoc3.getWordWeightVector());
        Assert.assertEquals(testDoc3.getWordWeightVector(), testDoc1.getWordWeightVector());

    }

    @Test
    public void initVectors_properWordsWeightVectorInitialization_OverlappingWords(){

        List<Document> testDocuments = new ArrayList<>();

        Document testDoc1 = new Document("testDoc1");
        testDoc1.addAllWordsToBag(generateTestWords("word1", 10));
        testDocuments.add(testDoc1);

        Document testDoc2 = new Document("testDoc2");
        testDoc1.addAllWordsToBag(generateTestWords("word2", 10));
        testDocuments.add(testDoc2);

        Document testDoc3 = new Document("testDoc3");
        testDoc1.addAllWordsToBag(generateTestWords("word1", 10));
        testDocuments.add(testDoc3);

        linkCalculator().initVectors(testDocuments);

        Assert.assertEquals(testDoc1.getWordWeightVector().keySet().size(), 20);

        for(String w: testDoc1.getBagOfWords()){
            Assert.assertTrue(testDoc1.getWordWeightVector().containsKey(w.hashCode()));
        }

        Assert.assertEquals(testDoc1.getWordWeightVector(), testDoc2.getWordWeightVector());
        Assert.assertEquals(testDoc2.getWordWeightVector(), testDoc3.getWordWeightVector());
        Assert.assertEquals(testDoc3.getWordWeightVector(), testDoc1.getWordWeightVector());

    }

    @Test
    public void initVectors_properVerbObjectWeightVectorInitialisation_OverlappinPhrases(){

        List<Document> testDocuments = new ArrayList<>();

        Document testDoc1 = new Document("testDoc1");
        testDoc1.addAllPhrases(generateTestVoPhrases("word1", 10));
        testDocuments.add(testDoc1);

        Document testDoc2 = new Document("testDoc2");
        testDoc1.addAllPhrases(generateTestVoPhrases("word2", 10));
        testDocuments.add(testDoc2);

        Document testDoc3 = new Document("testDoc3");
        testDoc1.addAllPhrases(generateTestVoPhrases("word1", 10));
        testDocuments.add(testDoc3);

        linkCalculator().initVectors(testDocuments);

        Assert.assertEquals(testDoc1.getVerbObjectWeightVector().keySet().size(), 20);

        for(String vop: testDoc1.getBagOfWords()){
            Assert.assertTrue(testDoc1.getVerbObjectWeightVector().containsKey(vop.hashCode()));
        }

        Assert.assertEquals(testDoc1.getVerbObjectWeightVector(), testDoc2.getVerbObjectWeightVector());
        Assert.assertEquals(testDoc2.getVerbObjectWeightVector(), testDoc3.getVerbObjectWeightVector());
        Assert.assertEquals(testDoc3.getVerbObjectWeightVector(), testDoc1.getVerbObjectWeightVector());

    }

    @Test
    public void initVectors_properVerbObjectWeightVectorInitialisation_DistinctPhrases(){

        List<Document> testDocuments = new ArrayList<>();

        Document testDoc1 = new Document("testDoc1");
        testDoc1.addAllPhrases(generateTestVoPhrases("word1", 10));
        testDocuments.add(testDoc1);

        Document testDoc2 = new Document("testDoc2");
        testDoc1.addAllPhrases(generateTestVoPhrases("word2", 10));
        testDocuments.add(testDoc2);

        Document testDoc3 = new Document("testDoc3");
        testDoc1.addAllPhrases(generateTestVoPhrases("word3", 10));
        testDocuments.add(testDoc3);

        linkCalculator().initVectors(testDocuments);

        Assert.assertEquals(testDoc1.getVerbObjectWeightVector().keySet().size(), 30);

        for(String vop: testDoc1.getBagOfWords()){
            Assert.assertTrue(testDoc1.getVerbObjectWeightVector().containsKey(vop.hashCode()));
        }

        Assert.assertEquals(testDoc1.getVerbObjectWeightVector(), testDoc2.getVerbObjectWeightVector());
        Assert.assertEquals(testDoc2.getVerbObjectWeightVector(), testDoc3.getVerbObjectWeightVector());
        Assert.assertEquals(testDoc3.getVerbObjectWeightVector(), testDoc1.getVerbObjectWeightVector());

    }

    @Test
    public void buildRelationGraph_properEdgeAdding(){

        List<ModelEntityDocument> testDocuments = new ArrayList<>();

        ModelEntityDocument testEntity1 = new ModelEntityDocument("testEntity1", "testEntity1", "1");
        testDocuments.add(testEntity1);

        ModelEntityDocument testEntity2 = new ModelEntityDocument("testEntity2", "testEntity2", "2");
        testDocuments.add(testEntity2);

        ModelEntityDocument testEntity3 = new ModelEntityDocument("testEntity3", "testEntity3", "3");
        testDocuments.add(testEntity3);


        testEntity1.addReferencedEntityId("2");
        testEntity2.addReferencedEntityId("3");

        RelationGraph graph = linkCalculator().buildRelationGraph(testDocuments);

        Assert.assertTrue(graph.hasEdge(testEntity1, testEntity2));
        Assert.assertTrue(graph.hasEdge(testEntity2, testEntity3));

    }

    @Test
    public void buildRelationGraph_noDuplicatedEdges(){

        List<ModelEntityDocument> testDocuments = new ArrayList<>();

        ModelEntityDocument testEntity1 = new ModelEntityDocument("testEntity1", "testEntity1", "1");
        testDocuments.add(testEntity1);

        ModelEntityDocument testEntity2 = new ModelEntityDocument("testEntity2", "testEntity2", "2");
        testDocuments.add(testEntity2);

        ModelEntityDocument testEntity3 = new ModelEntityDocument("testEntity3", "testEntity3", "3");
        testDocuments.add(testEntity3);


        testEntity1.addReferencedEntityId("2");
        testEntity2.addReferencedEntityId("3");
        testEntity2.addReferencedEntityId("3");

        RelationGraph graph = linkCalculator().buildRelationGraph(testDocuments);

        Assert.assertEquals(graph.getRelatedDocuments(testEntity2).size(), 1);

    }





}
