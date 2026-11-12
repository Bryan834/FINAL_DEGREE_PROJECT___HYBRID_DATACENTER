package demand;

public class VMConnection {
    private String id;
    private VM sourceVM;
    private VM targetVM;
    private double requiredBandwidth;

    public VMConnection() {
    }

    public VMConnection(String id, VM sourceVM, VM targetVM, double requiredBandwidth) {
        this.id = id;
        this.sourceVM = sourceVM;
        this.targetVM = targetVM;
        this.requiredBandwidth = requiredBandwidth;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public VM getSourceVM() { return sourceVM; }
    public void setSourceVM(VM sourceVM) { this.sourceVM = sourceVM; }

    public VM getTargetVM() { return targetVM; }
    public void setTargetVM(VM targetVM) { this.targetVM = targetVM; }

    public double getRequiredBandwidth() { return requiredBandwidth; }
    public void setRequiredBandwidth(double requiredBandwidth) { this.requiredBandwidth = requiredBandwidth; }

    @Override
    public String toString() {
        return "Conexión " + id + " [" + sourceVM.getId() + " <-> " + targetVM.getId() + " | BW: " + requiredBandwidth + " Gbps]";
    }
}