package entity;

public class TraceLink implements Comparable<TraceLink>{
    private double weight;
    private final Document documentationDocument;
    private final ModelEntityDocument entityDocument;

    public TraceLink(double weight, Document documentationDocument, ModelEntityDocument entityDocument) {
        this.weight = weight;
        this.documentationDocument = documentationDocument;
        this.entityDocument = entityDocument;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Document getDocumentationDocument() {
        return documentationDocument;
    }

    public ModelEntityDocument getEntityDocument() {
        return entityDocument;
    }

    @Override
    public String toString(){
        String[] docuName = documentationDocument.getName().split("/");
        String[] compName = entityDocument.getEntityName().split("/");

        String str ="[" + docuName[docuName.length-1]+ "]-[" + compName[compName.length-1]+"] : "+ weight;
       return str ;
    }

    @Override
    public int compareTo(TraceLink link) {
        if(this.weight<link.getWeight())
            return 1;
        if(this.weight>link.getWeight())
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj){
        TraceLink link = (TraceLink) obj;
        return (this.documentationDocument.getName().equals(link.getDocumentationDocument().getName())
                && this.entityDocument.getEntityName().equals(link.getEntityDocument().getEntityName()));
    }

    @Override
    public int hashCode(){
        return((getDocumentationDocument().getName() + getEntityDocument().getEntityName()).hashCode());
    }

}
