package tokyo.northside.omegat.rst;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import org.nuiton.jrst.legacy.ReStructuredText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    static final String EMPHASIS = "*";
    static final String STRONG = "**";
    static final String TRANSITION = "\n----------\n";
    static final String LITERAL_BLOCK = "::";
    private int level;

    public RstVisitor(final RstFilter filter) {
        this.filter = filter;
        printer = new RstPrinter();
    }

    public void visitDocument(Element e) {
    }

    private void putAsToken(String result) {
        filter.writeTranslate(result, true);
    }

    private void putAsNonToken(String result) {
        filter.writeTranslate(result, false);
    }

    public void visitTitle(Element e) {
        String result = e.getText();
        putAsToken(result);
        StringBuilder underLine = new StringBuilder(LINE_SEPARATOR);
        for (int i = 0; i < result.length(); i++) {
            underLine.append(TITLE_CHAR[level]);
        }
        putAsNonToken(underLine.append(LINE_SEPARATOR).toString());
    }

    public void visitSubTitle(Element e) {
        String result = e.getText();
        putAsToken(result);
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR[level];
        }
        putAsNonToken(LINE_SEPARATOR + underLine + LINE_SEPARATOR);
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
        putAsToken(buffer.toString());
    }

    public void visitTransition(Element e) {
        putAsNonToken(TRANSITION);
    }

    public void visitParagraph(Element e) {
        putAsToken(indent(e.getText(), level));
    }

    public void visitEmphasis(Element e) {
        putAsToken(EMPHASIS + e.getText() + EMPHASIS);
    }

    public void visitStrong(Element e) {
        putAsToken(STRONG + e.getText() + STRONG);
    }

    public void visitAttribution(Element e) {
    }

    public void visitBlockQuote(Element e) {
        putAsToken(indent(e.getText(), level));
    }

    public void visitLiteralBlock(Element e) {
        putAsNonToken(LITERAL_BLOCK);
        putAsToken(indent(e.getText(), level));
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
