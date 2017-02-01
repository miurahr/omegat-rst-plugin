package tokyo.northside.omegat.rst;

/**
 * Created by miurahr on 17/01/09.
 */
public class Element {

    private int start;
    private int end;
    private ElementType type;
    private String text;

    public Element(int start, int end, ElementType type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
