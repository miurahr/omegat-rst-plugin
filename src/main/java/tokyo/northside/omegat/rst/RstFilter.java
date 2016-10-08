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

import org.nuiton.jrst.convertisor.DocUtilsVisitor;
import org.omegat.core.Core;
import org.omegat.core.data.ProtectedPart;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.IAlignCallback;
import org.omegat.filters2.IFilter;
import org.omegat.filters2.IParseCallback;
import org.omegat.filters2.ITranslateCallback;
import org.omegat.filters2.Instance;
import org.omegat.filters2.TranslationException;
import org.omegat.util.NullBufferedWriter;

import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Visitor;
import org.nuiton.jrst.JRST;
import org.nuiton.jrst.convertisor.DocUtils2RST;


/**
 * Created by miurahr on 16/10/07.
 */
public class RstFilter implements IFilter {
    /**
     * Callback for parse.
     */
    private IParseCallback entryParseCallback;

    /**
     * Callback for translate.
     */
    private ITranslateCallback entryTranslateCallback;

    /**
     * Callback for align.
     */
    private IAlignCallback entryAlignCallback;

    /**
     * Options for processing time.
     */
    private Map<String, String> processOptions;

    private String inEncodingLastParsedFile;

    private List<ProtectedPart> protectedParts = new ArrayList<>();

    /**
     * Plugin loader.
     */
    public static void loadPlugins() {
        Core.registerFilterClass(RstFilter.class);
    }

    /**
     * Plugin unloader.
     */
    public static void unloadPlugins() {
    }


    /**
     * Human-readable name of the File Format this filter supports.
     *
     * @return File format name
     */
    public String getFileFormatName(){
        return "reStructured Text Filter";
    }

    /**
     * Returns the hint displayed while the user edits the filter, and when she
     * adds/edits the instance of this filter. The hint may be any string,
     * preferably in a non-geek language.
     *
     * @return The hint for editing the filter in a non-geek language.
     */
    public String getHint() {
        return "Note: Filter to translate reStructured Text files.";
    }

    /**
     * Define fuzzy mark prefix for source which will be stored in TM.
     * It's 'fuzzy' by default, but each
     * filter can redefine it.
     *
     * @return fuzzy mark prefix
     */
    public String getFuzzyMark() {
        return "fuzzy";
    }

    /**
     * OmegaT calls this to see whether the filter has any options.
     * By default returns false, so filter
     * authors should override this to tell OmegaT core that this filter has options.
     *
     * @return True if the filter has any options, and false otherwise.
     */
    public boolean hasOptions() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, String> changeOptions(final Dialog parent,
                                             final Map<String, String> config) {
        return null;
    }

    /**
     * The default list of filter instances that this filter class has. One
     * filter class may have different filter instances, different by source
     * file mask, encoding of the source file etc.
     * <p>
     * Note that the user may change the instances freely.
     *
     * @return Default filter instances
     */
    public Instance[] getDefaultInstances() {
        return new Instance[]{new Instance("*.md")};
    }

    /**
     * Either the encoding can be read, or it is UTF-8..
     *
     * @return <code>false</code>
     */
    public boolean isSourceEncodingVariable() {
        return false;
    }

    /**
     * @return <code>false</code>
     */
    public boolean isTargetEncodingVariable() {
        return false;
    }


        /**
     * Returns whether the file is supported by the filter,
     * given the file and possible file's encoding (
     * <code>null</code> encoding means autodetect).
     *
     * @param inFile Source file.
     * @param config optional configuration.
     * @param fc     Filter context.
     * @return Does the filter support the file.
     */
    public boolean isFileSupported(final File inFile, final Map<String, String> config,
                                   final FilterContext fc) {
        String inEncoding = fc.getInEncoding();
        try (BufferedReader reader = getBufferedReader(inFile, inEncoding)) {
            String line;
            int len = 0;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (len > 0) {
                    if (getHeadingLevel(len, trimmed) > 0) {
                        reader.close();
                        return true;
                    }
                }
                len = line.length();
            }
            reader.close();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Simple heading detector without parser.
     *
     * @param trimmed trimmed string.
     * @return 1 if it has level 1 section header, otherwise false.
     */
    private int getHeadingLevel(final int len, final String trimmed) {
        if (trimmed.matches("^=+$")) {
            if (trimmed.length() >= len) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Parse Rst file and process it.
     *
     * @param inFile   file to parse
     * @param config   filter's configuration options
     * @param fc       filters context.
     * @param callback callback for parsed data
     * @throws IOException when I/O error happened.
     */
    public final void parseFile(final File inFile, final Map<String, String> config,
                                final FilterContext fc, final IParseCallback callback)
            throws IOException, TranslationException {
        entryParseCallback = callback;
        entryTranslateCallback = null;
        entryAlignCallback = null;
        processOptions = config;
        try {
            processFile(inFile, null, fc);
        } finally {
            entryParseCallback = null;
            processOptions = null;
        }
    }

    /**
     * Align Rst files.
     * <p>
     * Currently not supported.
     * </p>
     *
     * @param inFile   source file
     * @param outFile  translated file
     * @param config   filter's configuration options
     * @param fc filter context.
     * @param callback callback for store aligned data
     * @throws IOException when I/O error happened.
     */
    public final void alignFile(final File inFile, final File outFile,
                                final Map<String, String> config, final FilterContext fc,
                                final IAlignCallback callback) throws IOException {
        entryParseCallback = null;
        entryTranslateCallback = null;
        entryAlignCallback = callback;
        processOptions = config;
        try {
            // TODO: Implement me.
            System.err.println("Not implemented yet.");
        } finally {
            entryAlignCallback = null;
            processOptions = null;
        }
    }

    /**
     * Generate translated files.
     *
     * @param inFile   source file
     * @param outFile  output file
     * @param config   filter's configuration options
     * @param fc       filters context.
     * @param callback callback for get translation
     * @throws IOException when I/O error happened.
     */
    public final void translateFile(final File inFile, final File outFile,
                                    final Map<String, String> config, final FilterContext fc,
                                    final ITranslateCallback callback) throws IOException, TranslationException {
        entryParseCallback = null;
        entryTranslateCallback = callback;
        entryAlignCallback = null;
        processOptions = config;
        try {
            entryTranslateCallback.setPass(1);
            processFile(inFile, outFile, fc);
        } finally {
            entryTranslateCallback = null;
            processOptions = null;
        }
    }

    /**
     * Core of Rst file processing.
     *
     * @param inFile  input file.
     * @param outFile output file.
     * @param fc      filter context.
     * @throws IOException throes when I/O error happened.
     */
    protected void processFile(final File inFile, final File outFile, final FilterContext fc)
            throws IOException, TranslationException {
        String inEncoding = fc.getInEncoding();
        Document doc;
        try {
            doc = JRST.generateDocutils(inFile, inEncoding);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return;
        }
        if (outFile != null) {
            String outEncoding = getOutputEncoding(fc);
            try (BufferedWriter outfile = getBufferedWriter(outFile, outEncoding)) {
                doc = process(doc);
                DocUtilsVisitor visitor = new DocUtils2RST();
                doc.accept(visitor);
                String result = visitor.getResult();
                outfile.write(result);
                outfile.flush();
                outfile.close();
            }
        } else {
            process(doc);
        }
    }

    private Document process(final Document doc) {
        // fixme.
        return doc;
    }

    /**
     * Return input file's encoding.
     * @return always UTF-8 with this plugin.
     */
    public String getInEncodingLastParsedFile() {
        return inEncodingLastParsedFile;
    }

    /**
     * Create BufferedReader from specified file and encoding.
     *
     * @param inFile file to read.
     * @param inEncoding file encoding.
     * @return BufferReader object.
     * @throws IOException when file I/O error happened.
     */
    public static BufferedReader getBufferedReader(final File inFile, final String inEncoding)
            throws IOException {
        InputStreamReader isr;
        if (inEncoding == null) {
            isr = new InputStreamReader(new FileInputStream(inFile), Charset.defaultCharset());
        } else {
            isr = new InputStreamReader(new FileInputStream(inFile), inEncoding);
        }
        return new BufferedReader(isr);
    }

    /**
     * Get the output encoding. If it's not set in the FilterContext (setting is "&lt;auto&gt;")
     * and the filter allows ({@link #isTargetEncodingVariable()}):
     * <ul><li>Reuse the input encoding if it's Unicode
     * <li>If the input was not Unicode, fall back to UTF-8.
     * </ul>
     * The result may be null.
     *
     * @param fc filter context.
     * @return Encoding for output file.
     */
    private String getOutputEncoding(final FilterContext fc) {
        String encoding = fc.getOutEncoding();
        if (encoding == null && isTargetEncodingVariable()) {
            // Use input encoding if it's Unicode; otherwise default to UTF-8
            if (inEncodingLastParsedFile != null && inEncodingLastParsedFile
                    .toLowerCase().startsWith("utf-")) {
                encoding = inEncodingLastParsedFile;
            } else {
                encoding = "UTF-8";
            }
        }
        return encoding;
    }

    /**
     * Create BufferedWriter object from specified file and encoding.
     *
     * @param outFile file to output.
     * @param outEncoding file encoding.
     * @return BufferedWiter object.
     * @throws IOException when file I/O error happened.
     */
    public static BufferedWriter getBufferedWriter(final File outFile, final String outEncoding)
            throws IOException {
        OutputStreamWriter osw;
        if (outEncoding == null) {
           osw = new OutputStreamWriter(new FileOutputStream(outFile), Charset.defaultCharset());
        } else {
            osw = new OutputStreamWriter(new FileOutputStream(outFile), outEncoding);
        }
        return new BufferedWriter(osw);
    }

}
