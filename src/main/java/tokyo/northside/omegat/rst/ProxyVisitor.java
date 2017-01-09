/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
 with fuzzy matching, translation memory, keyword search,
 glossaries, and translation leveraging into updated projects.

 Copyright (C) 2016 Hiroshi Miura
 Home page: http://www.omegat.org/
 Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

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

package tokyo.northside.omegat.rst;

import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import tokyo.northside.jrst.ReStructuredText;


/**
 * Proxy Visitor for DOM4j.
 * @author Hiroshi Miura
 */
public class ProxyVisitor extends VisitorSupport {
    private RstVisitor v;

    /**
     * Constructor get concrete visitor.
     * @param visitor concrete visitor.
     */
    public ProxyVisitor(final RstVisitor visitor) {
        this.v = visitor;
    }

    @Override
    public void visit(Element e) {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Root Element
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (elementEquals(ReStructuredText.DOCUMENT, e)) {
            v.visitDocument(e);
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Title Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.TITLE, e)) {
            v.visitTitle(e);
        } else if (elementEquals(ReStructuredText.SUBTITLE, e)) {
            v.visitSubTitle(e);
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Bibliographic Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.DOCINFO, e)) {
            //v.visitDocInfo(e);
         } else if (elementEquals(ReStructuredText.AUTHOR, e)) {
            //v.visitAuthor(e);
        } else if (elementEquals(ReStructuredText.AUTHORS, e)) {
            //v.visitAuthors(e);
        } else if (elementEquals(ReStructuredText.ORGANIZATION, e)) {
            //v.visitOrganization(e);
        } else if (elementEquals(ReStructuredText.ADDRESS, e)) {
            //v.visitAddress(e);
        } else if (elementEquals(ReStructuredText.CONTACT, e)) {
            //v.visitContact(e);
        } else if (elementEquals(ReStructuredText.VERSION, e)) {
            //v.visitVersion(e);
        } else if (elementEquals(ReStructuredText.REVISION, e)) {
            //v.visitRevision(e);
        } else if (elementEquals(ReStructuredText.STATUS, e)) {
            //v.visitStatus(e);
        } else if (elementEquals(ReStructuredText.DATE, e)) {
            //v.visitDate(e);
        } else if (elementEquals(ReStructuredText.COPYRIGHT, e)) {
            //v.visitCopyright(e);
        } else if (elementEquals(ReStructuredText.TOPIC, e)) {
            v.visitTopic(e);
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Decoration Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.DECORATION, e)) {
            //v.visitDecoration(e);
        } else if (elementEquals(ReStructuredText.HEADER, e)) {
            //v.visitHeader(e);
        } else if (elementEquals(ReStructuredText.FOOTER, e)) {
            //v.visitFooter(e);
        } else if (elementEquals(ReStructuredText.FIELD_NAME, e)) {
            //v.visitFieldName(e);
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Structural Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.SECTION, e)) {
            v.visitSection(e);
        } else if (elementEquals(ReStructuredText.SIDEBAR, e)) {
            //v.visitSideBar(e);
        } else if (elementEquals(ReStructuredText.TRANSITION, e)) {
            v.visitTransition(e);
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Body Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.PARAGRAPH, e)) {
            v.visitParagraph(e);
        } else if (elementEquals(ReStructuredText.COMPOUND, e)) {
        } else if (elementEquals(ReStructuredText.CONTAINER, e)) {
        } else if (elementEquals(ReStructuredText.BULLET_LIST, e)) {
        } else if (elementEquals(ReStructuredText.ENUMERATED_LIST, e)) {
        } else if (elementEquals(ReStructuredText.LIST_ITEM, e)) {
        } else if (elementEquals(ReStructuredText.DEFINITION_LIST, e)) {
        } else if (elementEquals(ReStructuredText.DEFINITION_LIST_ITEM, e)) {
        } else if (elementEquals(ReStructuredText.TERM, e)) {
        } else if (elementEquals(ReStructuredText.CLASSIFIER, e)) {
        } else if (elementEquals(ReStructuredText.DEFINITION, e)) {
        } else if (elementEquals(ReStructuredText.FIELD_LIST, e)) {
        } else if (elementEquals(ReStructuredText.FIELD, e)) {
            //v.visitField(e);
        } else if (elementEquals(ReStructuredText.FIELD_NAME, e)) {
        } else if (elementEquals(ReStructuredText.FIELD_BODY, e)) {
        } else if (elementEquals(ReStructuredText.OPTION_LIST, e)) {
        } else if (elementEquals(ReStructuredText.OPTION_LIST_ITEM, e)) {
        } else if (elementEquals(ReStructuredText.OPTION_GROUP, e)) {
        } else if (elementEquals(ReStructuredText.OPTION, e)) {
        } else if (elementEquals(ReStructuredText.OPTION_STRING, e)) {
        } else if (elementEquals(ReStructuredText.OPTION_ARGUMENT, e)) {
        } else if (elementEquals(ReStructuredText.DESCRIPTION, e)) {
        } else if (elementEquals(ReStructuredText.LITERAL_BLOCK, e)) {
            v.visitLiteralBlock(e);
        } else if (elementEquals(ReStructuredText.LINE_BLOCK, e)) {
        } else if (elementEquals(ReStructuredText.LINE, e)) {
        } else if (elementEquals(ReStructuredText.BLOCK_QUOTE, e)) {
            v.visitBlockQuote(e);
        } else if (elementEquals(ReStructuredText.ATTRIBUTION, e)) {
            v.visitAttribution(e);
        } else if (elementEquals(ReStructuredText.DOCTEST_BLOCK, e)) {
        } else if (elementEquals(ReStructuredText.ATTENTION, e)) {
        } else if (elementEquals(ReStructuredText.CAUTION, e)) {
        } else if (elementEquals(ReStructuredText.DANGER, e)) {
        } else if (elementEquals(ReStructuredText.ERROR, e)) {
        } else if (elementEquals(ReStructuredText.HINT, e)) {
        } else if (elementEquals(ReStructuredText.IMPORTANT, e)) {
        } else if (elementEquals(ReStructuredText.NOTE, e)) {
        } else if (elementEquals(ReStructuredText.TIP, e)) {
        } else if (elementEquals(ReStructuredText.WARNING, e)) {
        } else if (elementEquals(ReStructuredText.ADMONITION, e)) {
        } else if (elementEquals(ReStructuredText.FOOTNOTE, e)) {
        } else if (elementEquals(ReStructuredText.CITATION, e)) {
        } else if (elementEquals(ReStructuredText.LABEL, e)) {
        } else if (elementEquals(ReStructuredText.RUBRIC, e)) {
        } else if (elementEquals(ReStructuredText.TARGET, e)) {
        } else if (elementEquals(ReStructuredText.SUBSTITUTION_DEFINITION, e)) {
        } else if (elementEquals(ReStructuredText.COMMENT, e)) {
        } else if (elementEquals(ReStructuredText.PENDING, e)) {
        } else if (elementEquals(ReStructuredText.FIGURE, e)) {
        } else if (elementEquals(ReStructuredText.IMAGE, e)) {
        } else if (elementEquals(ReStructuredText.CAPTION, e)) {
        } else if (elementEquals(ReStructuredText.LEGEND, e)) {
        } else if (elementEquals(ReStructuredText.SYSTEM_MESSAGE, e)) {
        } else if (elementEquals(ReStructuredText.RAW, e)) {
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Table Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.TABLE, e)) {
        } else if (elementEquals(ReStructuredText.TGROUP, e)) {
        } else if (elementEquals(ReStructuredText.COLSPEC, e)) {
        } else if (elementEquals(ReStructuredText.THEAD, e)) {
        } else if (elementEquals(ReStructuredText.TBODY, e)) {
        } else if (elementEquals(ReStructuredText.ROW, e)) {
        } else if (elementEquals(ReStructuredText.ENTRY, e)) {
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Inline Elements
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        else if (elementEquals(ReStructuredText.EMPHASIS, e)) {
            v.visitEmphasis(e);
        } else if (elementEquals(ReStructuredText.STRONG, e)) {
            v.visitStrong(e);
        } else if (elementEquals(ReStructuredText.LITERAL, e)) {
        } else if (elementEquals(ReStructuredText.REFERENCE, e)) {
        } else if (elementEquals(ReStructuredText.FOOTNOTE_REFERENCE, e)) {
        } else if (elementEquals(ReStructuredText.CITATION_REFERENCE, e)) {
        } else if (elementEquals(ReStructuredText.SUBSTITUTION_REFERENCE, e)) {
        } else if (elementEquals(ReStructuredText.TITLE_REFERENCE, e)) {
        } else if (elementEquals(ReStructuredText.ABBREVIATION, e)) {
        } else if (elementEquals(ReStructuredText.ACRONYM, e)) {
        } else if (elementEquals(ReStructuredText.SUPERSCRIPT, e)) {
        } else if (elementEquals(ReStructuredText.SUBSCRIPT, e)) {
        } else if (elementEquals(ReStructuredText.INLINE, e)) {
        } else if (elementEquals(ReStructuredText.PROBLEMATIC, e)) {
        } else if (elementEquals(ReStructuredText.GENERATED, e)) {
       }
    }

    protected boolean elementEquals(String name, Element e) {
        return e.getName().equals(name);
    }

}
