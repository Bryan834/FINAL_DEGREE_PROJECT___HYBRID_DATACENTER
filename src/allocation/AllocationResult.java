package allocation;

public class AllocationResult {
    private int totalRequests;
    private int allocatedCount;
    private int rejectedCount;

    public AllocationResult(int totalRequests) {
        this.totalRequests = totalRequests;
        this.allocatedCount = 0;
        this.rejectedCount = 0;
    }

    public void incrementAllocated() {
        this.allocatedCount++;
    }

    public void incrementRejected() {
        this.rejectedCount++;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getAllocatedCount() {
        return allocatedCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public double getSuccessPercentage() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return ((double) allocatedCount / totalRequests) * 100.0;
    }

    public void printSummary(String algorithmName) {
        System.out.println("\n================ RESUMEN DE ASIGNACIÓN (" + algorithmName + ") ================");
        System.out.println("Total de peticiones de conexión:       " + totalRequests);
        System.out.println("Conexiones y VMs asignadas con Éxito: " + allocatedCount);
        System.out.println("Peticiones rechazadas: " + rejectedCount);
        System.out.printf("Tasa / Porcentaje de Éxito:           %.2f%%\n", getSuccessPercentage());
        System.out.println("=======================================================================");
    }
}