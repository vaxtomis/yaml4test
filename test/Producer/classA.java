package Producer;

/**
 * @author vaxtomis
 */
public class classA {
    private classB B;
    private int C;
    private String D;

    public classB getB() {
        return B;
    }

    public void setB(classB b) {
        B = b;
    }

    public int getC() {
        return C;
    }

    public void setC(int c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    @Override
    public String toString() {
        return "classA{" +
                "B=" + B +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                '}';
    }
}
