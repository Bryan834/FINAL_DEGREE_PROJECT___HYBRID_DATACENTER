package datacenter;

public class Tor extends Switch {

    public Tor(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return "TOR Switch " + getId();
    }
}