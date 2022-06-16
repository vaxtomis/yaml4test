package com.vaxtomis.yaml4test.converter;

import java.math.BigDecimal;
import static com.vaxtomis.yaml4test.common.Define.MATCH_BIG_DECIMAL;

/**
 * BigDecimal Converter.
 * @author vaxtomis
 */
class BigDecimalConverter extends AbstractCustomConverter {
    @Override
    public BigDecimal customConvert(String pairValue) {
        if (pairValue.matches(MATCH_BIG_DECIMAL)) {
            return new BigDecimal(pairValue);
        } else {
            return new BigDecimal("0.00");
        }
    }
}
