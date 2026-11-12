package demand;

import java.util.ArrayList;
import java.util.List;

public class Demand {
    private List<VM> vms;
    private List<VMConnection> connections;

    public Demand() {
        this.vms = new ArrayList<VM>();
        this.connections = new ArrayList<>();
    }

    // Añadir una VM creada manualmente
    public void addVM(VM vm) {
        this.vms.add(vm);
        
        
    }

    // Añadir una conexión entre dos VMs
    public void addConnection(VMConnection connection) {
        this.connections.add(connection);
    }

    // Método helper para crear e inyectar una conexión en un solo paso
    public void createConnection(String connId, VM sourceVM, VM targetVM, double bandwidthGbps) {
        if (!vms.contains(sourceVM)) vms.add(sourceVM);
        if (!vms.contains(targetVM)) vms.add(targetVM);
        VMConnection conn = new VMConnection(connId, sourceVM, targetVM, bandwidthGbps);
        this.connections.add(conn);
    }

    public List<VM> getVms() { return vms; }
    public List<VMConnection> getConnections() { return connections; }
}