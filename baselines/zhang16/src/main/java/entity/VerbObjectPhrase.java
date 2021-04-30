package entity;

public class VerbObjectPhrase {

    private final String verb;
    private final String object;

    public VerbObjectPhrase(String verb, String object) {
        this.verb = verb;
        this.object = object;
    }

    public String getVerb() {
        return verb;
    }

    public String getObject() {
        return object;
    }

    @Override
    public boolean equals(Object vop){
        return (this.hashCode() == vop.hashCode());
    }

    @Override
    public int hashCode(){
        return (this.toString().hashCode());
    }

    @Override
    public String toString(){
        return "<" + this.verb + " , " + this.object + ">";
    }
}
