package com.vaxtomis.yaml4test.Parser;

import com.vaxtomis.yaml4test.Tokenizer.TokenType;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @description Define the constraints for tokens parser.
 * @author vaxtomis
 *
 * K   -> S         :k-v get key's scalar
 * KS  -> V         :k-v need value
 * VS  -> BE        :block end
 *     -> K         :next key
 * ES  -> E         :next entry
 *     -> BE        :block sequence close
 * BE  -> K         :new key
 *     -> E         :block entry
 *     -> BE        :block end include sequence end and mapping end
 *     -> SE        :stream end
 * V   -> S         :k-v complete
 *     -> C         :get sub class name
 *     -> BMS       :sub mapping start
 *     -> BSS       :sub sequencing start
 * C   -> BMS       :sub class start
 * BMS -> K         :first k-v
 * BSS -> E         :first entry
 * E   -> S         :value of entry
 *     -> C         :get entry class name
 *     -> BMS       :start sub block mapping
 */
public class ConstraintsMap {
    private final static HashMap<TokenType, String> TOKEN_MAPPING = new HashMap<>();
    private final static HashMap<String, HashSet<String>> CONSTRAINTS = new HashMap<>();
    static {
        TOKEN_MAPPING.put(TokenType.STREAM_START, "SS");
        TOKEN_MAPPING.put(TokenType.STREAM_END, "SE");
        TOKEN_MAPPING.put(TokenType.KEY, "K");
        TOKEN_MAPPING.put(TokenType.VALUE, "V");
        TOKEN_MAPPING.put(TokenType.SCALAR, "S");
        TOKEN_MAPPING.put(TokenType.CLASSNAME, "C");
        TOKEN_MAPPING.put(TokenType.BLOCK_MAPPING_START, "BMS");
        TOKEN_MAPPING.put(TokenType.BLOCK_SEQUENCE_START, "BSS");
        TOKEN_MAPPING.put(TokenType.BLOCK_ENTRY, "E");
        TOKEN_MAPPING.put(TokenType.BLOCK_END, "BE");


        CONSTRAINTS.put("SS", new HashSet<String>() {
            {
                add("BMS");
            }
        });



        CONSTRAINTS.put("K", new HashSet<String>() {
            {
                add("S");
            }
        });

        CONSTRAINTS.put("K-S", new HashSet<String>() {
            {
                add("V");
            }
        });

        CONSTRAINTS.put("V-S", new HashSet<String>() {
            {
                add("BE");
                add("K");
            }
        });

        CONSTRAINTS.put("E-S", new HashSet<String>() {
            {
                add("E");
                add("BE");
            }
        });

        CONSTRAINTS.put("BE", new HashSet<String>() {
            {
                add("K");
                add("E");
                add("BE");
                add("SE");
            }
        });

        CONSTRAINTS.put("V", new HashSet<String>() {
            {
                add("S");
                add("C");
                add("BMS");
                add("BSS");
            }
        });

        CONSTRAINTS.put("C", new HashSet<String>() {
            {
                add("BMS");
            }
        });

        CONSTRAINTS.put("BMS", new HashSet<String>() {
            {
                add("K");
            }
        });

        CONSTRAINTS.put("BSS", new HashSet<String>() {
            {
                add("E");
            }
        });

        CONSTRAINTS.put("E", new HashSet<String>() {
            {
                add("S");
                add("C");
                add("BMS");
            }
        });
    }
    public static boolean isAllowed(Parser.SlidingWindow sw) {
        String doublePre = TOKEN_MAPPING.get(sw.getTokenType("DPre"));
        String pre = TOKEN_MAPPING.get(sw.getTokenType("Pre"));
        String cur = TOKEN_MAPPING.get(sw.getTokenType("Cur"));
        if (pre == null || doublePre == null) return true;
        HashSet<String> single = CONSTRAINTS.get(pre);
        HashSet<String> combine = CONSTRAINTS.get(doublePre + "-" + pre);
        if (combine != null) {
            return combine.contains(cur);
        }
        if (single != null) {
            return single.contains(cur);
        }
        return false;
    }
}
