package tokyo.northside.omegat.rst;

import java.util.List;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import org.nuiton.jrst.legacy.ReStructuredText;


/**
 * Visitor for reStructured Text DOM.
 * @author Hiroshi Miura
 */
public class RstVisitor {
    private static Logger log = LoggerFactory.getLogger(RstVisitor.class);
    private ProxyVisitor v;
    private RstFilter filter;

    static final String EMPTY_STRING = "";
    static final String LINE_SEPARATOR = "\n";
    static final String SPACE = " ";
    static final Character[] TITLE_CHAR = {'=','-','~','^'};
    static final String EMPHASIS = "**";
    private int level;

    public RstVisitor(final RstFilter filter) {
        this.filter = filter;
    }

    public void visitDocument(Element e) {
    }

    private void write(String result) {
        filter.writeTranslate(result, true);
    }

    private void put(String result) {
        filter.writeTranslate(result, false);
    }

    public void visitTitle(Element e) {
        String result = e.getText();
        write(result);
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR[level];
        }
        put(LINE_SEPARATOR + underLine + LINE_SEPARATOR);
    }

    public void visitSubTitle(Element e) {
        String result = e.getText();
        write(result);
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR[level];
        }
        put(LINE_SEPARATOR + underLine + LINE_SEPARATOR);
    }

    public void visitSection(Element e) {
        level++;
    }

    public void visitTopic(Element e) {
        StringBuilder buffer = new StringBuilder(".. topic:: ");
        level++;
        List<?> elements = e.elements();
        for (Object o : elements) {
            Element element = (Element) o;
            if (elementEquals(ReStructuredText.TITLE, element)) {
                buffer.append(indent(element.getText()));
            } else if (elementEquals(ReStructuredText.PARAGRAPH, element)) {
                element.accept(v);
            }
        }
        --level;
        write(buffer.toString());
    }

    public void visitParagraph(Element e) {
        write(indent(e.getText(), level));
    }

    public void visitEmphasis(Element e) {
        write(EMPHASIS + e.getText() + EMPHASIS);
    }

    public void visitAttribution(Element e) {
    }

    public void visitBlockQuote(Element e) {
    }

    public void parseDocument(Document doc) {
        v = new ProxyVisitor(this);
        doc.accept(v);
    }

    protected boolean elementEquals(String name, Element e) {
        return e.getName().equals(name);
    }

    protected String indent(String toIndent) {
        return indent(toIndent, level);
    }

    protected String indent(String toIndent, int l) {
        String prefix = EMPTY_STRING;
        for (int i = 0; i < l; i++) {
            prefix += SPACE;
        }
        StringBuilder buffer = new StringBuilder();
        for (String s : toIndent.split(LINE_SEPARATOR)) {
            buffer.append(prefix).append(s).append(LINE_SEPARATOR);
        }
        return buffer.toString();
    }
}
