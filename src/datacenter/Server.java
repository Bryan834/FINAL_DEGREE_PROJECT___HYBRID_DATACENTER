package datacenter;

import java.util.ArrayList;
import java.util.List;
import demand.VM;

public class Server extends Node {
    private int xpu;
    private int ram;
    private int disk;
    private NodeType type;
    private boolean active;

    private int usedXpu;
    private int usedRam;
    private int usedDisk;
    private List<VM> hostedVMs;

    public Server(String id) {
        super(id);
        this.type = NodeType.Server;
        this.active = true;
        this.hostedVMs = new ArrayList<>();
    }

    public void setCapacity(int xpu, int ram, int disk) {
        this.xpu = xpu;
        this.ram = ram;
        this.disk = disk;
    }

    public boolean canHost(VM vm) {
        if (!active) return false;
        return (getAvailableXpu() >= vm.getXpu()) &&
               (getAvailableRam() >= vm.getRam()) &&
               (getAvailableDisk() >= vm.getDisk());
    }

    public boolean allocateVM(VM vm) {
        if (!canHost(vm)) return false;
        hostedVMs.add(vm);
        usedXpu += vm.getXpu();
        usedRam += vm.getRam();
        usedDisk += vm.getDisk();
        return true;
    }

    public boolean releaseVM(VM vm) {
        if (hostedVMs.remove(vm)) {
            usedXpu -= vm.getXpu();
            usedRam -= vm.getRam();
            usedDisk -= vm.getDisk();
            return true;
        }
        return false;
    }

    public int getXpu() { return xpu; }
    public void setXpu(int xpu) { this.xpu = xpu; }

    public int getRam() { return ram; }
    public void setRam(int ram) { this.ram = ram; }

    public int getDisk() { return disk; }
    public void setDisk(int disk) { this.disk = disk; }

    public NodeType getType() { return type; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getAvailableXpu() { return xpu - usedXpu; }
    public int getAvailableRam() { return ram - usedRam; }
    public int getAvailableDisk() { return disk - usedDisk; }

    public List<VM> getHostedVMs() { return hostedVMs; }

    @Override
    public String toString() {
        return "Server " + getId() + " [Estado: " + (active ? "ACTIVO" : "INACTIVO") +
               " | XPU: " + usedXpu + "/" + xpu +
               " | RAM: " + usedRam + "/" + ram + " GB" +
               " | Disco: " + usedDisk + "/" + disk + " GB" +
               " | VMs alojadas: " + hostedVMs.size() + "]";
    }
}