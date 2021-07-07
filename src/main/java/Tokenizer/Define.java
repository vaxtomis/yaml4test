package Tokenizer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @description: Static condition.
 * '\u0085' Next Line
 * '\u2028' Line Separator
 * '\u2029' Paragraph Separator
 **/
public class Define {
    public final static String LINEBREAK = "\n\u0085\u2028\u2029";
    public final static String RN = "\r\n";
    public final static String BLANK_T = " \t";
    public final static String NULL_BLANK_LINEBREAK = "\0 \r\n\u0085";
    public final static String NULL_BLANK_T_LINEBREAK = "\0 \t\r\n\u0085";
    public final static String NULL_OR_LINEBREAK = "\0\r\n\u0085";
    public final static String FULL_LINEBREAK = "\r\n\u0085";
    public final static String BLANK_OR_LINEBREAK = " \r\n\u0085";
    public final static String ALPHA = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    public final static String NOT_ALPHA_OR_NUM = "\0 \t\r\n\u0085?:,]}%@`";

    public final static String S4 = "\0 \t\r\n\u0028[]{}";

    public final static Pattern R_FLOW_ZERO = Pattern.compile("[\0 \t\r\n\u0085]|(:[\0 \t\r\n\u0085])");
    public final static Pattern R_FLOW_NOT_ZERO = Pattern.compile("[\0 \t\r\n\u0085\\[\\]{},:?]");
    public final static Pattern END_OR_START = Pattern.compile("^(---|\\.\\.\\.)[\0 \t\r\n\u0085]$");
    public final static Pattern NOT_HEX = Pattern.compile("[^0-9A-Fa-f]");
    public final static Pattern NON_ALPHA = Pattern.compile("[^-0-9A-Za-z_]");
    public final static Pattern START = Pattern.compile("^\\.\\.\\.[\0 \t\r\n\u0085]$");
    public final static Pattern ENDING = Pattern.compile("^---[\0 \t\r\n\u0085]$");
    public final static Pattern BEG = Pattern
            .compile("[^\0 \t\r\n\u0085\\-?:,\\[\\]{}#&*!|>'\"%@]|([\\-?:][^\0 \t\r\n\u0085])");

    public final static Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap();
    public final static Map<Character, Integer> ESCAPE_CODES = new HashMap();

    static {
        ESCAPE_REPLACEMENTS.put('0', "\0");
        ESCAPE_REPLACEMENTS.put('n', "\n");
        ESCAPE_REPLACEMENTS.put('r', "\r");
        ESCAPE_REPLACEMENTS.put('"', "\"");
        ESCAPE_REPLACEMENTS.put('\\', "\\");

        ESCAPE_REPLACEMENTS.put('a', "\u0007");
        ESCAPE_REPLACEMENTS.put('b', "\u0008");
        ESCAPE_REPLACEMENTS.put('t', "\u0009");
        ESCAPE_REPLACEMENTS.put('\t', "\u0009");
        ESCAPE_REPLACEMENTS.put('v', "\u000B");
        ESCAPE_REPLACEMENTS.put('f', "\u000C");
        ESCAPE_REPLACEMENTS.put('e', "\u001B");
        ESCAPE_REPLACEMENTS.put(' ', "\u0020");
        ESCAPE_REPLACEMENTS.put('N', "\u0085");
        ESCAPE_REPLACEMENTS.put('_', "\u00A0");
        ESCAPE_REPLACEMENTS.put('L', "\u2028");
        ESCAPE_REPLACEMENTS.put('P', "\u2029");

        ESCAPE_CODES.put('x', 2);
        ESCAPE_CODES.put('u', 4);
        ESCAPE_CODES.put('U', 8);
    }

}
