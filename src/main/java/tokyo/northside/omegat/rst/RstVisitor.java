package tokyo.northside.omegat.rst;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.nuiton.jrst.legacy.ReStructuredText;


/**
 * Visitor for reStructured Text DOM.
 * @author Hiroshi Miura
 */
public class RstVisitor extends VisitorSupport {
    private static Logger log = LoggerFactory.getLogger(RstVisitor.class);
    private BufferedWriter writer;
    private boolean failure = false;
    private StringBuilder failureLog = new StringBuilder();

    static final String EMPTY_STRING = "";
    static final String LINE_SEPARATOR = "\n";
    static final String SPACE = " ";
    static final Character[] TITLE_CHAR = {'=','-','~','^'};
    private int level;

    public RstVisitor(final BufferedWriter writer) {
        this.writer = writer;
    }

    private void write(final String s) {
        try {
            writer.write(s);
        } catch (IOException ioe) {
            failure = true;
            failureLog.append(ioe.getMessage());
        }
    }

    @Override
    public void visit(Element e) {
        if (elementEquals(ReStructuredText.DOCUMENT, e)) {
            composeDocument(e);
        }
    }

    public String composeTitle(Element e) {
        String result = e.getText();
        write(result);
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR[level];
        }
        result += LINE_SEPARATOR + underLine + LINE_SEPARATOR;
        log.debug("composeTitle :\n" + result);
        return result;
    }

    public String composeSubTitle(Element e) {
        String result = e.getText();
        write(result);
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR[level];
        }
        result += LINE_SEPARATOR + underLine + LINE_SEPARATOR;
        log.debug("composeSubTitle :\n" + result);
        return result;
    }

    public String composeSection(Element e) {
        level++;
        return EMPTY_STRING;
    }

    public String composeTopic(Element e) {
        StringBuilder buffer = new StringBuilder(".. topic:: ");
        level++;
        List<?> elements = e.elements();
        for (Object o : elements) {
            Element element = (Element) o;
            if (elementEquals(ReStructuredText.TITLE, element)) {
                buffer.append(indent(element.getText()));
            } else if (elementEquals(ReStructuredText.PARAGRAPH, element)) {
                parseDocument(element);
            }
        }
        --level;
        String result = buffer.toString();
        log.debug("composeTopic :\n" + result);
        return result;
    }

    public String composeParagraph(Element e) {
        String result = indent(e.getText(), level);
        log.debug("composeParagraph :\n" + result);
        return result;
    }


    public void parseDocument(Element e) {
          e.accept(this);
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
