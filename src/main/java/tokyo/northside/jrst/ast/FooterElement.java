package tokyo.northside.jrst.ast;

public class FooterElement extends TextElement {
    private int line;

    public FooterElement(String string) {
        super(string);
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
