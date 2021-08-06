package Parser;

import Tokenizer.TokenType;

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
 * BE  -> NEXT      :to NEXT
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
        TOKEN_MAPPING.put(TokenType.KEY, "K");
        TOKEN_MAPPING.put(TokenType.VALUE, "V");
        TOKEN_MAPPING.put(TokenType.SCALAR, "S");
        TOKEN_MAPPING.put(TokenType.CLASSNAME, "C");
        TOKEN_MAPPING.put(TokenType.BLOCK_MAPPING_START, "BMS");
        TOKEN_MAPPING.put(TokenType.BLOCK_SEQUENCE_START, "BSS");
        TOKEN_MAPPING.put(TokenType.BLOCK_ENTRY, "B");
        TOKEN_MAPPING.put(TokenType.BLOCK_END, "BE");


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
                add("NEXT");
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
        String doublePre = TOKEN_MAPPING.get(sw.getDoublePre().getType());
        String pre = TOKEN_MAPPING.get(sw.getPre().getType());
        String cur = TOKEN_MAPPING.get(sw.getCur().getType());
        return CONSTRAINTS.get(pre).contains(cur) ||
                CONSTRAINTS.get(doublePre + "-" + pre).contains(cur);
    }
}
