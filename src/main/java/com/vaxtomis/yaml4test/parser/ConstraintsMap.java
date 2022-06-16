package com.vaxtomis.yaml4test.parser;

import com.vaxtomis.yaml4test.tokenizer.TokenType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.vaxtomis.yaml4test.common.Define.*;

/**
 * <p>
 * Define the constraints for tokens parser.<br>
 * 定义了分词语法限制的映射关系，路径不通即为语法错误。
 * </p>
 * <pre>
 *
 * Key                to  Scalar             :k-v get key's scalar
 *
 * Key-Scalar         to  Value              :k-v need value
 *
 * Value-Scalar       to  BlockEnd           :block end
 *                    to  Key                :next key
 *
 * BlockEntry-Scalar  to  BlockEntry         :next entry
 *                    to  BlockEnd           :block sequence close
 *
 * BlockEnd           to  Key                :new key
 *                    to  BlockEntry         :block entry
 *                    to  BlockEnd           :block end include
 *                                            sequence end and mapping end
 *                    to  StreamEnd          :stream end
 *
 * Value              to  Scalar             :k-v complete
 *                    to  ClassName          :get sub class name
 *                    to  BlockMappingStart  :sub mapping start
 *                    to  BlockSequenceStart :sub sequencing start
 *
 * ClassName          to  BlockMappingStart  :sub class start
 *
 * BlockMappingStart  to  Key                :first k-v
 *
 * BlockSequenceStart to  BlockEntry         :first entry
 *
 * BlockEntry         to  Scalar             :value of entry
 *                    to  ClassName          :get entry class name
 *                    to  BlockMappingStart  :start sub block mapping
 *
 * </pre>
 * @author vaxtomis
 */
public class ConstraintsMap {
    private final static HashSet<TokenType> TOKEN_SET = new HashSet<>();
    private final static HashMap<String, HashSet<String>> CONSTRAINTS = new HashMap<>();

    private final static String STREAM_START = "STREAM_START";
    private final static String STREAM_END = "STREAM_END";
    private final static String KEY = "KEY";
    private final static String VALUE = "VALUE";
    private final static String SCALAR = "SCALAR";
    private final static String CLASSNAME = "CLASSNAME";
    private final static String BLOCK_MAPPING_START = "BLOCK_MAPPING_START";
    private final static String BLOCK_SEQUENCE_START = "BLOCK_SEQUENCE_START";
    private final static String BLOCK_ENTRY = "BLOCK_ENTRY";
    private final static String BLOCK_END = "BLOCK_END";

    static {
        TOKEN_SET.addAll(
                Arrays.asList(TokenType.values())
        );


        CONSTRAINTS.put(STREAM_START, new HashSet<String>() {
            {
                add(BLOCK_MAPPING_START);
            }
        });

        CONSTRAINTS.put(KEY, new HashSet<String>() {
            {
                add(SCALAR);
            }
        });

        CONSTRAINTS.put(merge(KEY, SCALAR), new HashSet<String>() {
            {
                add(VALUE);
            }
        });

        CONSTRAINTS.put(merge(VALUE, SCALAR), new HashSet<String>() {
            {
                add(BLOCK_END);
                add(KEY);
            }
        });

        CONSTRAINTS.put(merge(BLOCK_ENTRY, SCALAR), new HashSet<String>() {
            {
                add(BLOCK_ENTRY);
                add(BLOCK_END);
            }
        });

        CONSTRAINTS.put(BLOCK_END, new HashSet<String>() {
            {
                add(KEY);
                add(BLOCK_ENTRY);
                add(BLOCK_END);
                add(STREAM_END);
            }
        });

        CONSTRAINTS.put(VALUE, new HashSet<String>() {
            {
                add(SCALAR);
                add(CLASSNAME);
                add(BLOCK_MAPPING_START);
                add(BLOCK_SEQUENCE_START);
            }
        });

        CONSTRAINTS.put(CLASSNAME, new HashSet<String>() {
            {
                add(BLOCK_MAPPING_START);
            }
        });

        CONSTRAINTS.put(BLOCK_MAPPING_START, new HashSet<String>() {
            {
                add(KEY);
            }
        });

        CONSTRAINTS.put(BLOCK_SEQUENCE_START, new HashSet<String>() {
            {
                add(BLOCK_ENTRY);
            }
        });

        CONSTRAINTS.put(BLOCK_ENTRY, new HashSet<String>() {
            {
                add(SCALAR);
                add(CLASSNAME);
                add(BLOCK_MAPPING_START);
            }
        });
    }
    public static boolean isAllowed(Parser.SlidingWindow window) {
        TokenType doublePre = window.getTokenType(DOUBLE_PREVIOUS);
        TokenType pre = window.getTokenType(PREVIOUS);
        if (!TOKEN_SET.contains(pre) || !TOKEN_SET.contains(doublePre)) {
            return true;
        }
        String cur = window.getTokenType(CURRENT).name();
        HashSet<String> single = CONSTRAINTS.get(pre.name());
        HashSet<String> combine = CONSTRAINTS.get(merge(doublePre.name(), pre.name()));
        if (combine != null) {
            return combine.contains(cur);
        }
        if (single != null) {
            return single.contains(cur);
        }
        return false;
    }

    private static String merge(String tokenNameA, String tokenNameB) {
        return tokenNameA + "-" + tokenNameB;
    }
}
