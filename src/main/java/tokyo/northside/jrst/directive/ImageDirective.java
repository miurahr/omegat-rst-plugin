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

package tokyo.northside.jrst.directive;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import tokyo.northside.jrst.JRSTDirective;
import tokyo.northside.jrst.JRSTLexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tokyo.northside.jrst.ReStructuredText.IMAGE;
import static tokyo.northside.jrst.ReStructuredText.SUBSTITUTION_DEFINITION;

/**
 * .. image:: picture.jpeg :height: 100 :width: 200 :scale: 50 :alt: alternate
 * text :align: right
 * 
 * Created: 4 nov. 06 12:52:02
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class ImageDirective implements JRSTDirective {

    protected static final String SCALE = "scale";
    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";

    /*
     * (non-Javadoc)
     * 
     * @see org.nuiton.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    @Override
    public Node parse(Element e) {
        Element result = DocumentHelper.createElement(IMAGE);

        if (e.getParent() != null
                && SUBSTITUTION_DEFINITION.equals(e.getParent().getName())) {
            String ref = e.getParent().attributeValue("name");
            result.addAttribute("alt", ref);
        }
        result.addAttribute("uri", e.attributeValue(JRSTLexer.DIRECTIVE_VALUE));

        Pattern arg = Pattern.compile(":([^:]+):\\s*(.*)");
        String[] lines = e.getText().split("\n");
        for (String l : lines) {
            Matcher matcher = arg.matcher(l.trim());
            if (matcher.matches()) {
                String name = matcher.group(1);
                String value = matcher.group(2);
                if (SCALE.equalsIgnoreCase(name)) {
                    if (!result.asXML().matches(".*" + WIDTH + "=\".*\".*")) {
                        result.addAttribute(WIDTH, value + (value.matches(".*%") ? "" : "%"));
                    }
                    if (!result.asXML().matches(".*" + HEIGHT + "=\".*\".*")) {
                        result.addAttribute(HEIGHT, value + (value.matches(".*%") ? "" : "%"));
                    }
                }
                result.addAttribute(name, value);
            }
        }
        return result;
    }

}
