package com.vaxtomis.yaml4test.converter;

import java.math.BigDecimal;

/**
 * BigDecimal Converter.
 * @author vaxtomis
 */
class BigDecimalConverter extends AbstractCustomConverter {
    @Override
    public BigDecimal customConvert(String pairValue) {
        if (pairValue.matches("^(-?\\d+)(\\.\\d+)?$")) {
            return new BigDecimal(pairValue);
        } else {
            return new BigDecimal("0.00");
        }
    }
}
