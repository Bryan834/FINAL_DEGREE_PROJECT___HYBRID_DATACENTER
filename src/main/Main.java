package main;

import allocation.AllocationAlgorithm;
import allocation.AllocationResult;
import allocation.FirstFit;
import allocation.ResilientFit;
import datacenter.DataCenter;
import datacenter.DataCenterJsonManager;
import demand.Demand;
import demand.DemandJsonManager;
import routing.Lightpath;
import routing.WavelengthAssignment;

public class Main {

    public static void main(String[] args) {
        String topologyPath = "topologia.json";
        String demandPath = "demandas.json";

        // 1. Ejecutar First-Fit (Consolidación)
        runExperiment(new FirstFit(), topologyPath, demandPath);

        // 2. Ejecutar Resilient-Fit (Anti-afinidad entre dominios de fallo)
        runExperiment(new ResilientFit(), topologyPath, demandPath);
    }

    private static void runExperiment(AllocationAlgorithm algorithm, String topologyPath, String demandPath) {
        System.out.println("\n######################################################################");
        System.out.println("  EJECUTANDO SIMULACIÓN: " + algorithm.getName());
        System.out.println("######################################################################");

        // Reiniciar estado óptico y cargar infraestructura limpia desde JSON
        WavelengthAssignment.reset();
        DataCenter dc = DataCenterJsonManager.importFromJson(topologyPath);
        if (dc == null) return;

        // Cargar peticiones estructuradas desde JSON
        Demand demand = DemandJsonManager.importFromJson(demandPath);
        if (demand == null) return;

        // Ejecutar orquestación (VM Placement + Dijkstra + RWA)
        AllocationResult result = algorithm.execute(dc, demand);

        // Mostrar métricas y estado final
        result.printSummary(algorithm.getName());

        System.out.println("\n--- LIGHTPATHS ÓPTICOS ESTABLECIDOS (RWA) ---");
        if (WavelengthAssignment.getActiveLightpaths().isEmpty()) {
            System.out.println("  (Ninguno: todo el tráfico se resolvió localmente o por red eléctrica)");
        } else {
            for (Lightpath lp : WavelengthAssignment.getActiveLightpaths()) {
                System.out.println("  * " + lp);
            }
        }

        System.out.println("\n--- ESTADO FINAL DE SERVIDORES Y ENLACES ---");
        dc.printStatus();
    }
}