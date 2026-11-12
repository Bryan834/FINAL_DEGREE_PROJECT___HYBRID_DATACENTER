package datacenter;

import java.util.ArrayList;


public class DataCenter {
    private ArrayList<Rack> racks;
    private ArrayList<Server> servers;
    private ArrayList<Switch> switches;
    private ArrayList<Link> links;
    private ArrayList<Node> nodes;

    public DataCenter() {
        this.racks = new ArrayList<Rack>();
        this.servers = new ArrayList<>();
        this.switches = new ArrayList<>();
        this.links = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    public ArrayList<Rack> getRacks() { return racks; }
    public ArrayList<Server> getServers() { return servers; }
    public ArrayList<Switch> getSwitches() { return switches; }
    public ArrayList<Link> getLinks() { return links; }
    public ArrayList<Node> getNodes() { return nodes; }

    public void addRack(Rack rack) {
        racks.add(rack);
    }

    public void addServer(Server server) {
        servers.add(server);
        nodes.add(server);
    }

    public void addServer(Server server, Rack rack) {
        addServer(server);
        if (rack != null) {
            rack.addServer(server);
        }
    }

    public void addSwitch(Switch sw) {
        switches.add(sw);
        nodes.add(sw);
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public Rack getRackOfServer(Server server) {
        for (Rack rack : racks) {
            if (rack.getServers().contains(server)) {
                return rack;
            }
        }
        return null;
    }

    public void printStatus() {
    	
    	System.out.println("ESTADO DATACENTER: Racks: " + racks.size() + " | Servidores: " + servers.size() +
    			
                           " | Switches: " + switches.size() + " | Enlaces: " + links.size());
        for (Server s : servers) {
            System.out.println("  -> " + s);
        }
    }
}