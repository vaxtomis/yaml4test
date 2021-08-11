package Producer;

/**
 * @author vaxtomis
 */
public class classB {
    private String suba;
    private String subb;

    public String getSuba() {
        return suba;
    }

    public String getSubb() {
        return subb;
    }

    public void setSuba(String suba) {
        this.suba = suba;
    }

    public void setSubb(String subb) {
        this.subb = subb;
    }

    @Override
    public String toString() {
        return "classB{" +
                "suba='" + suba + '\'' +
                ", subb='" + subb + '\'' +
                '}';
    }
}
