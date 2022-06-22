package com.vaxtomis.yaml4test.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * Static condition.<br>
 * '\u0085' Next Line<br>
 * '\u2028' Line Separator<br>
 * '\u2029' Paragraph Separator
 * </p>
 **/
public class Define {
    public final static String CLASS = "class";
    public final static String VALUE = "value";
    public final static String STRING = "java.lang.String";
    public final static String EMPTY = "";

    public final static String MATCH_LONG = "^(-|\\+)?\\d+$";
    public final static String MATCH_FLOAT = "^(-?\\d+)(\\.\\d+)?$";
    public final static String MATCH_BYTE = "^[0-9a-fA-F]+$";
    public final static String MATCH_BIG_DECIMAL = "^(-?\\d+)(\\.\\d+)?$";
    public final static String MATCH_BIG_INTEGER = "^(-|\\+)?\\d+$";

    public final static String MAPPING = "MAPPING";
    public final static String SEQUENCE = "SEQUENCE";
    public final static String PRIMITIVE = "PRIMITIVE";
    public final static String COPY_INSTANCE = "CopyInstance";
    public final static String COPY_INSTANCE_DOT = "CopyInstance.";

    public final static int DOUBLE_PREVIOUS = -2;
    public final static int PREVIOUS = -1;
    public final static int CURRENT = 0;


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

    public final static String NULL_OR_OTHER = "\0 \t\r\n\u0085";
    public final static String NON_ALPHA_OR_NUM = "\0 \t\r\n\u0085?:,]}%@`";
    public final static String S4 = "\0 \t\r\n\u0028[]{}";

    public final static String SPACES_AND_STUFF = "'\"\\\0 \t\r\n\u0085";
    public final static String DOUBLE_ESC = "\"\\";

    /**
     * ^[$_A-Za-z](\[)(0|[1-9][0-9]*)(\])$|[$_A-Za-z][A-Za-z0-9_-]+(\[)(0|[1-9][0-9]*)(\])
     */
    public final static Pattern PROPERTY_NAME_VARIABLE = Pattern.compile(
            "^[$_A-Za-z]$|[$_A-Za-z][A-Za-z0-9_-]+$" +
            "|^[$_A-Za-z](\\[)(0|[1-9][0-9]*)(\\])$" +
            "|[$_A-Za-z][A-Za-z0-9_-]+(\\[)(0|[1-9][0-9]*)(\\])");
    public final static Pattern BRACKETS_NUMBER = Pattern.compile("^(\\[)(0|[1-9][0-9]*)(\\])$");

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
