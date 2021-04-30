package process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import entity.Identifier;
import entity.IdentifierType;
import entity.ModelEntityDocument;
import entity.VerbObjectPhrase;
import util.XMLProcessor;

public class ModelProcessor {

    private static final String ENTITY_NAME = "entityName";

    private String repositoryXMLPath;

    private Document parsedRepository;

    private NLProcessor nlProcessor;

    public ModelProcessor(String repositoryXMLPath, NLProcessor nlProcessor) {
        this.repositoryXMLPath = repositoryXMLPath;
        parsedRepository = XMLProcessor.importXMLDocument(repositoryXMLPath);

        this.nlProcessor = nlProcessor;
    }

    public List<ModelEntityDocument> createModelEntityDocuments() {

        List<ModelEntityDocument> entityDocuments = new ArrayList<>();

        List<Node> entityNodes = XMLProcessor.getNodesByTagName("components__Repository", parsedRepository);
        entityNodes.addAll(XMLProcessor.getNodesByTagName("interfaces__Repository", parsedRepository));
        entityNodes.addAll(XMLProcessor.getNodesByTagName("failureTypes__Repository", parsedRepository));
        entityNodes.addAll(XMLProcessor.getNodesByTagName("dataTypes__Repository", parsedRepository));

        for (Node node : entityNodes) {
            String name = XMLProcessor.getAttributeValue(ENTITY_NAME, node);
            String id = XMLProcessor.getAttributeValue("id", node);

            ModelEntityDocument entity = new ModelEntityDocument(repositoryXMLPath + "#" + name, name, id);

            entity.addAllIdentifier(getIdentifierFromComponentNode(node));
            entity.addAllIdentifier(getIdentifierFromInterfaceNode(node));
            entity.addAllIdentifier(getIdentifierFromDataTypeNode(node));
            entity.addAllIdentifier(getIdentifierFromFailureTypeNode(node));

            entity.addAllReferencedEntityIds(getReferencedIdsFromComponentNode(node));
            entity.addAllReferencedEntityIds(getReferencedIdsFromInterfaceNode(node));

            entityDocuments.add(entity);

        }
        processEntityIdentifiers(entityDocuments);

        return entityDocuments;
    }

    private List<Identifier> getIdentifierFromComponentNode(Node componentNode) {
        List<Identifier> identifier = new ArrayList<>();

        if (!(componentNode.getNodeName().equals("components__Repository"))) {
            return identifier;
        }

        Element componentElement = (Element) componentNode;

        String name = XMLProcessor.getAttributeValue(ENTITY_NAME, componentNode);
        identifier.add(new Identifier(IdentifierType.NAME, name));

        for (Node step : XMLProcessor.getNodesByTagName("steps_Behaviour", componentElement)) {
            String ident = XMLProcessor.getAttributeValue(ENTITY_NAME, step);
            if (!(ident == null)) {
                if (!(ident.equals("start")) && !(ident.equals("stop")) && !(ident.contains("JNDI RMI"))) {
                    identifier.add(new Identifier(IdentifierType.OTHER, ident));
                }
            }
        }

        for (Node node : XMLProcessor.getNodesByTagName("providedRoles_InterfaceProvidingEntity", componentElement)) {
            String referencedId = XMLProcessor.getAttributeValue("providedInterface__OperationProvidedRole", node);
            Node referencedInterface = XMLProcessor.getNodeById(referencedId, parsedRepository);
            List<Identifier> seffNameIdentifier = new ArrayList<>();

            // get identifier from SEFF names
            for (Node seff : XMLProcessor.getNodesByTagName("serviceEffectSpecifications__BasicComponent", (Element) componentNode)) {
                Node referencedSeff = XMLProcessor.getNodeById(XMLProcessor.getAttributeValue("_describedService__SEFF", seff), parsedRepository);
                if (referencedSeff != null) {
                    String seffName = XMLProcessor.getAttributeValue(ENTITY_NAME, referencedSeff);
                    if (seffName != null) {
                        seffNameIdentifier.add(new Identifier(IdentifierType.SIGN, seffName));
                    }
                }
            }

            // get identifier from implemented interface
            List<Identifier> interfaceIdentifiers = getIdentifierFromInterfaceNode(referencedInterface);
            for (Identifier ident : interfaceIdentifiers) {
                if (!(ident.getType().equals(IdentifierType.NAME))) {
                    identifier.add(ident);
                }
            }

            for (Identifier ident : seffNameIdentifier) {
                // only add identifier if not already added due to the interface implementation to prevent redundancy
                if (!(interfaceIdentifiers.contains(ident))) {
                    identifier.add(ident);
                }
            }
        }

        return identifier;
    }

    private List<Identifier> getIdentifierFromInterfaceNode(Node interfaceNode) {
        List<Identifier> identifier = new ArrayList<>();
        if (interfaceNode == null) {
            return identifier;
        }

        if (!(interfaceNode.getNodeName().equals("interfaces__Repository"))) {
            return identifier;
        }

        String name = XMLProcessor.getAttributeValue(ENTITY_NAME, interfaceNode);
        identifier.add(new Identifier(IdentifierType.NAME, name));

        List<Node> signatures = XMLProcessor.getNodesByTagName("signatures__OperationInterface", (Element) interfaceNode);
        for (Node sig : signatures) {
            String signature = XMLProcessor.getAttributeValue(ENTITY_NAME, sig);
            if (!(signature == null)) {
                for (Node p : XMLProcessor.getNodesByTagName("parameters__OperationSignature", (Element) sig)) {
                    String paramter = XMLProcessor.getAttributeValue("parameterName", p);
                    identifier.add(new Identifier(IdentifierType.PARAM, paramter));
                }
                identifier.add(new Identifier(IdentifierType.SIGN, signature));
            }
        }

        return identifier;
    }

    private List<Identifier> getIdentifierFromFailureTypeNode(Node failureTypeNode) {
        List<Identifier> identifier = new ArrayList<>();
        if (!(failureTypeNode.getNodeName().equals("failureTypes__Repository"))) {
            return identifier;
        }

        String name = XMLProcessor.getAttributeValue(ENTITY_NAME, failureTypeNode);
        identifier.add(new Identifier(IdentifierType.NAME, name));
        // maybe failure types can have additional identifiers that i dont know yet
        return identifier;
    }

    private List<Identifier> getIdentifierFromDataTypeNode(Node dataTypeNode) {
        List<Identifier> identifier = new ArrayList<>();
        if (!(dataTypeNode.getNodeName().equals("dataTypes__Repository"))) {
            return identifier;
        }

        Element dataTypeElement = (Element) dataTypeNode;
        String name = XMLProcessor.getAttributeValue(ENTITY_NAME, dataTypeNode);
        identifier.add(new Identifier(IdentifierType.NAME, name));

        for (Node innerDeclaration : XMLProcessor.getNodesByTagName("innerDeclaration_CompositeDataType", dataTypeElement)) {
            identifier.add(new Identifier(IdentifierType.OTHER, XMLProcessor.getAttributeValue(ENTITY_NAME, innerDeclaration)));
        }
        return identifier;
    }

    private List<String> getReferencedIdsFromComponentNode(Node componentNode) {
        List<String> ids = new ArrayList<>();

        for (Node providedNode : XMLProcessor.getNodesByTagName("providedRoles_InterfaceProvidingEntity", (Element) componentNode)) {
            String interfaceId = XMLProcessor.getAttributeValue("providedInterface__OperationProvidedRole", providedNode);
            ids.add(interfaceId);
            Node interfaceNode = XMLProcessor.getNodeById(interfaceId, parsedRepository);
            if (interfaceNode != null) {
                ids.addAll(getReferencedIdsFromInterfaceNode(interfaceNode));
            }
        }

        for (Node providedNode : XMLProcessor.getNodesByTagName("requiredRoles_InterfaceRequiringEntity", (Element) componentNode)) {
            String interfaceId = XMLProcessor.getAttributeValue("requiredInterface__OperationRequiredRole", providedNode);
            ids.add(interfaceId);
            Node interfaceNode = XMLProcessor.getNodeById(interfaceId, parsedRepository);
            if (interfaceNode != null) {
                ids.addAll(getReferencedIdsFromInterfaceNode(interfaceNode));
            }
        }

        for (Node providedNode : XMLProcessor.getNodesByTagName("requiredRoles_ComponentRequiringEntity", (Element) componentNode)) {
            String interfaceId = XMLProcessor.getAttributeValue("requiredComponent__OperationRequiredRole", providedNode);
            ids.add(interfaceId);
            Node interfaceNode = XMLProcessor.getNodeById(interfaceId, parsedRepository);
            if (interfaceNode != null) {
                ids.addAll(getReferencedIdsFromInterfaceNode(interfaceNode));
            }
        }

        return ids;
    }

    private List<String> getReferencedIdsFromInterfaceNode(Node interfaceNode) {
        List<String> ids = new ArrayList<>();
        for (Node signatureNode : XMLProcessor.getNodesByTagName("signatures__OperationInterface", (Element) interfaceNode)) {
            String returnTypeId = XMLProcessor.getAttributeValue("returnType__OperationSignature", signatureNode);
            if (returnTypeId != null) {
                ids.add(returnTypeId);
            }
            for (Node parameterNode : XMLProcessor.getNodesByTagName("parameters__OperationSignature", (Element) signatureNode)) {
                String dataTypeId = XMLProcessor.getAttributeValue("dataType__Parameter", parameterNode);
                if (dataTypeId != null) {
                    ids.add(dataTypeId);
                }
            }
        }
        return ids;
    }

    public String getRepositoryXMLPath() {
        return repositoryXMLPath;
    }

    public Document getParsedRepository() {
        return parsedRepository;
    }

    private void processEntityIdentifiers(Collection<ModelEntityDocument> entityDocuments) {
        // process Identifier for each component:
        for (ModelEntityDocument entityDocument : entityDocuments) {
            entityDocument.addWordToBag(entityDocument.getEntityName());
            for (Identifier ident : entityDocument.getIdentifierList()) {
                Set<String> identifierStems = nlProcessor.stemIdentifier(ident);

                entityDocument.addAllWordsToBag(identifierStems);
                // also add unsplitted identifier since identifier are sometimes mentioned in the docu explicitly by its
                // name.
                entityDocument.addWordToBag(ident.getName(), ident.getTuningFactor());

                List<VerbObjectPhrase> p = nlProcessor.getStemmedVOPhrases(nlProcessor.splitIdentifierName(ident.getName()));

                entityDocument.addAllPhrases(p, ident.getTuningFactor());
            }
        }
    }
}
