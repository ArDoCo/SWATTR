package entity;

public class Identifier {

    private String name;
    private final IdentifierType type;

    public Identifier(IdentifierType type, String name) {
        this.name = name;
        this.type = type;
    }

    public IdentifierType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getTuningFactor() {
            switch(this.type){
            //name has a high tuning factor so that it creates a strong link if the actual entity name is explicitly mentioned in the docu.
            case NAME: return 3.0;
            case SIGN: return 1.5;
            case TYPE: return 1.0;
            case PARAM: return 1.0;
            case OTHER: return 1.0;
        }
        return 1.0;
    }

    @Override
    public String toString(){
        return "["+type.toString()+"]"+name;
    }

    @Override
    public boolean equals(Object vop){
        return (this.hashCode() == vop.hashCode());
    }

    @Override
    public int hashCode(){
        return (this.toString().hashCode());
    }
}
