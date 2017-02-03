package tokyo.northside.jrst.ast;

/**
 * Created by miurahr on 2017/02/03.
 */
public class FIeldListElement extends TextElement {
    private String name;

    public FIeldListElement(String name) {
        super(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
