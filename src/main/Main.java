package main;

import datacenter.*;
import demand.*;
import allocation.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== INFRAESTRUCTURA DEL DATACENTER ===");

        // 1. Inicializar la red y la topología física
        DataCenter dc = new DataCenter();

        Switch spine1 = new Switch("SPINE-01");
        Switch spine2 = new Switch("SPINE-02");
        dc.addSwitch(spine1);
        dc.addSwitch(spine2);

        int numLambdas = 40;
        double lambdaCapacity = 10.0;

        for (int r = 1; r <= 2; r++) {
            Rack rack = new Rack("RACK-0" + r);
            dc.addRack(rack);

            Tor tor = new Tor("TOR-0" + r);
            dc.addSwitch(tor);

            dc.addLink(new Link("LNK-TOR0" + r + "-SPINE01", tor, spine1, numLambdas, lambdaCapacity));
            dc.addLink(new Link("LNK-TOR0" + r + "-SPINE02", tor, spine2, numLambdas, lambdaCapacity));

            for (int s = 1; s <= 3; s++) {
                String serverId = "SRV-R" + r + "-0" + s;
                Server server = new Server(serverId);
                server.setCapacity(16, 64, 1000);
                dc.addServer(server, rack);

                dc.addLink(new Link("LNK-" + serverId + "-TOR0" + r, server, tor, 1.0));
            }
        }

        dc.printStatus();

        // 2. Cargar la demanda desde el archivo JSON
        String jsonPath = "demandas.json";
        Demand demand = DemandJsonManager.importFromJson(jsonPath);

        if (demand != null) {
            System.out.println("\n=== PETICIONES CARGADAS (" + demand.getConnections().size() + " CONEXIONES) ===");
            for (VMConnection conn : demand.getConnections()) {
                System.out.println("  " + conn);
            }

            // 3. Ejecutar algoritmo Random-Rack + First-Fit Servidores + RWA
            System.out.println("\n=== EJECUTANDO ASIGNACIÓN CÓMPUTO + RWA ===");
            FirstFit firstFit = new FirstFit();
            AllocationResult result = firstFit.execute(dc, demand);

            result.printSummary("Random-Rack + First-Fit Servidores desde JSON");
        }
    }
}