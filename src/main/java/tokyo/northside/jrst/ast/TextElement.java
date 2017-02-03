package tokyo.northside.jrst.ast;

import org.apache.commons.lang.StringUtils;

public class TextElement extends Element {
    private final StringBuilder sb;

    public TextElement() {
        this.sb = new StringBuilder();
    }

    public TextElement(String text) {
        this.sb = new StringBuilder(text);
    }

    public String getText() {
        return sb.toString();
    }

    public void append(String text) {
        sb.append(text);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + StringUtils.escape(getText()) + '\'';
    }
}
