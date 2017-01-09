package tokyo.northside.jrst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miurahr on 17/01/09.
 */
public class StringUtils {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Character[] openingChars = {'(', '{', '['};

    private static final Character[] closingChars = {')', '}', ']'};

    /**
     * Split string use 'separator' as separator. If String contains "'()[]{}
     * this method count the number of open char end close char to split
     * correctly argument
     *
     * WARNING: cette method ne fonctionne pas si le contenu contient
     * des carateres utilisé pour le parsing et présent une seule fois.
     * Par exemple: "l'idenfiant" contient ' qui empeche totalement le
     * parsing de fonctionner.
     *
     * @param args      string to split
     * @param separator separator use to split string
     * @return array of string
     */
    public static String[] split(String args, String separator) {
        return split(openingChars, closingChars, args, separator);
    }


    /**
     * Use to split string array representation in array according with ',' as
     * default separator.
     *
     * WARNING: cette method ne fonctionne pas si le contenu contient
     * des carateres utilisé pour le parsing et présent une seule fois.
     * Par exemple: "l'idenfiant" contient ' qui empeche totalement le
     * parsing de fonctionner.
     *
     * @param stringList string that represent array
     * @return array with length &gt; 0 if listAsString ≠ null or null
     */
    public static String[] split(String stringList) {
        String[] result;
        result = split(stringList, ",");
        return result;
    }

    /**
     * Split string use 'separator' as separator. If String contains "'
     * and {@code openingChar} {@code closingChars}
     *
     * this method count the number of open char end close char to split
     * correctly argument
     *
     * WARNING: cette method ne fonctionne pas si le contenu contient
     * des carateres utilisé pour le parsing et présent une seule fois.
     * Par exemple: "l'idenfiant" contient ' qui empeche totalement le
     * parsing de fonctionner.
     *
     * @param openingChars list of opening caracteres
     * @param closingChars list of closing caracteres
     * @param args         string to split
     * @param separator    separator use to split string
     * @return array of string
     */
    public static String[] split(Character[] openingChars,
                                 Character[] closingChars,
                                 String args, String separator) {
        if (args == null) {
            return EMPTY_STRING_ARRAY;
        }

        List<String> result = new ArrayList<String>();

        int start = 0;
        int end;
        StringBuilder op = new StringBuilder(); // stack of {([< currently open
        char last = '\0'; // contains " or ' if string is openned

        List<Character> opening = Arrays.asList(openingChars);

        List<Character> closing = Arrays.asList(closingChars);

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            if (c == '\\') {
                // pass next char
                i++;
            } else if (last != '"' && last != '\'') {
                if (opening.contains(c)) {
                    op.append(c);
                } else if (closing.contains(c)) {
                    op.deleteCharAt(op.length() - 1);
                } else if (c == '"' || c == '\'') {
                    // open string " or '
                    last = c;
                } else if (op.length() == 0 &&
                           args.regionMatches(i, separator, 0,
                                              separator.length())) {
                    // end of one arguement
                    end = i;
                    // pass separator
                    i += separator.length() - 1;

                    String a = args.substring(start, end);
                    result.add(a);
                    // start of next argument
                    start = end + separator.length();
                }
            } else if (c == last) {
                // close string " or '
                last = '\0';
            }
        }

        if (start < args.length()) {
            String a = args.substring(start, args.length());
            result.add(a);
        }

        return result.toArray(new String[result.size()]);
    }
}
