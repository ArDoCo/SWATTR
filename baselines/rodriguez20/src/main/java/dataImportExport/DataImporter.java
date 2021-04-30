package dataImportExport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nimbusds.jose.util.StandardCharset;

import edu.stanford.nlp.ling.TaggedWord;
import preprocessing.Stemmer;
import preprocessing.Tagger;

/**
 *
 * @author claudius Class imports Directories
 *
 */
public class DataImporter {
    private static final String UNDERSCORE = "_";
    private static final String REQUIRED = "Required_";
    private static final String PROVIDED = "Provided_";
    private static final String REGEX_SPLIT = "(?<=.)(?=\\p{Lu})";
    private static final String SPACE = " ";
    private static final String SIGNATURES_OPERATION_INTERFACE = "signatures__OperationInterface";
    private static final String ID = "id";
    private static final String REPOSITORY_FILE_ENDING = ".repository";
    private static final String COMPONENTS_REPOSITORY = "components__Repository";
    private static final String INTERFACES_REPOSITORY = "interfaces__Repository";
    private static final String REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY = "requiredRoles_InterfaceRequiringEntity";
    private static final String PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY = "providedRoles_InterfaceProvidingEntity";
    private static final String ENTITY_NAME = "entityName";

    private List<Pair<Integer, String[]>> codeFilesContent;
    private List<Pair<Integer, List<Pair<Integer, String[]>>>> reqFiles;
    private List<Pair<Integer, String>> clearText = new ArrayList<>();
    private List<Pair<Integer, Integer>> fileIDRelation = new ArrayList<>();
    private Map<Integer, String> reqIdFilenameMap = new HashMap<>();
    private Map<String, String> nameToId = new HashMap<>();
    private int reqIdCounter = 0;
    private int srcCodeStartCounter = 0;
    private int fileIdCounter = 0;
    private Tagger tagger = new Tagger();
    private Stemmer stemmer = new Stemmer();

    public File importDirectory(String path) {
        return new File(path);
    }

    public void setupDataSetForCalculations(String pathToReqFolder, String pathToSrcCodeFolder) {
        reqFileDirToStringList(importDirectory(pathToReqFolder));
        importPcmRepo(importDirectory(pathToSrcCodeFolder));

    }

    private void reqFileDirToStringList(File reqDir) {
        reqFiles = new ArrayList<>();
        List<Pair<Integer, String[]>> reqFilesContent;

        for (File currentReqFile : reqDir.listFiles()) {
            reqFilesContent = new ArrayList<>();
            if (!currentReqFile.isFile()) {
                continue;
            }
            try {
                List<String> bufferList = FileUtils.readLines(currentReqFile, StandardCharset.UTF_8);
                for (String currentRequirement : bufferList) {
                    String csvcompatible = currentRequirement.replace(",", "");
                    clearText.add(new Pair<>(reqIdCounter, csvcompatible));

                    String[] trimmedreq = stemmer.stemming(tagger.tagging(currentRequirement));
                    reqFilesContent.add(new Pair<>(reqIdCounter, trimmedreq));
                    addtoReqFileList(reqIdCounter, fileIdCounter);

                    String[] r = currentReqFile.getName().split(REGEX_SPLIT);
                    reqIdFilenameMap.put(reqIdCounter, Arrays.toString(r));
                    reqIdCounter++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            reqFiles.add(new Pair<>(fileIdCounter, reqFilesContent));
            fileIdCounter++;
        }
    }

    private void importPcmRepo(File codeDir) {
        try {
            codeFilesContent = new ArrayList<>();
            srcCodeStartCounter = reqIdCounter;
            for (File currentCodeFile : codeDir.listFiles()) {
                if (!currentCodeFile.getName().endsWith(REPOSITORY_FILE_ENDING)) {
                    continue;
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = factory.newDocumentBuilder();

                Document doc = dBuilder.parse(currentCodeFile);
                NodeList interfacesList = doc.getElementsByTagName(INTERFACES_REPOSITORY);
                NodeList componentList = doc.getElementsByTagName(COMPONENTS_REPOSITORY);
                for (int i = 0; i < componentList.getLength(); i++) {
                    Node componentNode = componentList.item(i);

                    NamedNodeMap nnm = componentNode.getAttributes();
                    String compName = splitCamelCase(nnm.getNamedItem(ENTITY_NAME).getNodeValue());
                    String compId = nnm.getNamedItem(ID).getNodeValue();
                    nameToId.put(compName, compId);

                    List<String> interfaceProperties = getAllInterfaceProperties(interfacesList, componentNode);
                    List<TaggedWord> taggedWords = new ArrayList<>();
                    for (String property : interfaceProperties) {
                        taggedWords.addAll(tagger.tagging(property));
                    }
                    codeFilesContent.add(new Pair<>(reqIdCounter, stemmer.stemming(taggedWords)));
                    reqIdFilenameMap.put(reqIdCounter, compName);
                    reqIdCounter++;

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getAllInterfaceProperties(NodeList interfacesList, Node componentNode) {
        List<String> interfaceProperties = new ArrayList<>();
        for (int j = 0; j < componentNode.getChildNodes().getLength(); j++) {
            Node componentChildNode = componentNode.getChildNodes().item(j);
            String componentChildNodeName = componentChildNode.getNodeName();
            if (componentChildNodeName.contentEquals(PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY)
                    || componentChildNodeName.contentEquals(REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY)) {
                String interfaceNameToSearchFor = cutRoleString(componentChildNode.getAttributes().getNamedItem(ENTITY_NAME).getNodeValue());
                List<String> propertiesOfInterface = getPropertiesOfInterface(interfacesList, interfaceNameToSearchFor);
                interfaceProperties.addAll(propertiesOfInterface);
            }
        }
        return interfaceProperties;
    }

    private String cutRoleString(String inputString) {

        String processedString = inputString.replace(PROVIDED, "");
        processedString = processedString.replace(REQUIRED, "");
        int pos = processedString.indexOf(UNDERSCORE);
        if (pos > 0) {
            processedString = processedString.substring(0, processedString.indexOf(UNDERSCORE));
        }
        return processedString;
    }

    private String splitCamelCase(String inputString) {
        StringBuilder builder = new StringBuilder();
        String[] processedString = inputString.split(REGEX_SPLIT);
        for (int i = 1; i < processedString.length - 1; i++) {
            if (processedString[i].length() == 1) {
                processedString[i] = processedString[i] + processedString[i + 1];
                processedString[i + 1] = "";
            }
        }
        for (String element : processedString) {
            if (builder.length() != 0) {
                builder.append(SPACE);
            }
            if (element.length() > 1) {
                builder.append(element);
            }

        }
        return builder.toString();
    }

    private List<String> getPropertiesOfInterface(NodeList interfacesList, String targetInterface) {
        ArrayList<String> signatures = new ArrayList<>();
        for (int u = 0; u < interfacesList.getLength(); u++) {
            Node interfaceU = interfacesList.item(u);
            if (interfaceU.getAttributes().getNamedItem(ENTITY_NAME).getNodeValue().contains(targetInterface)) {
                NodeList childNodes = interfaceU.getChildNodes();
                for (int x = 0; x < childNodes.getLength(); x++) {
                    Node interfaceUChildX = childNodes.item(x);
                    if (interfaceUChildX.getNodeName().contentEquals(SIGNATURES_OPERATION_INTERFACE)) {

                        String signatureValue = String.valueOf(interfaceUChildX.getAttributes().getNamedItem(ENTITY_NAME).getNodeValue());
                        signatures.add(splitCamelCase(signatureValue));
                    }
                }
            }

        }
        return signatures;
    }

    private void addtoReqFileList(int req, int file) {
        Pair<Integer, Integer> fileReqPair = new Pair<>(fileIdCounter, reqIdCounter);
        fileIDRelation.add(fileReqPair);
    }

    public List<Pair<Integer, List<Pair<Integer, String[]>>>> srcCodeToReqFileListFormatConverter(List<Pair<Integer, String[]>> srcCodeFiles) {

        List<Pair<Integer, List<Pair<Integer, String[]>>>> wrapperList = new ArrayList<>();

        for (int i = 0; i < srcCodeFiles.size(); i++) {
            ArrayList<Pair<Integer, String[]>> buffer = new ArrayList<>();
            buffer.add(srcCodeFiles.get(i));

            Pair<Integer, List<Pair<Integer, String[]>>> fileIdWithReqIDandContent = new Pair<>(fileIdCounter, buffer);

            for (int j = 0; j < fileIdWithReqIDandContent.getValue1().size(); j++) {
                Pair<Integer, Integer> req = new Pair<>(fileIdWithReqIDandContent.getValue0(), fileIdWithReqIDandContent.getValue1().get(j).getValue0());
                fileIDRelation.add(req);
            }

            fileIdCounter++;
            wrapperList.add(fileIdWithReqIDandContent);
        }
        return wrapperList;
    }

    public List<Pair<Integer, String[]>> getCodeFilesContent() {
        return codeFilesContent;
    }

    public int getReqIdCounter() {
        return reqIdCounter;
    }

    public List<Pair<Integer, List<Pair<Integer, String[]>>>> getReqFiles() {
        return reqFiles;
    }

    public int getFileIdCounter() {
        return fileIdCounter;
    }

    public void setReqIdCounter(int reqIdCounter) {
        this.reqIdCounter = reqIdCounter;
    }

    public int getSrcCodeStartCounter() {
        return srcCodeStartCounter;
    }

    public void setSrcCodeStartCounter(int srcCodeStartCounter) {
        this.srcCodeStartCounter = srcCodeStartCounter;
    }

    public List<Pair<Integer, Integer>> getFileIDRelation() {
        return fileIDRelation;
    }

    public Map<Integer, String> getReqIdFilenameMap() {
        return reqIdFilenameMap;
    }

    public Map<String, String> getNameToId() {
        return nameToId;
    }

    public List<Pair<Integer, String>> getClearText() {
        return clearText;
    }

    public void setClearText(List<Pair<Integer, String>> clearText) {
        this.clearText = clearText;
    }

}
