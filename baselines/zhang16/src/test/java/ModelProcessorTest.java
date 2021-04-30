import java.net.MalformedURLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import entity.ModelEntityDocument;
import process.ModelProcessor;
import process.NLProcessor;

public class ModelProcessorTest {

    String repoFilePath = "./src/test/resources/test-datasets/TeaStore/model-1/TeaStore_PCM/TeaStore.repository";
    ModelProcessor modelProcessor;

    public ModelProcessor modelProcessor() throws MalformedURLException {
        if (modelProcessor == null) {
            modelProcessor = new ModelProcessor(repoFilePath, new NLProcessor("./src/main/resources/wordnet-database/dict"));
        }
        return modelProcessor;
    }

    @Test
    public void modelProcessorTest() {
        ModelProcessor modelProcessor = null;
        try {
            modelProcessor = modelProcessor();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
            return;
        }

        Assert.assertNotNull(modelProcessor);
        Assert.assertNotNull(modelProcessor.getParsedRepository());

    }

    @Test
    public void createEntityDocumentsTest() {
        List<ModelEntityDocument> entiyDocs = null;
        try {
            entiyDocs = modelProcessor().createModelEntityDocuments();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assert.assertTrue(false);
            return;
        }

        Assert.assertEquals(entiyDocs.size(), 27);

    }

}
