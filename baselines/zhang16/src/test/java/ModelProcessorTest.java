import java.net.MalformedURLException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import entity.ModelEntityDocument;
import process.ModelProcessor;
import process.NLProcessor;

class ModelProcessorTest {

    String repoFilePath = "./src/test/resources/test-datasets/TeaStore/model-1/TeaStore_PCM/TeaStore.repository";
    ModelProcessor modelProcessor;

    ModelProcessor modelProcessor() throws MalformedURLException {
        if (modelProcessor == null) {
            modelProcessor = new ModelProcessor(repoFilePath, new NLProcessor("./src/main/resources/wordnet-database/dict"));
        }
        return modelProcessor;
    }

    @Test
    void modelProcessorTest() {
        ModelProcessor modelProcessor = null;
        try {
            modelProcessor = modelProcessor();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Assertions.assertTrue(false);
            return;
        }

        Assertions.assertNotNull(modelProcessor);
        Assertions.assertNotNull(modelProcessor.getParsedRepository());

    }

    @Test
    void createEntityDocumentsTest() {
        List<ModelEntityDocument> entiyDocs = null;
        try {
            entiyDocs = modelProcessor().createModelEntityDocuments();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assertions.assertTrue(false);
            return;
        }

        Assertions.assertEquals(entiyDocs.size(), 27);

    }

}
