package routing;

import datacenter.Link;
import java.util.ArrayList;
import java.util.List;

public class Lightpath {
    private String id;
    private List<Link> opticalLinks;
    private int lambda;
    private double totalCapacity;
    private double usedCapacity;
    private int connectionCount;

    public Lightpath(String id, List<Link> opticalLinks, int lambda, double totalCapacity) {
        this.id = id;
        this.opticalLinks = new ArrayList<>(opticalLinks);
        this.lambda = lambda;
        this.totalCapacity = totalCapacity;
        this.usedCapacity = 0.0;
        this.connectionCount = 0;
    }

    // Comprueba si otra ruta sigue exactamente el mismo camino óptico (en cualquier sentido)
    public boolean matchesOpticalPath(List<Link> otherOpticalLinks) {
        if (otherOpticalLinks == null || otherOpticalLinks.size() != opticalLinks.size()) {
            return false;
        }
        int n = opticalLinks.size();
        boolean forwardMatch = true;
        boolean reverseMatch = true;

        for (int i = 0; i < n; i++) {
            if (!opticalLinks.get(i).equals(otherOpticalLinks.get(i))) {
                forwardMatch = false;
            }
            if (!opticalLinks.get(i).equals(otherOpticalLinks.get(n - 1 - i))) {
                reverseMatch = false;
            }
        }
        return forwardMatch || reverseMatch;
    }

    public boolean canFit(double requiredBandwidth) {
        return (totalCapacity - usedCapacity) >= requiredBandwidth;
    }

    public void allocateBandwidth(double requiredBandwidth) {
        this.usedCapacity += requiredBandwidth;
        this.connectionCount++;
    }

    public void releaseBandwidth(double requiredBandwidth) {
        this.usedCapacity = Math.max(0.0, this.usedCapacity - requiredBandwidth);
        this.connectionCount = Math.max(0, this.connectionCount - 1);
    }

    public String getId() { return id; }
    public List<Link> getOpticalLinks() { return opticalLinks; }
    public int getLambda() { return lambda; }
    public double getTotalCapacity() { return totalCapacity; }
    public double getUsedCapacity() { return usedCapacity; }
    public int getConnectionCount() { return connectionCount; }

    @Override
    public String toString() {
        StringBuilder pathStr = new StringBuilder();
        for (int i = 0; i < opticalLinks.size(); i++) {
            pathStr.append(opticalLinks.get(i).getId());
            if (i < opticalLinks.size() - 1) pathStr.append(" -> ");
        }
        return id + " [Camino óptico: " + pathStr + " | Lambda: L" + lambda +
               " | Capacidad: " + String.format("%.2f", usedCapacity) + "/" + totalCapacity +
               " Gbps | Conexiones: " + connectionCount + "]";
    }
}