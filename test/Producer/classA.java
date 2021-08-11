package Producer;

/**
 * @author vaxtomis
 */
public class classA {
    private classB B;
    private String C;
    private String D;

    public classB getB() {
        return B;
    }

    public void setB(classB b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
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
