package tokyo.northside.omegat.rst

import static org.testng.Assert.*;

import org.omegat.filters2.FilterContext;
import org.omegat.util.Language;

import org.testng.annotations.*;

/**
 * @author Hiroshi Miura
 */
class RstFilterTest extends RstTestBase {

    @Test
    void testGetFileFormatName() throws Exception {
        assertEquals new RstFilter().getFileFormatName(), "reStructured Text Filter"
    }

    @Test
    void testGetHint() throws Exception {
        assertEquals new RstFilter().getHint(), "Note: Filter to translate reStructured Text files."
    }

    @Test
    void testIsSourceEncodingVariable() throws Exception {
        assertFalse new RstFilter().isSourceEncodingVariable()
    }

    @Test
    void testIsTargetEncodingVariable() throws Exception {
        assertFalse new RstFilter().isTargetEncodingVariable()
    }

    @Test
    void testIsFileSupported_true() throws Exception {
        File target = new File(this.getClass().getResource("/filters/rst/text1.rst").getFile())
        FilterContext fc = new FilterContext(new Language("en"), new Language("be"), true)
        assertTrue new RstFilter().isFileSupported(target, null, fc)
    }

    @Test
    void testIsFileSupported_false() throws Exception {
        File target = new File(this.getClass().getResource("/filters/text/text1.txt").getFile())
        FilterContext fc = new FilterContext(new Language("en"), new Language("be"), true)
        assertFalse new RstFilter().isFileSupported(target, null, fc)
    }

    /**
     * Test tokenize result.
     * @throws Exception when I/O or parser error happened.
     */
    @Test
    void testTokenize() throws Exception {
        testTokenize "/filters/rst/text1"
        testTokenize "/filters/rst/text2"
        testTokenize "/filters/rst/topic"
    }

    /**
     * Test result of output.
     * <p>
     *     parse -> put original text with translated token.
     *     this test use token as same as original.
     *     It should be exactly same as original text.
     * @throws Exception when I/O or parser error happened.
     */
    @Test
    void testOutput() throws Exception {
        testOutput "/filters/rst/text1"
        testOutput "/filters/rst/text2"
        testOutput "/filters/rst/text3"
    }
}
