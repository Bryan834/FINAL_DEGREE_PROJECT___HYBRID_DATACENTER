package datacenter;

import java.util.ArrayList;

public class Rack {
    private String id;
    private ArrayList<Server> servers;

    public Rack(String id) {
        this.id = id;
        this.servers = new ArrayList<>();
    }

    public String getId() { return id; }
    public ArrayList<Server> getServers() { return servers; }

    public void addServer(Server server) {
        this.servers.add(server);
    }

    @Override
    public String toString() {
        return "Rack " + id + " [Servidores: " + servers.size() + "]";
    }
}