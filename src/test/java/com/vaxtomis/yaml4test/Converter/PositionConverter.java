package com.vaxtomis.yaml4test.Converter;

/**
 * @author vaxtomis
 */
public class PositionConverter extends CustomConverter {
    @Override
    public CustomPosition customConvert(String getV) {
        CustomPosition position = new CustomPosition();
        String[] strs = getV.split("-");
        position.setX(Double.parseDouble(strs[0]));
        position.setY(Double.parseDouble(strs[1]));
        return position;
    }
}
