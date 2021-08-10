package Parser;

/**
 * @description Indicator for get name event.
 */
public class NameEvent extends Event {
    private String name;
    public NameEvent(String name) {
        super(EventType.GET_NAME);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "{Name: " + name + "}" ;
    }

    public String getName() {
        return name;
    }
}