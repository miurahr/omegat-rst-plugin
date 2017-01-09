/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
 with fuzzy matching, translation memory, keyword search,
 glossaries, and translation leveraging into updated projects.

 Copyright (C) 2004 - 2010 CodeLutin
 Copyright (C) 2016 Hiroshi Miura
 Home page: http://www.omegat.org/
 Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 This file is derived from jRST project by CodeLutin.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

/* *
 * JRSTReader.java
 *
 * Created: 27 oct. 06 00:15:34
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package tokyo.northside.jrst;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.IllegalAddException;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;

import tokyo.northside.jrst.directive.ContentDirective;
import tokyo.northside.jrst.directive.DateDirective;
import tokyo.northside.jrst.directive.ImageDirective;
import tokyo.northside.jrst.directive.SectnumDirective;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tokyo.northside.jrst.ReStructuredText.ADDRESS;
import static tokyo.northside.jrst.ReStructuredText.ADMONITION;
import static tokyo.northside.jrst.ReStructuredText.ATTRIBUTION;
import static tokyo.northside.jrst.ReStructuredText.AUTHOR;
import static tokyo.northside.jrst.ReStructuredText.AUTHORS;
import static tokyo.northside.jrst.ReStructuredText.BLOCK_QUOTE;
import static tokyo.northside.jrst.ReStructuredText.BULLET_LIST;
import static tokyo.northside.jrst.ReStructuredText.COLSPEC;
import static tokyo.northside.jrst.ReStructuredText.COMMENT;
import static tokyo.northside.jrst.ReStructuredText.CONTACT;
import static tokyo.northside.jrst.ReStructuredText.COPYRIGHT;
import static tokyo.northside.jrst.ReStructuredText.DATE;
import static tokyo.northside.jrst.ReStructuredText.DECORATION;
import static tokyo.northside.jrst.ReStructuredText.DEFINITION;
import static tokyo.northside.jrst.ReStructuredText.DEFINITION_LIST;
import static tokyo.northside.jrst.ReStructuredText.DEFINITION_LIST_ITEM;
import static tokyo.northside.jrst.ReStructuredText.DESCRIPTION;
import static tokyo.northside.jrst.ReStructuredText.DOCINFO;
import static tokyo.northside.jrst.ReStructuredText.DOCTEST_BLOCK;
import static tokyo.northside.jrst.ReStructuredText.DOCUMENT;
import static tokyo.northside.jrst.ReStructuredText.EMPHASIS;
import static tokyo.northside.jrst.ReStructuredText.ENTRY;
import static tokyo.northside.jrst.ReStructuredText.ENUMERATED_LIST;
import static tokyo.northside.jrst.ReStructuredText.FIELD;
import static tokyo.northside.jrst.ReStructuredText.FIELD_BODY;
import static tokyo.northside.jrst.ReStructuredText.FIELD_LIST;
import static tokyo.northside.jrst.ReStructuredText.FIELD_NAME;
import static tokyo.northside.jrst.ReStructuredText.FOOTER;
import static tokyo.northside.jrst.ReStructuredText.FOOTNOTE;
import static tokyo.northside.jrst.ReStructuredText.FOOTNOTE_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.FOOTNOTE_SYMBOL;
import static tokyo.northside.jrst.ReStructuredText.GENERATED;
import static tokyo.northside.jrst.ReStructuredText.HEADER;
import static tokyo.northside.jrst.ReStructuredText.IMAGE;
import static tokyo.northside.jrst.ReStructuredText.LABEL;
import static tokyo.northside.jrst.ReStructuredText.LINE;
import static tokyo.northside.jrst.ReStructuredText.LINE_BLOCK;
import static tokyo.northside.jrst.ReStructuredText.LIST_ITEM;
import static tokyo.northside.jrst.ReStructuredText.LITERAL;
import static tokyo.northside.jrst.ReStructuredText.LITERAL_BLOCK;
import static tokyo.northside.jrst.ReStructuredText.OPTION;
import static tokyo.northside.jrst.ReStructuredText.OPTION_ARGUMENT;
import static tokyo.northside.jrst.ReStructuredText.OPTION_GROUP;
import static tokyo.northside.jrst.ReStructuredText.OPTION_LIST;
import static tokyo.northside.jrst.ReStructuredText.OPTION_LIST_ITEM;
import static tokyo.northside.jrst.ReStructuredText.OPTION_STRING;
import static tokyo.northside.jrst.ReStructuredText.ORGANIZATION;
import static tokyo.northside.jrst.ReStructuredText.PARAGRAPH;
import static tokyo.northside.jrst.ReStructuredText.REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_ANONYMOUS_HYPERLINK_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_EMAIL;
import static tokyo.northside.jrst.ReStructuredText.REGEX_EMPHASIS;
import static tokyo.northside.jrst.ReStructuredText.REGEX_FOOTNOTE_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_HYPERLINK_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_INLINE_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_LITERAL;
import static tokyo.northside.jrst.ReStructuredText.REGEX_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REGEX_STRONG;
import static tokyo.northside.jrst.ReStructuredText.REGEX_SUBSTITUTION_REFERENCE;
import static tokyo.northside.jrst.ReStructuredText.REVISION;
import static tokyo.northside.jrst.ReStructuredText.ROW;
import static tokyo.northside.jrst.ReStructuredText.SECTION;
import static tokyo.northside.jrst.ReStructuredText.SIDEBAR;
import static tokyo.northside.jrst.ReStructuredText.STATUS;
import static tokyo.northside.jrst.ReStructuredText.STRONG;
import static tokyo.northside.jrst.ReStructuredText.SUBSTITUTION_DEFINITION;
import static tokyo.northside.jrst.ReStructuredText.SUBTITLE;
import static tokyo.northside.jrst.ReStructuredText.TABLE;
import static tokyo.northside.jrst.ReStructuredText.TARGET;
import static tokyo.northside.jrst.ReStructuredText.TBODY;
import static tokyo.northside.jrst.ReStructuredText.TERM;
import static tokyo.northside.jrst.ReStructuredText.TGROUP;
import static tokyo.northside.jrst.ReStructuredText.THEAD;
import static tokyo.northside.jrst.ReStructuredText.TITLE;
import static tokyo.northside.jrst.ReStructuredText.TOPIC;
import static tokyo.northside.jrst.ReStructuredText.TRANSITION;
import static tokyo.northside.jrst.ReStructuredText.VERSION;

/*
 * 
 * <pre> +--------------------------------------------------------------------+ |
 * document [may begin with a title, subtitle, decoration, docinfo] | |
 * +--------------------------------------+ | | sections [each begins with a
 * title] |
 * +-----------------------------+-------------------------+------------+ |
 * [body elements:] | (sections) | | | - literal | - lists | | - hyperlink
 * +------------+ | | blocks | - tables | | targets | | para- | - doctest | -
 * block | foot- | - sub. defs | | graphs | blocks | quotes | notes | - comments |
 * +---------+-----------+----------+-------+--------------+ | [text]+ | [text] |
 * (body elements) | [text] | | (inline
 * +-----------+------------------+--------------+ | markup) | +---------+
 * </pre>
 * 
 * 
 * Inline support: http://docutils.sourceforge.net/docs/user/rst/quickref.html
 * 
 * <li> STRUCTURAL ELEMENTS: document, section, topic, sidebar <li> STRUCTURAL
 * SUBELEMENTS: title, subtitle, decoration, docinfo, transition <li> docinfo:
 * address, author, authors, contact, copyright, date, field, organization,
 * revision, status, version <li> decoration: footer, header <li> BODY ELEMENTS:
 * admonition, attention, block_quote, bullet_list, caution, citation, comment,
 * compound, container, danger, definition_list, doctest_block, enumerated_list,
 * error, field_list, figure, footnote, hint, image, important, line_block,
 * literal_block, note, option_list, paragraph, pending, raw, rubric,
 * substitution_definition, system_message, table, target, tip, warning <li>
 * SIMPLE BODY ELEMENTS: comment, doctest_block, image, literal_block,
 * paragraph, pending, raw, rubric, substitution_definition, target <li>
 * COMPOUND BODY ELEMENTS: admonition, attention, block_quote, bullet_list,
 * caution, citation, compound, container, danger, definition_list,
 * enumerated_list, error, field_list, figure, footnote, hint, important,
 * line_block, note, option_list, system_message, table, tip, warning <li> BODY
 * SUBELEMENTS: attribution, caption, classifier, colspec, field_name, label,
 * line, option_argument, option_string, term definition, definition_list_item,
 * description, entry, field, field_body, legend, list_item, option,
 * option_group, option_list_item, row, tbody, tgroup, thead <li> INLINE
 * ELEMENTS: abbreviation, acronym, citation_reference, emphasis,
 * footnote_reference, generated, image, inline, literal, problematic,
 * reference, strong, subscript, substitution_reference, superscript, target,
 * title_reference, raw
 * 
 * <pre> DOCUMENT :: ( (title, subtitle?)?, decoration?, (docinfo,
 * transition?)?, STRUCTURE.MODEL; ) decoration :: (header?, footer?) header,
 * footer, definition, description, attention, caution, danger, error, hint,
 * important, note, tip, warning :: (BODY.ELEMENTS;)+ transition :: EMPTY
 * docinfo :: (BIBLIOGRAPHIC.ELEMENTS;)+ BIBLIOGRAPHIC.ELEMENTS :: author |
 * authors | organization | contact | address | version | revision | status |
 * date | copyright | field authors :: ( (author)+ ) field :: (field_name,
 * field_body) field_body, list_item :: (BODY.ELEMENTS;)* STRUCTURE.MODEL :: ( (
 * (BODY.ELEMENTS; | topic | sidebar)+, transition? )*, ( (section),
 * (transition?, (section) )* )? ) BODY.ELEMENTS :: paragraph | compound |
 * container | literal_block | doctest_block | line_block | block_quote | table |
 * figure | image | footnote | citation | rubric | bullet_list | enumerated_list |
 * definition_list | field_list | option_list | attention | caution | danger |
 * error | hint | important | note | tip | warning | admonition | reference |
 * target | substitution_definition | comment | pending | system_message | raw
 * topic :: (title?, (BODY.ELEMENTS;)+) sidebar :: (title, subtitle?,
 * (BODY.ELEMENTS; | topic)+) section :: (title, STRUCTURE.MODEL;) line_block ::
 * (line | line_block)+ block_quote:: ((BODY.ELEMENTS;)+, attribution?)
 * bullet_list, enumerated_list :: (list_item +) definition_list ::
 * (definition_list_item +) definition_list_item :: (term, classifier?,
 * definition) field_list :: (field +) option_list :: (option_list_item +)
 * option_list_item :: (option_string, option_argument *, description)
 * option_string, option_argument :: (#PCDATA) admonition :: (title,
 * (BODY.ELEMENTS;)+)
 * 
 * title, subtitle, author, organization, contact, address, version, revision,
 * status, date, copyright, field_name, paragraph, compound, container,
 * literal_block, doctest_block, attribution, line, term, classifier ::
 * TEXT.MODEL;
 * 
 * TEXT.MODEL :: (#PCDATA | INLINE.ELEMENTS;)* INLINE.ELEMENTS :: emphasis |
 * strong | literal | reference | footnote_reference | citation_reference |
 * substitution_reference | title_reference | abbreviation | acronym | subscript |
 * superscript | inline | problematic | generated | target | image | raw
 * emphasis :: '*' #PCDATA '*' strong :: '**' #PCDATA '**' literal :: '``'
 * #PCDATA '``' footnote_reference :: '[' ([0-9]+|#) ']' citation_reference ::
 * '[' [a-zA-Z]+ ']'
 * 
 * </pre>
 */

/**
 * Le principe est d'utiliser les methodes peek* {@link JRSTLexer} pour
 * prendre l'element que l'on attend, si la methode retourne null ou un autre
 * element et bien c que ce n'est pas le bon choix, cela veut dire que l'element
 * courant est fini d'etre lu (plus de paragraphe dans la section par exemple)
 * ou qu'il y a une erreur dans le fichier en entre.
 * <p>
 * Tous les elements ont un attribut level qui permet de savoir on il est dans
 * la hierarchie. Le Document a le level -1001, et les sections/titres on pour
 * level les valeurs 1000, -999, -998, ...
 * <p>
 * de cette facon les methods isUpperLevel et isSameLevel fonctionne pour tous
 * les elements de la meme facon
 *
 * <pre>
 *   abbreviation
 *   acronym
 *   address (done)
 *   admonition (done)
 *   attention (done)
 *   attribution
 *   author (done)
 *   authors (done)
 *   block_quote (done)
 *   bullet_list (done)
 *   caption
 *   caution (done)
 *   citation
 *   citation_reference
 *   classifier (done)
 *   colspec (done)
 *   comment
 *   compound
 *   contact (done)
 *   container
 *   copyright (done)
 *   danger (done)
 *   date (done)
 *   decoration (done)
 *   definition (done)
 *   definition_list (done)
 *   definition_list_item (done)
 *   description (done)
 *   docinfo (done)
 *   doctest_block (done)
 *   document (done)
 *   emphasis (done)
 *   entry (done)
 *   enumerated_list (done)
 *   error (done)
 *   field (done)
 *   field_body (done)
 *   field_list (done)
 *   field_name (done)
 *   figure
 *   footer (done)
 *   footnote	(done)
 *   footnote_reference (done)
 *   generated
 *   header (done)
 *   hint (done)
 *   image (done)
 *   important (done)
 *   inline
 *   label
 *   legend
 *   line (done)
 *   line_block (done)
 *   list_item (done)
 *   literal (done)
 *   literal_block (done)
 *   note (done)
 *   option (done)
 *   option_argument (done)
 *   option_group (done)
 *   option_list (done)
 *   option_list_item (done)
 *   option_string (done)
 *   organization (done)
 *   paragraph (done)
 *   pending
 *   problematic
 *   raw
 *   reference (done)
 *   revision (done)
 *   row (done)
 *   rubric
 *   section (done)
 *   sidebar (done)
 *   status (done)
 *   strong (done)
 *   subscript
 *   substitution_definition
 *   substitution_reference
 *   subtitle (done)
 *   superscript
 *   system_message
 *   table (done)
 *   target (done)
 *   tbody (done)
 *   term (done)
 *   tgroup (done)
 *   thead (done)
 *   tip (done)
 *   title (done)
 *   title_reference
 *   topic (done)
 *   transition (done)
 *   version (done)
 *   warning (done)
 * </pre>
 *
 * Created: 27 oct. 06 00:15:34
 *
 * @author poussin, letellier
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class JRSTReader {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    private static Log log = LogFactory.getLog(JRSTReader.class);

    protected static final String ANONYMOUS = "anonymous";

    protected static final String AUTO = "auto";

    protected static final String AUTONUM = "autoNum";

    protected static final String AUTONUMLABEL = "autoNumLabel";

    protected static final String AUTOSYMBOL = "autoSymbol";

    protected static final String ATTR_REFID = "refid";

    protected static final String ATTR_INLINE = "inline";

    protected static final String ATTR_IDS = "ids";

    protected static final String BACKREFS = "backrefs";

    protected static final String BULLET = "bullet";

    protected static final String CLASS = "class";

    protected static final String CONTENTS = "contents";

    protected static final String DELIMITER = "delimiter";

    protected static final String DELIMITEREXISTE ="delimiterExiste";

    protected static final String ENUMTYPE = "enumtype";

    protected static final String FOOTNOTES = "footnotes";

    protected static final String ID = "id";

    protected static final String INCLUDE = "include";

    protected static final String LEVEL = "level";

    protected static final String NAME = "name";

    protected static final String NAMES = "names";

    protected static final String NUM = "num";

    protected static final String REFURI = "refuri";

    protected static final String PREFIX = "prefix";

    protected static final String REMOVE = "remove";

    protected static final String START = "start";

    protected static final String SECTNUM = "sectnum";

    protected static final String SUBEXISTE = "subExiste";

    protected static final String SUFFIX = "suffix";

    protected static final String TRUE = "true";

    protected static final String TYPE = "type";

    protected static final String TARGETANONYMOUS = "targetAnonymous";

    protected static final String VALUE = "value";

    protected boolean ERROR_MISSING_ITEM;

    protected static int MAX_SECTION_DEPTH = -1000;

    protected static Map<String, JRSTDirective> defaultDirectives;

    protected Map<String, JRSTDirective> directives = new HashMap<>();

    private boolean sectnum;

    private Element footer;

    private int idMax;

    private int symbolMax;

    private int symbolMaxRef;

    private LinkedList<Integer> lblFootnotes = new LinkedList<Integer>();

    private LinkedList<Integer> lblFootnotesRef = new LinkedList<Integer>();

    private LinkedList<Element> eFootnotes = new LinkedList<Element>();

    private LinkedList<Element> eTarget = new LinkedList<Element>();

    private LinkedList<Element> eTargetAnonymous = new LinkedList<Element>();

    private LinkedList<Element> eTargetAnonymousCopy = new LinkedList<Element>();

    private LinkedList<Element> eTitle = new LinkedList<Element>();

    static {
        defaultDirectives = new HashMap<>();
        defaultDirectives.put(IMAGE, new ImageDirective());
        defaultDirectives.put(DATE, new DateDirective());
        defaultDirectives.put("time", new DateDirective());
        defaultDirectives.put(CONTENTS, new ContentDirective());
        defaultDirectives.put(SECTNUM, new SectnumDirective());
        // TODO put here all other directive
    }

    /**
     *
     */
    public JRSTReader() {
    }

    /**
     * @param name
     * @return the defaultDirectives
     */
    public static JRSTDirective getDefaultDirective(String name) {
        return defaultDirectives.get(name);
    }

    /**
     * @param name
     * @param directive the defaultDirectives to set
     */
    public static void addDefaultDirectives(String name, JRSTDirective directive) {
        JRSTReader.defaultDirectives.put(name, directive);
    }

    /**
     * @param name
     * @return the defaultDirectives
     */
    public JRSTDirective getDirective(String name) {
        return directives.get(name);
    }

    /**
     * @param name
     * @param directive the defaultDirectives to set
     */
    public void addDirectives(String name, JRSTDirective directive) {
        directives.put(name, directive);
    }

    /**
     * On commence par decouper tout le document en Element, puis on construit
     * l'article a partir de ces elements.
     *
     * @param reader
     * @return le document cree
     * @throws Exception
     */
    public Document read(Reader reader) throws Exception {
        JRSTLexer lexer = new JRSTLexer(reader);
        try {
            Element root = composeDocument(lexer);

            Document result = DocumentHelper.createDocument();
            result.setRootElement(root);

            root.accept(new VisitorSupport() {
                @Override
                public void visit(Element e) {
                    // remove all level attribute
                    e.addAttribute(LEVEL, null);
                    // Constrution du sommaire
                    String type = e.attributeValue(TYPE);
                    if (type != null) {
                        if (type.equals(CONTENTS)) {
                            composeContents(e);
                            e.addAttribute(TYPE, null);
                        }
                    }

                    if (TRUE.equalsIgnoreCase(e.attributeValue(ATTR_INLINE))) {
                        e.addAttribute(ATTR_INLINE, null);
                        try {
                            inline(e);
                        } catch (DocumentException eee) {
                            if (log.isWarnEnabled()) {
                                log.warn("Can't inline text for " + e, eee);
                            }
                        } catch (UnsupportedEncodingException ee) {
			    if (log.isWarnEnabled()) {
				log.warn("Unsupported encoding " + e, ee);
			    }
			}
                    }
                }
            });

            return result;
        } catch (Exception eee) {
            log.error(String.format("JRST parsing error line %d char %s:\n%s", lexer
                    .getLineNumber(), lexer.getCharNumber(), lexer
                    .readNotBlanckLine()));
            throw eee;
        }
    }

    /**
     * <p>
     * exemple :
     * </p>
     *
     * <pre>
     * ..contents : Sommaire
     *   depth: 3
     * </pre>
     *
     * <p>
     * depth sert a limiter la profondeur du sommaire
     * </p>
     *
     * @param e Element
     *
     */
    private void composeContents(Element e) {
        Element result = DocumentHelper.createElement(TOPIC);
        String option = e.getText();
        int depth = -1;
        // depth: 3
        Pattern pattern = Pattern.compile("\\s*\\:depth\\:\\s*\\p{Digit}+");
        Matcher matcher = pattern.matcher(option);
        if (matcher.matches()) {
            pattern = Pattern.compile("\\p{Digit}+");
            matcher = pattern.matcher(matcher.group());
            if (matcher.find()) {
                depth = Integer.parseInt(matcher.group());
            }
        }
        int levelInit = 0;
        boolean noTitle = false;

        try {
            levelInit = Integer.parseInt(eTitle.getFirst().attributeValue(
            		LEVEL));
        } catch (NumberFormatException eee) {
            log.error("Can't parse level in: "
                                    + eTitle.getFirst().asXML(), eee);
            return;
        } catch (NoSuchElementException eee) {
            noTitle = true;
        }

        LinkedList<Element> title = new LinkedList<Element>();
        // on rajoute les refid
        for (int i = 0; i < eTitle.size(); i++) {
            idMax++;
            eTitle.get(i).addAttribute(ATTR_REFID, ID + idMax);
        }
        // on enleve les titres limites par depth
        for (Element el : eTitle) {
            int level = Integer.parseInt(el.attributeValue(LEVEL));
            level = level - levelInit;
            el.addAttribute(LEVEL, "" + level);
            if (depth == -1) {
                title.add(el);
            }
            else {
                if (depth > level) {
                    title.add(el);
                }
            }
        }
        e.addAttribute(CLASS, CONTENTS);
        String titleValue = e.attributeValue(VALUE);
        e.addAttribute(VALUE, null);
        String value = titleValue.trim().toLowerCase();
        // sans titre c "contents" par default
        if (value.matches("\\s*")) {
            value = CONTENTS;
            titleValue = "Contents";
        }
        e.addAttribute(ATTR_IDS, value);
        e.addAttribute(NAMES, value);
        result.addElement(TITLE).setText(titleValue);
        // on compose les lignes
        if (!noTitle) { //Si il y a des titres à lier à la table des matières
            result.add(composeLineContent(title, ""));
        }
        e.setText("");
        e.appendContent(result);
    }

    /**
     * @param title
     *            <Element> title, String num
     * @return Element
     */
    private Element composeLineContent(LinkedList<Element> title, String num) {
        Element result = DocumentHelper.createElement(BULLET_LIST);
        if (sectnum) {
            result.addAttribute(CLASS, "auto-toc");
        }
        Element item = null;
        int cnt = 0;
        while (!title.isEmpty()) {

            Element e = title.getFirst();
            int level = Integer.parseInt(e.attributeValue(LEVEL));
            LinkedList<Element> child = new LinkedList<Element>();

            if (level <= 0) {
                cnt++;
                title.removeFirst();
                item = result.addElement(LIST_ITEM);
                Element para = item.addElement(PARAGRAPH);
                Element reference = para.addElement(REFERENCE);
                String text = e.getText();
                String id = e.attributeValue(ATTR_REFID);
                reference.addAttribute(ATTR_IDS, id);
                reference.addAttribute(ATTR_REFID, text.replaceAll("\\W+", " ")
                        .trim().toLowerCase().replaceAll("\\W+", "-"));
                // si l'on doit les numeroter
                if (sectnum) {
                    Element generated = reference.addElement(GENERATED)
                            .addAttribute(CLASS, SECTNUM);
                    generated.setText(num + cnt + "   ");
                    for (int i = 0; i < eTitle.size(); i++) {
                        if (eTitle.get(i).attributeValue(ATTR_REFID).equals(id)) {
                            Element generatedTitle = eTitle.get(i).addElement(
                                    GENERATED);
                            generatedTitle.addAttribute(CLASS, SECTNUM);
                            generatedTitle.setText(num + cnt + "   ");
                        }

                    }
                }

                text = text.trim();
                text = text.replaceAll("_", "");

                text = REGEX_STRONG.matcher(text).replaceAll(
                        "<" + STRONG + ">$1</" + STRONG + ">");
                text = REGEX_EMPHASIS.matcher(text).replaceAll(
                        "<" + EMPHASIS + ">$1</" + EMPHASIS + ">");

                try {
                    Element textElement = DocumentHelper.parseText("<TMP>" + text + "</TMP>").getRootElement();
                    reference.appendContent(textElement);

                } catch (DocumentException eee) {
                    if (log.isWarnEnabled()) {
                        log.warn("Can't inline text for " + e, eee);
                    }
                }

            } else {
                do {
                    e.addAttribute(LEVEL, "" + (level - 1));
                    child.add(e);
                    title.removeFirst();
                    if (!title.isEmpty()) {
                        e = title.getFirst();
                        level = Integer.parseInt(e.attributeValue(LEVEL));
                    }
                } while (!title.isEmpty() && level > 0);
                String numTmp = "";
                // numerotation
                if (sectnum) {
                    numTmp = num + cnt + ".";
                }
                if (item != null) {
                    item.add(composeLineContent(child, numTmp)); // Appel
                    // recursif
                } else {
                    result.add(composeLineContent(child, numTmp)); // Appel
                    // recursif
                }
            }
        }
        return result;
    }

    /**
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeDocument(JRSTLexer lexer) throws Exception {
        Element result = DocumentHelper.createElement(DOCUMENT);
        result.addAttribute(LEVEL, String.valueOf(MAX_SECTION_DEPTH - 1));

        Element item = null;

        // skip blank line
        skipBlankLine(lexer);

        // les liens anonymes
        LinkedList<Element> items = lexer.refTarget();
        for (Element e : items) {
            eTarget.add(e);

        }

        // le header
        item = lexer.peekHeader();
        if (itemEquals(HEADER, item)) {
            Element decoration = result.addElement(DECORATION);
            Element header = decoration.addElement(HEADER);
            header.addAttribute(ATTR_INLINE, TRUE).setText(item.getText());
        }

        // le footer
        item = lexer.peekFooter();
        if (itemEquals(FOOTER, item)) {
            footer = DocumentHelper.createElement(DECORATION);
            Element header = footer.addElement(FOOTER);
            header.addAttribute(ATTR_INLINE, TRUE).setText(item.getText());
        }

        // les hyperlinks
        LinkedList<Element> listItem = lexer.peekTargetAnonymous();
        if (listItem != null) {
            for (Element e : listItem) {
                Element anonym = DocumentHelper.createElement(TARGET);
                anonym.addAttribute(ANONYMOUS, "1");
                idMax++;
                anonym.addAttribute(ATTR_IDS, ID + idMax);

                anonym.addAttribute(REFURI, e.attributeValue(REFURI).trim());

                eTargetAnonymous.add(anonym);
                eTargetAnonymousCopy.add(anonym);
            }
        }

        // les eléments a enlever (deja parser : header, footer...)
        item = lexer.peekRemove();
        if (itemEquals(REMOVE, item)) {
            lexer.remove();
        }

        // skip blank line
        skipBlankLine(lexer);

        // les commentaires
        List<Element> comments = lexer.peekAllComment();

        // skip blank line
        skipBlankLine(lexer);

        // le titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element title = result.addElement(TITLE);
            String txt = item.getText();
            result.addAttribute(ATTR_IDS, txt.replaceAll("[(\\W+)_]", " ")
                    .toLowerCase().trim().replaceAll("\\s+", "-"));
            result.addAttribute(NAMES, txt.toLowerCase().replaceAll(
                    "[(\\W+)_&&[^\\:]]+", " ").trim());
            copyLevel(item, title);
            title.addAttribute(ATTR_INLINE, TRUE).setText(txt.trim());
        }

        // skip blank line
        skipBlankLine(lexer);

        // le sous titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element subtitle = result.addElement(SUBTITLE);
            String txt = item.getText();
            subtitle.addAttribute(ATTR_IDS, txt.replaceAll("[(\\W+)_]", " ")
                    .toLowerCase().trim().replaceAll("\\s+", "-"));
            subtitle.addAttribute(NAMES, txt.toLowerCase().replaceAll(
                    "[(\\W+)_]", " ").trim());
            copyLevel(item, subtitle);
            DocumentHelper.createElement(FOOTNOTES);
            subtitle.addAttribute(ATTR_INLINE, TRUE).setText(txt.trim());
        }

        // skip blank line
        skipBlankLine(lexer);

        // les infos du doc
        item = lexer.peekDocInfo();
        Element documentinfo = null;
        while (itemEquals(DOCINFO, item) || itemEquals(FIELD_LIST, item)) {

            if (documentinfo == null) {
                documentinfo = result.addElement(DOCINFO);
            }
            skipBlankLine(lexer);
            if (itemEquals(FIELD_LIST, item)) {
                Element field = composeFieldItemList(lexer);
                documentinfo.add(field);
            } else {
                if ("author".equalsIgnoreCase(item.attributeValue(TYPE))) {
                    documentinfo.addElement(AUTHOR).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText());
                } else if ("date".equalsIgnoreCase(item.attributeValue(TYPE))) {
                    documentinfo.addElement(DATE)
                            .addAttribute(ATTR_INLINE, TRUE).setText(
                                    item.getText().trim());
                } else if ("organization".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(ORGANIZATION).addAttribute(
                            ATTR_INLINE, TRUE).setText(item.getText().trim());
                } else if ("contact".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(CONTACT).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("address".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(ADDRESS).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("version".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(VERSION).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("revision".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(REVISION).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("status".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(STATUS).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("copyright".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    documentinfo.addElement(COPYRIGHT).addAttribute(ATTR_INLINE,
                            TRUE).setText(item.getText().trim());
                } else if ("authors".equalsIgnoreCase(item
                        .attributeValue(TYPE))) {
                    Element authors = documentinfo.addElement(AUTHORS);
                    int t = 0;
                    String line = item.getText();
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ';' || line.charAt(i) == ',') {
                            authors.addElement(AUTHOR).addAttribute(ATTR_INLINE,
                                    TRUE)
                                    .setText(line.substring(t, i).trim());
                            t = i + 1;
                        }

                    }
                    authors.addElement(AUTHOR).addAttribute(ATTR_INLINE, TRUE)
                            .setText(line.substring(t, line.length()).trim());
                }
                lexer.remove();
            }
            // skip blank line
            // skipBlankLine(lexer);
            item = lexer.peekDocInfo();

        }

         // Ajout des commentaires
    //        System.out.println(comment.asXML());
        for (Element comment : comments){
            result.add(composeComment(comment));
        }

        // l'abstract du doc
        item = lexer.peekTitle();
        while (itemNotEquals(TITLE, item) && !lexer.eof()) {
            composeBody(lexer, result);
            item = lexer.peekTitle();
        }

        // les sections
        item = lexer.peekTitle();
        while (itemEquals(TITLE, item, true, lexer.eof())) {
            Element section = composeSection(lexer);
            result.add(section);
            item = lexer.peekTitle();
        }

        // on ajoute le footer a la fin
        if (footer != null) {
            result.add(footer);
        }

        return result;
    }

    /**
     * <p>
     * skip blank line
     * </p>
     *
     * @param lexer
     * @throws DocumentException
     * @throws IOException
     */
    private void skipBlankLine(JRSTLexer lexer) throws IOException,
            DocumentException {
        Element item = lexer.peekBlankLine();
        // skip blank line
        while (itemEquals(JRSTLexer.BLANK_LINE, item)) {
            // go to the next element
            lexer.remove();
            item = lexer.peekBlankLine();
        }
    }

    /**
     * *
     * <p>
     * Corps du document
     * </p>
     *
     * @param lexer
     * @return Element
     * @throws DocumentException
     * @throws IOException
     */
    private Element composeBody(JRSTLexer lexer, Element parent)
            throws Exception {

        Element item = lexer.peekTitleOrBodyElement();
        if (item == null && !lexer.eof()) {
            item = lexer.peekTitleOrBodyElement();
        }

        while (!lexer.eof() && itemNotEquals(TITLE, item)
                && isUpperLevel(item, parent)) {
            if (itemEquals(JRSTLexer.BLANK_LINE, item)) {
                // go to the next element
                lexer.remove();
            } else if (itemEquals(REMOVE, item)) {
                lexer.remove();
            } else if (itemEquals(INCLUDE, item)) {
                lexer.remove();
                Element list = composeInclude(item);
                parent.add(list);
            } else if (itemEquals(DOCTEST_BLOCK, item)) {
                lexer.remove();
                Element list = composeDoctestBlock(item);
                parent.add(list);
            } else if (itemEquals(ADMONITION, item)) {
                lexer.remove();
                Element list = composeAdmonition(item);
                parent.add(list);
            } else if (itemEquals(SIDEBAR, item)) {
                lexer.remove();
                Element list = composeSidebar(item);
                parent.add(list);
            } else if (itemEquals(TOPIC, item)) {
                lexer.remove();
                Element list = composeTopic(item);
                parent.add(list);
            } else if (itemEquals(TRANSITION, item)) {
                lexer.remove();
                Element para = parent.addElement(TRANSITION);
                copyLevel(item, para);
            } else if (itemEquals(PARAGRAPH, item)) {
                lexer.remove();
                Element para = parent.addElement(PARAGRAPH);
                copyLevel(item, para);
                para.addAttribute(ATTR_INLINE, TRUE).setText(item.getText());
            } else if (itemEquals(JRSTLexer.DIRECTIVE, item)) {
                lexer.remove();
                Node directive = composeDirective(item);
                parent.add(directive);
            } else if (itemEquals(SUBSTITUTION_DEFINITION, item)) {
                lexer.remove();
                Element subst = composeSubstitutionDefinition(item);
                parent.add(subst);
            } else if (itemEquals(LITERAL_BLOCK, item)) {
                lexer.remove();
                Element para = parent.addElement(LITERAL_BLOCK);
                copyLevel(item, para);
                para.setText(item.getText());
            } else if (itemEquals(JRSTLexer.TABLE, item)) {
                lexer.remove();
                Element table = composeTable(item);
                parent.add(table);
                // Element para = parent.addElement(TABLE);
                // copyLevel(item, para);
                // para.setText(item.getText());
            } else if (itemEquals(LINE_BLOCK, item)) {
                lexer.remove();
                Element list = composeLineBlock(lexer, item);
                parent.add(list);
            } else if (itemEquals(BULLET_LIST, item)) {
                Element list = composeBulletList(lexer);
                parent.add(list);
            } else if (itemEquals(ENUMERATED_LIST, item)) {
                Element list = composeEnumeratedList(lexer);
                parent.add(list);
            } else if (itemEquals(DEFINITION_LIST, item)) {
                Element list = composeDefinitionList(lexer);
                parent.add(list);
            } else if (itemEquals(FIELD_LIST, item)) {
                Element list = composeFieldList(lexer);
                parent.add(list);
            } else if (itemEquals(BLOCK_QUOTE, item)) {
                lexer.remove();
                Element list = composeBlockQuote(item);
                parent.add(list);
            } else if (itemEquals(OPTION_LIST, item)) {
                Element list = composeOptionList(lexer);
                parent.add(list);
            } else if (itemEquals(TARGET, item)) {
                lexer.remove();
                Element list = composeTarget(item);
                if (list != null) {
                    try {
                	parent.add(list);
                    } catch (IllegalAddException e) {}
                } else
                    System.err.println("Unknown target name : \"" + item.attributeValue(ATTR_IDS) + "\"");
            } else if (itemEquals(TARGETANONYMOUS, item)) {
                lexer.remove();
                Element list = composeTargetAnonymous(item);
                parent.add(list);
            } else if (itemEquals(FOOTNOTES, item)) {
                lexer.remove();
                Element[] list = composeFootnote(item);
                for (Element l : list) {
                    parent.add(l);
                }
            } else if (itemEquals(COMMENT, item)) {
                lexer.remove();
                Element list = composeComment(item);
                parent.add(list);
            }

            else {
                if (ERROR_MISSING_ITEM) {
                    throw new DocumentException("Unknow item type: "
                            + item.getName());
                } else {
                    lexer.remove();
                }
            }

            // Pour afficher le "PseudoXML"
            // if (item!=null) System.out.println(item.asXML());

            item = lexer.peekTitleOrBodyElement();
        }

        return parent;
    }

    /**
     * <p>
     * include un document rst
     * </p>
     *
     * <pre>
     * .. include:: doc.rst
     * </pre>
     *
     * <p>
     * include un document literal (code...)
     * </p>
     *
     * <pre>
     * .. include:: literal
     *       doc.rst
     * </pre>
     *
     * @param item
     * @return Element
     * @throws Exception
     */
    private Element composeInclude(Element item) throws Exception {
        String option = item.attributeValue(OPTION);
        String path = item.getText();
        Element result = null;
        if (option.equals(LITERAL)) {
            result = DocumentHelper.createElement(LITERAL_BLOCK);
            FileReader reader = new FileReader(path);
            BufferedReader bf = new BufferedReader(reader);
            String line = "";
            String lineTmp = bf.readLine();
            while (lineTmp != null) {
                line += '\n' + lineTmp;
                lineTmp = bf.readLine();
            }
            result.setText(line);
        } else {
            File fileIn = new File(path);
            URL url = fileIn.toURI().toURL();
            Reader in = new InputStreamReader(url.openStream());

            Document doc = newJRSTReader(in);

            result = doc.getRootElement();
        }
        return result;
    }

    /**
     * <pre>
     * ..
     *   So this block is not &quot;lost&quot;,
     *   despite its indentation.
     * </pre>
     *
     * @param item
     * @return Element
     */
    private Element composeComment(Element item) {

        return item;
    }

    /**
     * <pre>
     * __ http://truc.html
     * </pre>
     *
     * @param item
     * @return Element
     */
    private Element composeTargetAnonymous(Element item) {
        Element result = null;
        result = eTargetAnonymousCopy.getFirst();
        eTargetAnonymousCopy.removeFirst();
        return result;
    }

    /**
     * <pre _ target: target.html </pre>
     *
     * @param item
     * @return Element
     */
    private Element composeTarget(Element item) {
        Element result = null;
        for (Element e : eTarget) {
            if (e.attributeValue(ID).equals(item.attributeValue(ID))) {
                result = e;
            }
        }
        return result;
    }

    /**
     * <pre>
     * .. [#] This is a footnote
     * </pre>
     *
     * @param item
     * @return Element
     * @throws Exception
     */
    private Element[] composeFootnote(Element item) throws Exception {
        Element[] result = null;
        if (itemEquals(FOOTNOTES, item)) {
            List<Element> footnotes = (List<Element>) item
                    .selectNodes(FOOTNOTE);
            result = new Element[footnotes.size()];
            int cnt = 0;
            for (Element footnote : footnotes) {
                result[cnt] = DocumentHelper.createElement(FOOTNOTE);
                Element efootnote = DocumentHelper.createElement(FOOTNOTE);
                int labelMax = 0;

                for (int i = 0; i < lblFootnotes.size(); i++) {
                    int lbl = lblFootnotes.get(i);
                    labelMax = Math.max(lbl, labelMax);
                }

                boolean[] labels = new boolean[labelMax];
                for (int i = 0; i < labels.length; i++) {
                    labels[i] = false;
                }
                for (int i = 0; i < lblFootnotes.size(); i++) {
                    labels[lblFootnotes.get(i) - 1] = true;
                }
                idMax++;
                String name = null;
                String id = "";
                String label = null;
                String type = footnote.attributeValue(TYPE);
                if (type.equals(AUTONUM) || type.equals(AUTONUMLABEL)) {
                    result[cnt].addAttribute(AUTO, "1");
                }
                if (type.equals(AUTOSYMBOL)) {
                    result[cnt].addAttribute(AUTO, "*");
                }
                result[cnt].addAttribute(BACKREFS, ID + idMax);
                efootnote.addAttribute(BACKREFS, ID + idMax);
                if (type.equals(NUM) || type.equals(AUTONUMLABEL)) {
                    name = footnote.attributeValue(NAME);
                    if (type.equals(AUTONUMLABEL)) {
                        id = name;
                    }
                    else {
                        label = name;
                    }
                }
                if (type.equals(AUTONUM) || type.equals(AUTONUMLABEL)) {
                    boolean done = false;

                    for (int i = 0; i < labels.length && !done; i++) {
                        if (!labels[i]) {
                            done = true;
                            label = "" + (i + 1);
                        }
                    }
                    if (!done) {
                        label = "" + (labels.length + 1);
                    }
                    if (type.equals(AUTONUM)) {
                        name = label;
                    }
                }
                if (type.equals(AUTOSYMBOL)) {

                    int nb = Math.abs(symbolMax / 10) + 1;
                    char symbol = FOOTNOTE_SYMBOL.charAt(symbolMax % 10);
                    label = "";
                    for (int j = 0; j < nb; j++) {
                        label += symbol;
                    }
                    symbolMax++;

                }
                result[cnt].addAttribute(ATTR_IDS, "" + id);
                efootnote.addAttribute(ATTR_IDS, "" + id);
                if (!type.equals(AUTOSYMBOL)) {
                    result[cnt].addAttribute(NAME, "" + name);
                    efootnote.addAttribute(NAME, "" + name);
                }
                result[cnt].addElement(LABEL).setText("" + label);
                efootnote.addAttribute(LABEL, "" + label);
                if (!type.equals(AUTOSYMBOL)) {
                    lblFootnotes.add(Integer.parseInt(label));
                }
                efootnote.addAttribute(TYPE, type);
                eFootnotes.add(efootnote);
                String text = footnote.getText();
                Document doc = newJRSTReader(new StringReader(text));
                result[cnt].appendContent(doc.getRootElement());

                cnt++;
            }
        }
        for (int i = 0; i < result.length; i++) {
            if (result[i].attributeValue(ATTR_IDS).equals("")) {
                idMax++;
                result[i].addAttribute(ATTR_IDS, ID + idMax);
                (eFootnotes.get(i)).addAttribute(ATTR_IDS, ID + idMax);
            }

        }

        return result;
    }

    /**
     * <pre>
     * -a command-line option &quot;a&quot; -1 file, --one=file, --two file Multiple
     * options with arguments.
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     * @throws DocumentException
     */
    private Element composeOptionList(JRSTLexer lexer)
            throws DocumentException, Exception {
        Element item = lexer.peekOption();
        Element result = DocumentHelper.createElement(OPTION_LIST);
        while (itemEquals(OPTION_LIST, item)) {
            lexer.remove();
            Element optionListItem = result.addElement(OPTION_LIST_ITEM);
            Element optionGroup = optionListItem.addElement(OPTION_GROUP);
            List<Element> option = (List<Element>) item.selectNodes(OPTION);
            for (Element e : option) {
                Element eOption = optionGroup.addElement(OPTION);
                eOption.addElement(OPTION_STRING).setText(
                        e.attributeValue(OPTION_STRING));
                if (e.attributeValue(DELIMITEREXISTE).equals(TRUE)) {
                    eOption.addElement(OPTION_ARGUMENT).addAttribute(
                            DELIMITER, e.attributeValue(DELIMITER))
                            .setText(e.attributeValue(OPTION_ARGUMENT));
                }
            }
            Element description = optionListItem.addElement(DESCRIPTION);

            String text = item.getText();
            Document doc = newJRSTReader(new StringReader(text));
            description.appendContent(doc.getRootElement());

            item = lexer.peekOption();
        }
        return result;
    }

    /**
     * <pre>
     * .. topic:: Title
     *
     *    Body.
     * </pre>
     *
     * @param item Element
     *            item
     * @return Element
     * @throws Exception
     */

    private Element composeTopic(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(TOPIC);
        result.addElement(TITLE).addAttribute(ATTR_INLINE, TRUE).setText(
                item.attributeValue(TITLE));
        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());

        return result;
    }

    /**
     * <pre>
     * .. sidebar:: Title
     *    :subtitle: If Desired
     *
     *    Body.
     * </pre>
     *
     * @param item Element
     * @return Element
     * @throws Exception
     */

    private Element composeSidebar(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(SIDEBAR);
        result.addElement(TITLE).addAttribute(ATTR_INLINE, TRUE).setText(
                item.attributeValue(TITLE));
        if (item.attributeValue(SUBEXISTE).equals(TRUE)) {
            result.addElement(SUBTITLE).addAttribute(ATTR_INLINE, TRUE).setText(
                    item.attributeValue(SUBTITLE));
        }

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());

        return result;
    }

    /**
     * <pre>
     * | line block
     * |
     * |    indent
     * </pre>
     *
     * @param lexer
     * @param item
     * @return Element
     * @throws Exception
     */
    private Element composeLineBlock(JRSTLexer lexer, Element item)
            throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(LINE_BLOCK);
        List<Element> lines = (List<Element>) item.selectNodes(LINE);
        int[] levels = new int[lines.size()];
        int cnt = 0;
        for (Element l : lines) {
            levels[cnt] = Integer.parseInt(l.attributeValue(LEVEL));
            cnt++;
        }
        cnt = 0;
        boolean[] lineDone = new boolean[lines.size()];
        for (int i = 0; i < lineDone.length; i++) {
            lineDone[i] = false;
        }
        for (Element l : lines) {
            if (levels[cnt] == 0) {
                result.addElement(LINE).addAttribute(ATTR_INLINE, TRUE).setText(
                        l.getText());
            }
            else {
                if (!lineDone[cnt]) {
                    Element newItem = DocumentHelper.createElement(LINE_BLOCK);
                    Boolean done = false;
                    for (int i = cnt; i < lines.size() && !done; i++) {
                        if (levels[i] > 0) {
                            Element eLine = newItem.addElement(LINE);
                            eLine.addAttribute(LEVEL, "" + (levels[i] - 1));
                            eLine.setText(lines.get(i).getText());
                            lineDone[i] = true;
                        } else {
                            done = true;
                        }

                    }
                    Element eLineBlock = result.addElement(LINE_BLOCK);
                    // Appel recursif
                    eLineBlock.appendContent(composeLineBlock(lexer, newItem));
                }
            }
            cnt++;

        }
        return result;
    }

    /**
     * <pre>
     * &gt;&gt;&gt; print 'this is a Doctest block'
     * this is a Doctest block
     * </pre>
     *
     * @param item Element
     * @return Element
     */
    private Element composeDoctestBlock(Element item) {
        return item;
    }

    /**
     * <pre>
     * As a great paleontologist once said,
     *
     *      This theory, that is mine, is mine.
     *
     *      -- Anne Elk (Miss)
     * </pre>
     *
     * @param item
     * @return Element
     * @throws Exception
     *
     */
    private Element composeBlockQuote(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(BLOCK_QUOTE);

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());
        String sAttribution = item.attributeValue(ATTRIBUTION);
        if (sAttribution != null) {
            Element attribution = result.addElement(ATTRIBUTION);
            attribution.setText(sAttribution);
            attribution.addAttribute(ATTR_INLINE, TRUE);
        }
        return result;
    }

    /**
     * <pre>
     * .. admonition:: And, by the way...
     *
     *      You can make up your own admonition too.
     * </pre>
     *
     * @param item
     * @return Element
     * @throws Exception
     *
     */
    private Element composeAdmonition(Element item) throws Exception {
        Element result = null;
        if (item.attributeValue(TYPE).equalsIgnoreCase(ADMONITION)) {
            result = DocumentHelper.createElement(ADMONITION);
            String title = item.attributeValue(TITLE);
            String admonitionClass = "admonition_" + title;
            admonitionClass = admonitionClass.toLowerCase().replaceAll(
                    "\\p{Punct}", "");
            admonitionClass = admonitionClass.replace(' ', '-');
            admonitionClass = admonitionClass.replace('\n', '-');
            result.addAttribute(CLASS, admonitionClass);
            result.addElement(TITLE).addAttribute(ATTR_INLINE, TRUE).setText(
                    title.trim());
        } else {
            result = DocumentHelper.createElement(item.attributeValue(TYPE)
                    .toLowerCase());
        }

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());
        return result;
    }

    /**
     * parse all directives
     *
     * @param item
     * @return Node
     */
    private Node composeDirective(Element item) {
        Node result = item;
        String type = item.attributeValue(JRSTLexer.DIRECTIVE_TYPE);
        if (type.equals(SECTNUM)) {
            sectnum = true;
        }
        JRSTDirective directive = getDirective(type);
        if (directive == null) {
            directive = getDefaultDirective(type);
        }
        if (directive != null) {
            result = directive.parse(item);
        } else {
            log.warn("Unknow directive type '" + type + "' in: " + item);
        }
        return result;
    }

    /**
     * <pre>
     * .. |biohazard| image:: biohazard.png
     * </pre>
     *
     * @param item Element
     * @return Element
     */
    private Element composeSubstitutionDefinition(Element item) {
        Element result = item;
        Element child = (Element) item.selectSingleNode("*");
        Node newChild = composeDirective(child);
        result.remove(child); // remove old after composeDirective, because
        // directive can be used this parent
        result.add(newChild);
        return result;
    }

    /**
     * <p>
     * Complexe Table
     * </p>
     *
     * <pre>
     * +------------------------+------------+---------------------+
     * | body row 3             | Cells may  | - Table cells       |
     * +------------------------+ span rows. | - contain           |
     * | body row 4             |            | - body elements.    |
     * +------------------------+------------+---------------------+
     * </pre>
     *
     * <p>
     * And simple table
     * </p>
     *
     * <pre>
     * =====  =====  ======
     *    Inputs     Output
     * ============  ======
     *   A      B    A or B
     * ------------  ------
     *   A      B    A or B
     * =====  =====  ======
     * </pre>
     *
     * @param item
     * @return Element
     *
     */
    private Element composeTable(Element item) throws Exception {

        Element result = DocumentHelper.createElement(TABLE);

        int tableWidth = Integer.parseInt(item
                .attributeValue(JRSTLexer.TABLE_WIDTH));

        TreeSet<Integer> beginCellList = new TreeSet<Integer>();

        for (Element cell : (List<Element>) item.selectNodes(JRSTLexer.ROW
                + "/" + JRSTLexer.CELL)) {
            Integer begin = Integer.valueOf(cell
                    .attributeValue(JRSTLexer.CELL_INDEX_START));
            beginCellList.add(begin);
        }

        int[] beginCell = new int[beginCellList.size() + 1]; // + 1 to put
        // table width
        // to simulate
        // new cell
        int[] lengthCell = new int[beginCellList.size()];

        int cellNumber = 0;
        for (int b : beginCellList) {
            beginCell[cellNumber] = b;
            if (cellNumber > 0) {
                lengthCell[cellNumber - 1] = beginCell[cellNumber]
                        - beginCell[cellNumber - 1];
            }
            cellNumber++;
        }
        beginCell[cellNumber] = tableWidth;
        lengthCell[cellNumber - 1] = beginCell[cellNumber]
                - beginCell[cellNumber - 1];

        Element tgroup = result.addElement(TGROUP).addAttribute("cols",
                String.valueOf(cellNumber));
        for (int width : lengthCell) {
            tgroup.addElement(COLSPEC).addAttribute("colwidth",
                    String.valueOf(width));
        }

        Element rowList = null;
        if (TRUE.equals(item.attributeValue(JRSTLexer.TABLE_HEADER))) {
            rowList = tgroup.addElement(THEAD);
        } else {
            rowList = tgroup.addElement(TBODY);
        }
        List<Element> rows = (List<Element>) item.selectNodes(JRSTLexer.ROW);
        for (int r = 0; r < rows.size(); r++) {
            Element row = rowList.addElement(ROW);
            List<Element> cells = (List<Element>) rows.get(r).selectNodes(
                    JRSTLexer.CELL);
            for (int c = 0; c < cells.size(); c++) {
                Element cell = cells.get(c);
                // si la cellule a ete utilise pour un regroupement vertical on
                // la passe
                if (!TRUE.equals(cell.attributeValue("used"))) {
                    Element entry = row.addElement(ENTRY);
                    String text = "";

                    // on regroupe les cellules verticalement
                    int morerows = -1;
                    Element tmpCell = null;
                    String cellStart = cell
                            .attributeValue(JRSTLexer.CELL_INDEX_START);
                    do {
                        morerows++;
                        tmpCell = (Element) rows.get(r + morerows)
                                .selectSingleNode(
                                        JRSTLexer.CELL + "[@"
                                                + JRSTLexer.CELL_INDEX_START
                                                + "=" + cellStart + "]");
                        text += tmpCell.getText();
                        // on marque la cellule comme utilise
                        tmpCell.addAttribute("used", TRUE);
                    } while (!TRUE.equals(tmpCell
                            .attributeValue(JRSTLexer.CELL_END)));

                    if (morerows > 0) {
                        entry
                                .addAttribute("morerows", String
                                        .valueOf(morerows));
                    }

                    // on compte le nombre de cellules regroupees
                    // horizontalement
                    int morecols = 0;
                    tmpCell = cells.get(c + morecols);
                    int cellEnd = Integer.parseInt(tmpCell
                            .attributeValue(JRSTLexer.CELL_INDEX_END));
                    while (cellEnd + 1 != beginCell[c + morecols + 1]) {
                        morecols++;
                        // tmpCell = cells.get(c + morecols);
                        // cellEnd =
                        // Integer.parseInt(tmpCell.attributeValue(JRSTLexer.
                        // CELL_INDEX_END));
                    }
                    if (morecols > 0) {
                        entry
                                .addAttribute("morecols", String
                                        .valueOf(morecols));
                    }
                    // parse entry text in table
                    Document doc = newJRSTReader(new StringReader(text));
                    entry.appendContent(doc.getRootElement());
                }
            }
            if (TRUE.equals(rows.get(r).attributeValue(
                    JRSTLexer.ROW_END_HEADER))) {
                rowList = tgroup.addElement(TBODY);
            }
        }

        return result;
    }

    /**
     * <p>
     * items begin with "-", "+", or "*"
     * </p>
     *
     * <pre>
     * * aaa
     *   - bbb
     * * ccc
     *   - ddd
     *      + eee
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeBulletList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBulletList();
        Element result = DocumentHelper.createElement(BULLET_LIST);
        copyLevel(item, result);
        result.addAttribute(BULLET, item.attributeValue(BULLET));
        while (itemEquals(BULLET_LIST, item) && isSameLevel(item, result)
                && hasSameAttribute(item, result, BULLET)) {
            lexer.remove();
            Element bullet = result.addElement(LIST_ITEM);
            copyLevel(item, bullet);
            bullet.addElement(PARAGRAPH).addAttribute(ATTR_INLINE, TRUE)
                    .setText(item.getText());
            composeBody(lexer, bullet);

            item = lexer.peekBulletList();
        }
        return result;
    }

    /**
     * <pre>
     * 3. et meme
     * * #. pour voir
     * * I) de tout
     * (a) pour tout
     * (#) vraiment tout
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeEnumeratedList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekEnumeratedList();
        Element result = DocumentHelper.createElement(ENUMERATED_LIST);
        copyLevel(item, result);
        String enumType = item.attributeValue(ENUMTYPE);
        if (!enumType.equals("arabic")) {
            result.addAttribute(START, item.attributeValue(START));
        }
        result.addAttribute(PREFIX, item.attributeValue(PREFIX));
        result.addAttribute(SUFFIX, item.attributeValue(SUFFIX));
        result.addAttribute(ENUMTYPE, enumType);
        while (itemEquals(ENUMERATED_LIST, item)
                && isSameLevel(item, result)
                && hasSameAttribute(item, result, PREFIX, SUFFIX)
                && (AUTO.equals(item.attributeValue(ENUMTYPE)) || hasSameAttribute(
                        item, result, ENUMTYPE))) {
            lexer.remove();
            Element e = result.addElement(LIST_ITEM);
            copyLevel(item, e);
            e.addElement(PARAGRAPH).addAttribute(ATTR_INLINE, TRUE).setText(
                    item.getText());
            composeBody(lexer, e);

            item = lexer.peekEnumeratedList();
        }
        return result;
    }

    /**
     * <pre>
     * le mot : la classe
     *   la definition
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeDefinitionList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBodyElement();
        Element result = DocumentHelper.createElement(DEFINITION_LIST);
        copyLevel(item, result);
        while (itemEquals(DEFINITION_LIST, item) && isSameLevel(item, result)) {
            lexer.remove();
            Element def = result.addElement(DEFINITION_LIST_ITEM);
            copyLevel(item, def);

            Element term = def.addElement(TERM);
            copyLevel(item, term);
            term.addAttribute(ATTR_INLINE, TRUE).setText(
                    item.attributeValue("term"));

            String[] classifiers = StringUtils.split(item
                    .attributeValue("classifiers"), " : ");
            for (String classifierText : classifiers) {
                Element classifier = def.addElement("classifier");
                copyLevel(item, classifier);
                classifier.addAttribute(ATTR_INLINE, TRUE).setText(
                        classifierText);
            }

            Element definition = def.addElement(DEFINITION);
            definition.addElement(PARAGRAPH).addAttribute(ATTR_INLINE, TRUE)
                    .setText(item.getText());
            copyLevel(item, definition);

            composeBody(lexer, definition);

            item = lexer.peekBodyElement();
        }
        return result;
    }

    /**
     * <pre>
     * :un peu: de field
     *      ca ne fait pas
     *      de mal
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeFieldList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBodyElement();
        Element result = DocumentHelper.createElement(FIELD_LIST);
        copyLevel(item, result);
        while (itemEquals(FIELD_LIST, item) && isSameLevel(item, result)) {
            Element field = composeFieldItemList(lexer);
            result.add(field);
            item = lexer.peekBodyElement();
        }
        return result;
    }

    /**
     * <pre>
     * :field1: avec un
     *    petit texte
     *    - et meme un
     *    - debut
     *    - de list
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeFieldItemList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekFieldList();
        if (itemEquals(FIELD_LIST, item)) {
            lexer.remove();
            Element field = DocumentHelper.createElement(FIELD);
            copyLevel(item, field);
            Element fieldName = field.addElement(FIELD_NAME);
            copyLevel(item, fieldName);
            fieldName.addAttribute(ATTR_INLINE, TRUE).setText(
                    item.attributeValue(NAME));
            Element fieldBody = field.addElement(FIELD_BODY);
            fieldBody.addElement(PARAGRAPH).addAttribute(ATTR_INLINE, TRUE)
                    .setText(item.getText());
            copyLevel(item, fieldBody);
            composeBody(lexer, fieldBody);

            return field;
        } else {
            throw new DocumentException("Waiting for " + FIELD_LIST
                    + " and found " + item.getName());
        }
    }

    /**
     * <pre>
     * DEFINITIONS
     * -----------
     * </pre>
     *
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeSection(JRSTLexer lexer) throws Exception {
        Element result = DocumentHelper.createElement(SECTION);
        Element firstTitle = null;

        Element item = null;

        // le titre de la section
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item, true, lexer.eof())) {
            lexer.remove();
            firstTitle = item;
            Element title = result.addElement(TITLE);
            copyLevel(item, result);
            copyLevel(item, title);
            title.addAttribute(ATTR_INLINE, TRUE).setText(item.getText().trim());
            result.addAttribute(ATTR_IDS, item.getText().replaceAll("\\W+", " ")
                    .trim().toLowerCase().replaceAll("\\W+", "-"));
            result.addAttribute(NAME, item.getText().toLowerCase().trim());
            eTitle.add(title);
        }

        // le contenu de la section
        item = lexer.peekTitle();
        while (itemNotEquals(TITLE, item) && !lexer.eof()) {
            composeBody(lexer, result);
            item = lexer.peekTitle();
        }

        // les sous sections
        item = lexer.peekTitle();
        while (itemEquals(TITLE, item) && isUpperLevel(item, firstTitle)) {
            Element section = composeSection(lexer);
            result.add(section);
            item = lexer.peekTitle();
        }

        return result;
    }

    /**
     * Indique si la sous section est bien une sous section, c-a-d dire que son
     * level est superieur a celui de la section
     * 
     * @param subSection element
     * @param section element
     * @return boolean
     * @throws DocumentException
     */
    private boolean isUpperLevel(Element subSection, Element section)
            throws DocumentException {
        // if (!(itemEquals(SECTION, subSection) && itemEquals(SECTION,
        // section))
        // || itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
        // // all element is upper than Document or section
        // return true;
        // }
        int subSectionLevel = Integer.parseInt(subSection
                .attributeValue(LEVEL));
        int sectionLevel = Integer.parseInt(section.attributeValue(LEVEL));
        boolean result = subSectionLevel > sectionLevel;
        return result;
    }

    /**
     * Indique si les deux elements sont au meme niveau
     * 
     * @param subSection  element
     * @param section element
     * @return boolean
     * @throws DocumentException
     */
    private boolean isSameLevel(Element subSection, Element section)
            throws DocumentException {
        // if (itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
        // // all element is upper than Document or section
        // return false;
        // }
        int subSectionLevel = Integer.parseInt(subSection
                .attributeValue(LEVEL));
        int sectionLevel = Integer.parseInt(section.attributeValue(LEVEL));
        boolean result = subSectionLevel == sectionLevel;
        return result;
    }

    /**
     * @param e1 element1
     * @param e2 element2
     * @param attnames attribution names
     * @return boolean
     */
    private boolean hasSameAttribute(Element e1, Element e2, String... attnames) {
        boolean result = true;
        for (String attname : attnames) {
            String a1 = e1.attributeValue(attname);
            String a2 = e2.attributeValue(attname);
            if (!Objects.equals(a1, a2)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * @param from element
     * @param to element
     * @throws DocumentException
     */
    private void copyLevel(Element from, Element to) throws DocumentException {
        String level = from.attributeValue(LEVEL);
        if (level == null) {
            throw new DocumentException("Element without level: " + from);
        }
        to.addAttribute(LEVEL, level);
    }

    /**
     * @param name of element
     * @param e element
     * @return boolean
     * @throws DocumentException
     */
    private boolean itemEquals(String name, Element e) throws DocumentException {
        boolean result = itemEquals(name, e, false, false);
        return result;
    }

    /**
     * @param name name of element
     * @param e Element
     * @param throwError
     * @param eof
     * @return boolean
     * @throws DocumentException
     */
    private boolean itemEquals(String name, Element e, boolean throwError,
            boolean eof) throws DocumentException {
        boolean result = e != null && name.equals(e.getName());
        if (ERROR_MISSING_ITEM && !result && throwError && !eof) {
            throw new DocumentException("Malformed document waiting " + name
                    + " and found " + (e != null ? e.getName() : "null"));
        }
        return result;
    }

    /**
     * @param name Name of Element
     * @param  e Element
     * @return boolean
     */
    private boolean itemNotEquals(String name, Element e) {
        boolean result = e == null || !name.equals(e.getName());
        return result;
    }

    private Document newJRSTReader(Reader r) throws Exception {
        JRSTReader reader = new JRSTReader();
        reader.setVariable(idMax, symbolMax, symbolMaxRef, lblFootnotes,
                lblFootnotesRef, eFootnotes, eTarget, eTargetAnonymous,
                eTargetAnonymousCopy);

        return reader.read(r);

    }

    /**
     * <p>
     * Initialises les variables d'environements par ex, les hyperlinks peuvent
     * etre referencer dans tous le document
     * </p>
     * 
     * @param idMax
     * @param symbolMax
     * @param symbolMaxRef
     * @param lblFootnotes
     * @param lblFootnotesRef
     * @param eFootnotes
     * @param eTarget
     * @param eTargetAnonymous
     * @param eTargetAnonymousCopy
     */
    public void setVariable(int idMax, int symbolMax, int symbolMaxRef,
            LinkedList<Integer> lblFootnotes,
            LinkedList<Integer> lblFootnotesRef,
            LinkedList<Element> eFootnotes, LinkedList<Element> eTarget,
            LinkedList<Element> eTargetAnonymous,
            LinkedList<Element> eTargetAnonymousCopy) {
        this.idMax = idMax;
        this.symbolMax = symbolMax;
        this.symbolMaxRef = symbolMaxRef;
        this.lblFootnotes = lblFootnotes;
        this.lblFootnotesRef = lblFootnotesRef;
        this.eFootnotes = eFootnotes;
        this.eTarget = eTarget;
        this.eTargetAnonymous = eTargetAnonymous;
        this.eTargetAnonymousCopy = eTargetAnonymousCopy;
    }

    /**
     * Parse text in element and replace text with parse result
     * 
     * @param e element
     * @throws DocumentException
     * @throws UnsupportedEncodingException 
     */
    
    private void inline(Element e) throws DocumentException, UnsupportedEncodingException {
        String text = e.getText();

        text = StringEscapeUtils.escapeXml(text);
        // search all LITERAL and replace it with special mark
        // this prevent substitution in literal, example **something** must not
        // change in literal
        Map<String, String> temporaries = new HashMap<String, String>();
        Matcher matcher = REGEX_LITERAL.matcher(text);
        int index = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String literal = "<" + LITERAL + ">" + matcher.group(1) + "</"
                    + LITERAL + ">";
            String key = LITERAL + index++;
            temporaries.put(key, literal);
            text = text.substring(0, start) + "<tmp>" + key + "</tmp>"
                    + text.substring(end);
            matcher = REGEX_LITERAL.matcher(text);
        }
        // search all REGEX_INLINE_REFERENCE and replace it with special mark
        // this prevent substitution of URL with REGEX_REFERENCE. Use same
        // mechanisme as literal for that
        matcher = REGEX_INLINE_REFERENCE.matcher(text);
        index = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            Element ref = DocumentHelper.createElement(REFERENCE);
            ref.addAttribute(REFURI, StringEscapeUtils.unescapeXml(matcher.group(2)));
            ref.setText(StringEscapeUtils.unescapeXml(matcher.group(1)));
            String key = "inlineReference" + index++;
            temporaries.put(key, ref.asXML());
            text = text.substring(0, start) + "<tmp>" + key + "</tmp>"
                    + text.substring(end);
            matcher = REGEX_INLINE_REFERENCE.matcher(text);

        }
        // do all substitution inline
        text = REGEX_EMAIL.matcher(text).replaceAll(
                "$1<" + REFERENCE + " refuri='mailto:$2'>$2</" + REFERENCE
                        + ">$3");
        text = REGEX_STRONG.matcher(text).replaceAll(
                "<" + STRONG + ">$1</" + STRONG + ">");
        text = REGEX_EMPHASIS.matcher(text).replaceAll(
                "<" + EMPHASIS + ">$1</" + EMPHASIS + ">");
        text = REGEX_REFERENCE.matcher(text).replaceAll(
                "<" + REFERENCE + " refuri='$1'>$1</" + REFERENCE + ">$2");
        // _[#]truc
        matcher = REGEX_FOOTNOTE_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end()-1, text.length()-1);
            Element footnote = DocumentHelper.createElement(FOOTNOTE_REFERENCE);
            String sFootnote = matcher.group();
            boolean done = false;
            for (int i = 0; i < sFootnote.length() && !done; i++) {
                if (sFootnote.charAt(i) == ']') {
                    String id = sFootnote.substring(1, i);
                    if (id.equals("*")) {
                        int nb = Math.abs(symbolMaxRef / 10) + 1;
                        char symbol = FOOTNOTE_SYMBOL.charAt(symbolMaxRef % 10);
                        String label = "";
                        for (int j = 0; j < nb; j++) {
                            label += symbol;
                        }
                        symbolMaxRef++;
                        footnote.addAttribute(AUTO, "*");
                        for (int j = 0; j < eFootnotes.size(); j++) {
                            Element eFootnote = eFootnotes.get(j);
                            if (eFootnote.attributeValue(LABEL).equals(label)) {

                                footnote.addAttribute(ATTR_IDS, eFootnote
                                        .attributeValue(BACKREFS));
                                footnote.addAttribute(ATTR_REFID, eFootnote
                                        .attributeValue(ATTR_IDS));

                            }
                        }
                        footnote.setText(label);

                    } else if (id.matches("[1-9]+")) {

                        for (int j = 0; j < eFootnotes.size(); j++) {
                            Element eFootnote = eFootnotes.get(j);
                            if (eFootnote.attributeValue(LABEL).equals(id)) {
                                footnote.addAttribute(ATTR_IDS, eFootnote
                                        .attributeValue(BACKREFS));
                                footnote.addAttribute(ATTR_REFID, eFootnote
                                        .attributeValue(ATTR_IDS));
                            }
                        }
                        footnote.setText(id);
                        lblFootnotesRef.add(Integer.parseInt(id));

                    } else if (id.equals("#")) {
                        int lblMax = 0;
                        for (int j = 0; j < lblFootnotesRef.size(); j++) {
                            lblMax = Math.max(lblMax, lblFootnotesRef.get(j));
                        }

                        boolean[] lbls = new boolean[lblMax];
                        for (int j = 0; j < lbls.length; j++) {
                            lbls[j] = false;
                        }
                        for (int j = 0; j < lblFootnotesRef.size(); j++) {
                            lbls[lblFootnotesRef.get(j) - 1] = true;
                        }
                        boolean valide = false;
                        do {
                            boolean trouve = false;
                            String label = null;
                            for (int j = 0; j < lbls.length && !trouve; j++) {

                                if (!lbls[j]) {
                                    trouve = true;
                                    label = "" + (j + 1);
                                }
                            }
                            if (!trouve) {
                                label = "" + (lbls.length + 1);
                            }
                            footnote.addAttribute(AUTO, "1");
                            for (int j = 0; j < eFootnotes.size(); j++) {
                                Element eFootnote = eFootnotes.get(j);
                                if (eFootnote.attributeValue(LABEL).equals(
                                        label)) {
                                    if (!(eFootnote.attributeValue(TYPE)
                                            .equals(AUTONUMLABEL))) {
                                        footnote.addAttribute(ATTR_IDS, eFootnote
                                                .attributeValue(BACKREFS));
                                        footnote.addAttribute(ATTR_REFID,
                                                eFootnote.attributeValue(ATTR_IDS));
                                        footnote.setText(label);
                                        lblFootnotesRef.add(Integer
                                                .parseInt(label));
                                        valide = true;
                                    } else {
                                        valide = false;
                                        lbls[Integer.parseInt(label) - 1] = true;
                                    }
                                }
                            }
                        } while (!valide);

                    }

                    else {
                        footnote.addAttribute(AUTO, "1");

                        String name = id.substring(1);
                        boolean trouve = false;
                        for (int j = 0; j < eFootnotes.size() && !trouve; j++) {
                            Element eFootnote = eFootnotes.get(j);
                            if (eFootnote.attributeValue(NAMES).equals(name)) {
                                footnote.addAttribute(ATTR_IDS, eFootnote
                                        .attributeValue(BACKREFS));
                                footnote.addAttribute(ATTR_REFID, eFootnote
                                        .attributeValue(ATTR_IDS));
                                String label = eFootnote
                                        .attributeValue(LABEL);
                                footnote.setText(label);
                                lblFootnotesRef.add(Integer.parseInt(label));
                                trouve = true;
                            }
                        }

                        footnote.addAttribute(NAMES, name);
                    }
                    done = true;
                }
            }
            text = txtDebut + footnote.asXML() + txtFin;
            matcher = REGEX_FOOTNOTE_REFERENCE.matcher(text);
        }
        // .. __http://truc.html
        matcher = REGEX_ANONYMOUS_HYPERLINK_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end(), text.length());
            String ref = text.substring(matcher.start(), matcher.end() - 2);
            ref = ref.replaceAll("`", "");
            Element anonym = DocumentHelper.createElement(REFERENCE);
            anonym.addAttribute(ANONYMOUS, "1");
            anonym.addAttribute(NAME, ref.trim());
            if (!eTargetAnonymous.isEmpty()) {
                Element target = eTargetAnonymous.getFirst();
                eTargetAnonymous.removeFirst();
                anonym.addAttribute(REFURI, target.attributeValue(REFURI));
            }
            anonym.setText(ref);
            text = txtDebut + anonym.asXML() + txtFin;
            matcher = REGEX_ANONYMOUS_HYPERLINK_REFERENCE.matcher(text);
        }
        // .. _truc: http://truc.html
        matcher = REGEX_HYPERLINK_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end(), text.length());
            String ref = text.substring(matcher.start(), matcher.end() - 1);
            ref = StringEscapeUtils.unescapeXml(ref);
            ref = ref.replaceAll("(&apos;|_)", "");
            ref = ref.replaceAll("`", "");
            Element hyper = DocumentHelper.createElement(REFERENCE);
            hyper.addAttribute(NAME, ref);
            boolean trouve = false;
            for (int i = 0; i < eTarget.size() && !trouve; i++) {
                Element el = eTarget.get(i);
                String refTmp = URLEncoder.encode(ref.replaceAll("\\s", "-").toLowerCase(), "UTF-8");
                if (el.attributeValue(ID).equalsIgnoreCase((refTmp))) {
                    hyper.addAttribute(REFURI, el.attributeValue(REFURI));
                    trouve = true;
                }
            }
            if (!trouve) {
                hyper.addAttribute(ATTR_REFID, ref);
            }
            hyper.setText(ref);
            text = txtDebut + hyper.asXML() + " " + txtFin;
            matcher = REGEX_HYPERLINK_REFERENCE.matcher(text);

        }

        // substitution reference
        matcher = REGEX_SUBSTITUTION_REFERENCE.matcher(text);
        int begin = 0;
        while (matcher.find(begin)) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());
            String ref = matcher.group(1);

            Node subst = e.selectSingleNode("//" + SUBSTITUTION_DEFINITION
                    + "[@name='" + ref + "']/child::node()");

            if (subst == null) {
                text = start + "|" + ref + "|";
            } else {
                text = start + subst.asXML();
            }

            begin = text.length();
            text += end;
            matcher = REGEX_SUBSTITUTION_REFERENCE.matcher(text);

        }
        // undo substitution in LITERAL
        Pattern p = Pattern.compile("<tmp>([^<>]+)</tmp>");

        matcher = p.matcher(text);
        while (matcher.find()) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());

            String tempKey = matcher.group(1);
            text = start + temporaries.get(tempKey) + end;
            matcher = p.matcher(text);
        }
        
        String resultElementText = text.trim();
        Element result = DocumentHelper.parseText(
                "<TMP>" + resultElementText + "</TMP>").getRootElement();

        e.setText("");
        e.appendContent(result);
    }
}
