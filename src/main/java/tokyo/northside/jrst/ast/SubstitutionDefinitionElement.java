package tokyo.northside.jrst.ast;

public class SubstitutionDefinitionElement extends Element {
    private String name;

    public SubstitutionDefinitionElement(String ref, Element children) {
        super(children);
        this.name = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
