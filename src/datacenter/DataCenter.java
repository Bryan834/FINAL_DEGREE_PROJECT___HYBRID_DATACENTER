package datacenter;

import java.util.ArrayList;
import java.util.List;

public class DataCenter {
    private List<Server> servers;
    private List<Switch> switches;
    private List<Link> links;
    private List<Node> nodes;

    public DataCenter() {
        this.servers = new ArrayList<>();
        this.switches = new ArrayList<>();
        this.links = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    public List<Server> getServers() { return servers; }
    public List<Switch> getSwitches() { return switches; }
    public List<Link> getLinks() { return links; }
    public List<Node> getNodes() { return nodes; }

    public void addServer(Server server) {
        servers.add(server);
        nodes.add(server);
    }

    public void addSwitch(Switch sw) {
        switches.add(sw);
        nodes.add(sw);
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public Node getNodeById(String id) {
        for (Node n : nodes) {
            if (n.getId().equals(id)) return n;
        }
        return null;
    }

    public void printStatus() {
        System.out.println("ESTADO DATACENTER HÍBRIDO | Servidores: " + servers.size() +
                           " | Switches: " + switches.size() + " | Enlaces: " + links.size());
        for (Server s : servers) {
            System.out.println("  -> " + s);
        }
        for (Link l : links) {
            System.out.println("  -> " + l);
        }
    }
}