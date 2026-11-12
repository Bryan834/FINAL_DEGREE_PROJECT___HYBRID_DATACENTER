package demand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demand {
    private List<VM> vms;
    private List<VMConnection> connections;

    public Demand() {
        this.vms = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public void addVM(VM vm) {
        if (!vms.contains(vm)) {
            this.vms.add(vm);
        }
    }

    public void addConnection(VMConnection connection) {
        this.connections.add(connection);
    }

    public void createConnection(String connId, VM sourceVM, VM targetVM, double bandwidthGbps) {
        addVM(sourceVM);
        addVM(targetVM);
        this.connections.add(new VMConnection(connId, sourceVM, targetVM, bandwidthGbps));
    }

    public void normalize() {
        if (this.vms == null) this.vms = new ArrayList<>();
        if (this.connections == null) this.connections = new ArrayList<>();

        Map<String, VM> vmRegistry = new HashMap<>();
        for (VM vm : this.vms) {
            if (vm != null && vm.getId() != null) {
                vmRegistry.put(vm.getId(), vm);
            }
        }

        for (VMConnection conn : this.connections) {
            if (conn.getSourceVM() != null && conn.getSourceVM().getId() != null) {
                VM existing = vmRegistry.get(conn.getSourceVM().getId());
                if (existing != null) {
                    conn.setSourceVM(existing);
                } else {
                    vmRegistry.put(conn.getSourceVM().getId(), conn.getSourceVM());
                    this.vms.add(conn.getSourceVM());
                }
            }
            if (conn.getTargetVM() != null && conn.getTargetVM().getId() != null) {
                VM existing = vmRegistry.get(conn.getTargetVM().getId());
                if (existing != null) {
                    conn.setTargetVM(existing);
                } else {
                    vmRegistry.put(conn.getTargetVM().getId(), conn.getTargetVM());
                    this.vms.add(conn.getTargetVM());
                }
            }
        }
    }

    public List<VM> getVms() { return vms; }
    public List<VMConnection> getConnections() { return connections; }
}