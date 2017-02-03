package tokyo.northside.jrst.ast;

public class DefinitionTermElement extends TextElement {
    private String classifiers;

    public DefinitionTermElement(String term) {
        super(term);
    }

    public String getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(String classifiers) {
        this.classifiers = classifiers;
    }
}
