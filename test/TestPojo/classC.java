package TestPojo;

import java.util.Arrays;

public class classC {
    private String E;
    private String F;
    private int[] G;

    public int[] getG() {
        return G;
    }

    public void setG(int[] g) {
        G = g;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    @Override
    public String toString() {
        return "classC{" +
                "E='" + E + '\'' +
                ", F='" + F + '\'' +
                ", G=" + Arrays.toString(G) +
                '}';
    }
}
