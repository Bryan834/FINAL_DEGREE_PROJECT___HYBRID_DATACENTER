package main;

import allocation.AllocationAlgorithm;
import allocation.AllocationResult;
import allocation.FirstFit;
import allocation.ResilientFit;
import datacenter.DataCenter;
import datacenter.Link;
import datacenter.Server;
import datacenter.Switch;
import demand.Demand;
import demand.DemandJsonManager;
import routing.Lightpath;
import routing.WavelengthAssignment;

public class Main {

    public static void main(String[] args) {
      
            // Escenario 1: Demanda estándar (reutilización de lambdas sin bloqueo)
            runExperiment(new FirstFit(), "demandas.json");
            runExperiment(new ResilientFit(), "demandas.json");

            // Escenario 2: Saturación óptica y bloqueo RWA
            runExperiment(new FirstFit(), "demandas_saturacion.json");
            runExperiment(new ResilientFit(), "demandas_saturacion.json");
        
    }

    private static void runExperiment(AllocationAlgorithm algorithm, String jsonPath) {
        System.out.println("\n######################################################################");
        System.out.println("  EJECUTANDO SIMULACIÓN: " + algorithm.getName());
        System.out.println("######################################################################");

        // Reiniciar estado de red y construir topología híbrida limpia
        WavelengthAssignment.reset();
        DataCenter dc = buildGenericHybridDataCenter();

        // Cargar peticiones estructuradas desde JSON
        Demand demand = DemandJsonManager.importFromJson(jsonPath);
        if (demand == null) {
            System.err.println("No se pudo cargar el archivo " + jsonPath);
            return;
        }

        // Ejecutar orquestación (VM Placement + Dijkstra + RWA)
        AllocationResult result = algorithm.execute(dc, demand);

        // Mostrar métricas y estado final de infraestructura
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

    // Construye un datacenter híbrido genérico (sin topología fija Spine-Leaf)
    private static DataCenter buildGenericHybridDataCenter() {
        DataCenter dc = new DataCenter();

        // Servidores agrupados por dominio de fallo lógico
        Server srv1 = new Server("SRV-1", "ZONA-A");
        srv1.setCapacity(16, 64, 500);
        Server srv3 = new Server("SRV-3", "ZONA-A");
        srv3.setCapacity(16, 64, 500);

        Server srv2 = new Server("SRV-2", "ZONA-B");
        srv2.setCapacity(16, 64, 500);
        Server srv4 = new Server("SRV-4", "ZONA-B");
        srv4.setCapacity(16, 64, 500);

        dc.addServer(srv1);
        dc.addServer(srv3);
        dc.addServer(srv2);
        dc.addServer(srv4);

        // Switches genéricos (eléctricos de acceso + núcleo óptico mallado)
        Switch sw1 = new Switch("SW-1");
        Switch sw2 = new Switch("SW-2");
        Switch opt1 = new Switch("OPT-1");
        Switch opt2 = new Switch("OPT-2");
        Switch opt3 = new Switch("OPT-3");
        Switch opt4 = new Switch("OPT-4");

        dc.addSwitch(sw1);
        dc.addSwitch(sw2);
        dc.addSwitch(opt1);
        dc.addSwitch(opt2);
        dc.addSwitch(opt3);
        dc.addSwitch(opt4);

        // Enlaces eléctricos de acceso (20 Gbps)
        dc.addLink(new Link("LNK-SRV1-SW1", srv1, sw1, 20.0));
        dc.addLink(new Link("LNK-SRV3-SW1", srv3, sw1, 20.0));
        dc.addLink(new Link("LNK-SW2-SRV2", sw2, srv2, 20.0));
        dc.addLink(new Link("LNK-SW2-SRV4", sw2, srv4, 20.0));

        // Enlaces ópticos WDM (4 lambdas de 10 Gbps cada una para ver saturación fácilmente)
        int lambdas = 4;
        double capPerLambda = 10.0;

        dc.addLink(new Link("OPT-SW1-OPT1", sw1, opt1, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-SW1-OPT3", sw1, opt3, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT1-OPT2", opt1, opt2, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT3-OPT4", opt3, opt4, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT1-OPT3", opt1, opt3, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT2-OPT4", opt2, opt4, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT2-SW2", opt2, sw2, lambdas, capPerLambda));
        dc.addLink(new Link("OPT-OPT4-SW2", opt4, sw2, lambdas, capPerLambda));

        return dc;
    }
}