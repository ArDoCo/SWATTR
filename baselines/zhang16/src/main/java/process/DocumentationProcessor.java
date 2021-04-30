package process;

import entity.Document;
import util.FileImporter;

import java.util.*;

public class DocumentationProcessor {

    private String documentationPath;
    private NLProcessor nlProcessor;


    public DocumentationProcessor(String documentationPath, NLProcessor nlProcessor) {

        this.nlProcessor = nlProcessor;
        this.documentationPath = documentationPath;
    }

    public List<Document> createDocuments(){
        List<Document> documentList = new ArrayList<>();

        List<String> rawDocs = FileImporter.importFileLines(documentationPath);
        rawDocs.removeAll(Collections.singleton(""));

        int i = 0;
        for(String raw: rawDocs){
            i+=1;
            Document document = new Document(documentationPath + "#"+i);
            document.addAllWordsToBag(nlProcessor.getBagOfStemmedWords(raw));
            document.addAllPhrases(nlProcessor.getStemmedVOPhrases(raw));
            document.addAllWordsToBag(nlProcessor.extractIdentifierFromText(raw), 2.0);

            documentList.add(document);
        }

        return documentList;
    }
}
