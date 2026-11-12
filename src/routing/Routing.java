package routing;

import datacenter.DataCenter;
import datacenter.Link;
import datacenter.Node;
import java.util.*;

public class Routing {

    public static Route findRoute(DataCenter dc, Node source, Node destination) {
        if (source.getId().equals(destination.getId())) {
            Route r = new Route(source, destination);
            r.addNode(source);
            return r;
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> parentNode = new HashMap<>();
        Map<Node, Link> parentLink = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);

        boolean found = false;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.getId().equals(destination.getId())) {
                found = true;
                break;
            }

            for (Link link : dc.getLinks()) {
                Node neighbor = null;
                if (link.getSource().getId().equals(current.getId())) {
                    neighbor = link.getDestination();
                } else if (link.getDestination().getId().equals(current.getId())) {
                    neighbor = link.getSource();
                }

                if (neighbor != null && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentNode.put(neighbor, current);
                    parentLink.put(neighbor, link);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) return null;

        List<Node> nodePath = new ArrayList<>();
        List<Link> linkPath = new ArrayList<>();
        Node curr = destination;

        while (curr != null) {
            nodePath.add(0, curr);
            Link l = parentLink.get(curr);
            if (l != null) {
                linkPath.add(0, l);
            }
            curr = parentNode.get(curr);
        }

        Route route = new Route(source, destination);
        for (Node n : nodePath) route.addNode(n);
        for (Link l : linkPath) route.addLink(l);

        return route;
    }
}