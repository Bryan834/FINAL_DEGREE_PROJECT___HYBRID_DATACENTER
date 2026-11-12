package routing;

import datacenter.Link;
import datacenter.Node;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private Node source;
    private Node destination;
    private List<Node> nodes;
    private List<Link> links;

    public Route(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
        this.nodes = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public void addNode(Node node) { nodes.add(node); }
    public void addLink(Link link) { links.add(link); }

    public Node getSource() { return source; }
    public Node getDestination() { return destination; }
    public List<Node> getNodes() { return nodes; }
    public List<Link> getLinks() { return links; }

    public int getHopCount() {
        return links.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ruta (").append(source.getId()).append(" -> ").append(destination.getId()).append("): ");
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(nodes.get(i).getId());
            if (i < nodes.size() - 1) sb.append(" -> ");
        }
        sb.append(" [Saltos: ").append(getHopCount()).append("]");
        return sb.toString();
    }
}