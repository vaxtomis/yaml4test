package com.vaxtomis.yaml4test.converter;

import java.math.BigDecimal;

/**
 * BigDecimal Converter.
 * @author vaxtomis
 */
class BigDecimalConverter extends AbstractCustomConverter {
    @Override
    public BigDecimal customConvert(String getV) {
        if (getV.matches("^(-?\\d+)(\\.\\d+)?$")) {
            return new BigDecimal(getV);
        } else {
            return new BigDecimal("0.00");
        }
    }
}
