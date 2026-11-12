package routing;

import datacenter.DataCenter;
import datacenter.Link;
import datacenter.Node;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

public class Routing {

    public static Route findRoute(DataCenter dc, Node source, Node destination) {
        if (dc == null || source == null || destination == null) {
            return null;
        }

        if (source.equals(destination)) {
            Route localRoute = new Route(source, destination);
            localRoute.addNode(source);
            return localRoute;
        }

        Graph<Node, Link> graph = new WeightedMultigraph<>(Link.class);

        for (Node node : dc.getNodes()) {
            graph.addVertex(node);
        }

        for (Link link : dc.getLinks()) {
            if (link.getSource() != null && link.getDestination() != null
                    && !link.getSource().equals(link.getDestination())) {
                graph.addEdge(link.getSource(), link.getDestination(), link);
                graph.setEdgeWeight(link, link.getWeight());
            }
        }

     
        DijkstraShortestPath<Node, Link> dijkstra = new DijkstraShortestPath<>(graph);
        GraphPath<Node, Link> shortestPath = dijkstra.getPath(source, destination);

        if (shortestPath == null) {
            return null; 
        }

        Route route = new Route(source, destination);
        for (Node n : shortestPath.getVertexList()) {
            route.addNode(n);
        }
        for (Link l : shortestPath.getEdgeList()) {
            route.addLink(l);
        }

        return route;
    }
}