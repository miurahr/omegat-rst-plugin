package tokyo.northside.omegat.rst

import com.alibaba.fastjson.JSON
import org.apache.commons.io.IOUtils
import org.omegat.filters.TestFilterBaseNg

import static org.testng.Assert.*;

class RstTestBase extends TestFilterBaseNg {

    def test(final String testcase) throws Exception {
        translateText(new RstFilter(), testcase + ".rst")
        List<String> entries = parse(new RstFilter(), testcase + ".rst")
        URL url = this.getClass().getResource(testcase + ".json");
        if (url == null) {
            throw new IOException("Cannot find test expectation.");
        }
        BufferedReader reader = getBufferedReader(url.getFile())
        String jsonString = IOUtils.toString(reader);
        ArrayList expected = JSON.parseObject(jsonString, ArrayList.class)
        assertEquals entries, expected, testcase
    }

    def getBufferedReader(final String inFile)
            throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(new File(inFile)), "UTF-8"))
    }
}
