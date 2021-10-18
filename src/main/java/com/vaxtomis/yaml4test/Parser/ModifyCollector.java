package com.vaxtomis.yaml4test.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @description
 *
 * @author vaxotmis
 */
public class ModifyCollector {
    String[][] matrix;
    HashMap<String, Integer> keyAccountMap = new HashMap<>();
    HashMap<String, String> innerMap =  new HashMap();

    /**
     * 有重复返回 true，无重复则返回 false。
     * @param propName
     * @param value
     * @return boolean
     */
    public boolean add(String propName, String value) {
        if (keyAccountMap.containsKey(propName)) {
            int number = keyAccountMap.get(propName);
            innerMap.put(propName + "$" + number, value);
            keyAccountMap.put(propName, number + 1);
            return true;

        } else {
            innerMap.put(propName + "$0", value);
            keyAccountMap.put(propName, 1);
            return false;
        }
    }

    public String[] getValues(String propName) {
        int number = keyAccountMap.getOrDefault(propName, 0);
        if (number == 0) {
            return null;
        }
        String[] array = new String[number];
        for (int i = 0; i < number; i++) {
            array[i] = innerMap.get(propName + "$" + i);
        }
        return array;
    }

    public int size() {
        return keyAccountMap.size();
    }

    public String[] getNames() {
        String[] name = new String[keyAccountMap.size()];
        keyAccountMap.keySet().toArray(name);
        return name;
    }

    /**
     * 记录更变部分，生成矩阵。
     * @return
     */
    public String[][] generateModifyMatrix() {
        String[][] matrix = new String[size()][biggestAccount()];
        String[] propNames = getNames();
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = getValues(propNames[i]);
        }
        this.matrix = matrix;
        return this.matrix;
    }

    public List<List<Position>> generateGroup() {
        List<List<Position>> group = new ArrayList<>();
        group.add(new ArrayList<>());
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int size = group.size();
                for (int k = 0; k < size; k++) {
                    List<Position> newList = new ArrayList<>(group.get(k));
                    newList.add(new Position(i, j));
                    group.add(newList);
                }
            }
        }
        return group;
    }

    public class Position {
        private int x;
        private int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String toString() {
            return "[" + x + "," + y + "]";
        }
    }

    private int biggestAccount() {
        int i = 0;
        for (int j : keyAccountMap.values()) {
            if (i < j) {
                i = j;
            }
        }
        return i;
    }
}