package com.vaxtomis.yaml4test.converter;

/**
 * @author vaxtomis
 */
public class PositionConverter extends AbstractCustomConverter {
    @Override
    public CustomPosition customConvert(String pairValue) {
        CustomPosition position = new CustomPosition();
        String[] strs = pairValue.split("-");
        position.setX(Double.parseDouble(strs[0]));
        position.setY(Double.parseDouble(strs[1]));
        return position;
    }
}
