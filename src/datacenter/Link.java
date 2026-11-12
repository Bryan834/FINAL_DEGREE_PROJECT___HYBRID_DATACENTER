package datacenter;

public class Link {
    private String id;
    private Node source;
    private Node destination;
    private double weight;                     // Coste/latencia del enlace para Path Computation
    private double bandwidth;                  // Capacidad total en Gbps
    private double usedBandwidth;              // Ancho de banda ocupado (enlaces eléctricos)
    private boolean optical;                   // true = enlace óptico WDM, false = enlace eléctrico
    private int numWavelengths;                // Número de canales (lambdas) disponibles
    private double wavelengthCapacity;         // Capacidad en Gbps de cada lambda
    private double[] wavelengthsUsedBandwidth; // Ocupación individual de cada lambda

    // Constructor para enlaces eléctricos / clásicos
    public Link(String id, Node source, Node destination, double bandwidth) {
        this(id, source, destination, bandwidth, 1.0);
    }

    public Link(String id, Node source, Node destination, double bandwidth, double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.bandwidth = bandwidth;
        this.weight = weight;
        this.usedBandwidth = 0;
        this.optical = false;
        this.numWavelengths = 0;
        this.wavelengthCapacity = 0;
    }

    // Constructor para enlaces ópticos WDM (con N lambdas de capacidad C)
    public Link(String id, Node source, Node destination, int numWavelengths, double wavelengthCapacity) {
        this(id, source, destination, numWavelengths, wavelengthCapacity, 1.0);
    }

    public Link(String id, Node source, Node destination, int numWavelengths, double wavelengthCapacity, double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.optical = true;
        this.numWavelengths = numWavelengths;
        this.wavelengthCapacity = wavelengthCapacity;
        this.wavelengthsUsedBandwidth = new double[numWavelengths];
        this.bandwidth = numWavelengths * wavelengthCapacity;
    }

    public String getId() { return id; }
    public Node getSource() { return source; }
    public Node getDestination() { return destination; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getBandwidth() { return bandwidth; }
    public boolean isOptical() { return optical; }
    public int getNumWavelengths() { return numWavelengths; }
    public double getWavelengthCapacity() { return wavelengthCapacity; }

    public boolean hasAvailableBandwidth(double reqBw) {
        if (!optical) return (bandwidth - usedBandwidth) >= reqBw;
        return true;
    }

    public void reserveBandwidth(double reqBw) {
        if (!optical) usedBandwidth += reqBw;
    }

    public void releaseBandwidth(double reqBw) {
        if (!optical) usedBandwidth = Math.max(0, usedBandwidth - reqBw);
    }

    public boolean isLambdaAvailable(int lambda, double reqBw) {
        if (!optical || lambda < 0 || lambda >= numWavelengths) return false;
        return (wavelengthCapacity - wavelengthsUsedBandwidth[lambda]) >= reqBw;
    }

    public boolean isLambdaActive(int lambda) {
        if (!optical || lambda < 0 || lambda >= numWavelengths) return false;
        return wavelengthsUsedBandwidth[lambda] > 0;
    }

    public void reserveLambda(int lambda, double reqBw) {
        if (optical && lambda >= 0 && lambda < numWavelengths) {
            wavelengthsUsedBandwidth[lambda] += reqBw;
        }
    }

    public void releaseLambda(int lambda, double reqBw) {
        if (optical && lambda >= 0 && lambda < numWavelengths) {
            wavelengthsUsedBandwidth[lambda] = Math.max(0, wavelengthsUsedBandwidth[lambda] - reqBw);
        }
    }

    public int getOccupiedWavelengthsCount() {
        if (!optical) return 0;
        int count = 0;
        for (double used : wavelengthsUsedBandwidth) {
            if (used > 0) count++;
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enlace ").append(id);
        if (source != null && destination != null) {
            sb.append(" (").append(source.getId()).append(" <-> ").append(destination.getId()).append(")");
        }
        if (optical) {
            sb.append(" | ÓPTICO WDM (").append(numWavelengths).append(" lambdas x ")
              .append(wavelengthCapacity).append(" Gbps) - Activas: ")
              .append(getOccupiedWavelengthsCount()).append("/").append(numWavelengths).append(" [");
            for (int i = 0; i < numWavelengths; i++) {
                sb.append(wavelengthsUsedBandwidth[i] > 0 ? "X" : "-");
            }
            sb.append("]");
        } else {
            sb.append(" | ELÉCTRICO (").append(bandwidth).append(" Gbps) - Uso: ")
              .append(String.format("%.2f", usedBandwidth)).append("/").append(bandwidth).append(" Gbps");
        }
        return sb.toString();
    }
}