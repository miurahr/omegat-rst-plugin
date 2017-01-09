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
import org.dom4j.Text;

import tokyo.northside.jrst.JRSTDirective;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * .. date:: .. time::
 * 
 * Created: 4 nov. 06 13:29:25
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class DateDirective implements JRSTDirective {

    /*
     * @see org.nuiton.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    @Override
    public Node parse(Element e) {

        // String format = e.attributeValue(JRSTLexer.DIRECTIVE_VALUE);

        // TODO used format to format date, this format is not Java standard
        // is python standard "%Y-%m-%d"

        Text result = DocumentHelper.createText(SimpleDateFormat.getInstance()
                .format(new Date()));
        return result;
    }

}
