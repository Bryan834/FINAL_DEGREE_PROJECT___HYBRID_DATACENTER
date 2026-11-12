package datacenter;

public class Link {
    private String id;
    private Node source;
    private Node destination;
    private double bandwidth;                  // Capacidad total nominal del enlace
    private double usedBandwidth;              // Ancho de banda ocupado (Enlaces Servidor-ToR)
    private boolean optical;                   // true si es enlace ToR-Spine
    private int numWavelengths;                // 40 interfaces / lambdas
    private double wavelengthCapacity;         // 10.0 Gbps por lambda
    private double[] wavelengthsUsedBandwidth; // Ocupación individual de cada lambda

    // Constructor Enlaces Servidor-ToR (Fibra de 1 Gbps)
    public Link(String id, Node source, Node destination, double bandwidth) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.bandwidth = bandwidth;
        this.usedBandwidth = 0;
        this.optical = false;
        this.numWavelengths = 0;
        this.wavelengthCapacity = 0;
    }

    // Constructor Enlaces Ópticos ToR-Spine (40 Lambdas x 10 Gbps)
    public Link(String id, Node source, Node destination, int numWavelengths, double wavelengthCapacity) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.optical = true;
        this.numWavelengths = numWavelengths;
        this.wavelengthCapacity = wavelengthCapacity;
        this.wavelengthsUsedBandwidth = new double[numWavelengths];
        this.bandwidth = numWavelengths * wavelengthCapacity;
    }

    public String getId() { return id; }
    public Node getSource() { return source; }
    public Node getDestination() { return destination; }
    public double getBandwidth() { return bandwidth; }
    public boolean isOptical() { return optical; }
    public int getNumWavelengths() { return numWavelengths; }
    public double getWavelengthCapacity() { return wavelengthCapacity; }

    // Control para Enlaces Servidor-ToR (1 Gbps)
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

    // Control para Enlaces Ópticos WDM (Traffic Grooming)
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
        if (source != null && destination != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Enlace ").append(id).append(" (").append(source.getId()).append(" <-> ").append(destination.getId()).append(")");
            if (optical) {
                sb.append(" | ÓPTICO WDM (40 Lambdas x 10G) - Canales Activos: ")
                  .append(getOccupiedWavelengthsCount()).append("/").append(numWavelengths).append(" [");
                for (int i = 0; i < numWavelengths; i++) {
                    sb.append(wavelengthsUsedBandwidth[i] > 0 ? "X" : "-");
                }
                sb.append("]");
            } else {
                sb.append(" | Estándar Servidor-ToR (1 Gbps) - Uso: ")
                  .append(String.format("%.2f", usedBandwidth)).append("/").append(bandwidth).append(" Gbps");
            }
            return sb.toString();
        }
        return "Enlace " + id;
    }
}