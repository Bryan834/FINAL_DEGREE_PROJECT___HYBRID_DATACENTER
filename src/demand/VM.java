package demand;

import java.util.Objects;

public class VM {
    private String id;
    private int xpu;
    private int ram;
    private int disk;

    public VM() {
    }

    public VM(String id, int xpu, int ram, int disk) {
        this.id = id;
        this.xpu = xpu;
        this.ram = ram;
        this.disk = disk;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getXpu() { return xpu; }
    public void setXpu(int xpu) { this.xpu = xpu; }

    public int getRam() { return ram; }
    public void setRam(int ram) { this.ram = ram; }

    public int getDisk() { return disk; }
    public void setDisk(int disk) { this.disk = disk; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VM vm = (VM) o;
        return Objects.equals(id, vm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VM " + id + " [XPU=" + xpu + ", RAM=" + ram + " GB, Disk=" + disk + " GB]";
    }
}