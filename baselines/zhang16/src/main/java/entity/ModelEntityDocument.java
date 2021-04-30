package entity;

import org.w3c.dom.Node;

import java.util.*;

public class ModelEntityDocument extends Document{

    private List<Identifier> identifierList;
    private String entityName;
    private String id;
    private List<String> referencedEntityIds;

    public ModelEntityDocument(String name, String entityName, String id) {
        super(name);
        this.entityName = entityName;
        this.identifierList = new ArrayList<>();
        this.id = id;
        referencedEntityIds = new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Identifier> getIdentifierList() {
        return identifierList;
    }

    public void addIdentifier(Identifier identifier){
        this.identifierList.add(identifier);
    }

    public void addAllIdentifier(Collection<Identifier> identifier){
        this.identifierList.addAll(identifier);
    }

    public List<String> getReferencedEntityIds() {
        return referencedEntityIds;
    }

    public void addReferencedEntityId(String referencedId){
        referencedEntityIds.add(referencedId);
    }

    public void addAllReferencedEntityIds(Collection<String> referencedIds){
        referencedEntityIds.addAll(referencedIds);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String name) {
        this.entityName = name;
    }

    public void setIdentifierList(List<Identifier> identifierList) {
        this.identifierList = identifierList;
    }

    @Override
    public String toString(){
        return entityName;

    }
}
