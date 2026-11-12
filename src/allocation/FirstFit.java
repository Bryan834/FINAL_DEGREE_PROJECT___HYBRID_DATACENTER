package allocation;

import datacenter.*;
import demand.*;
import routing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstFit {

    public AllocationResult execute(DataCenter datacenter, Demand demand) {
        AllocationResult result = new AllocationResult(demand.getConnections().size());
        List<Rack> racks = datacenter.getRacks();

        for (VMConnection conn : demand.getConnections()) {
            VM vm1 = conn.getSourceVM();
            VM vm2 = conn.getTargetVM();
            double reqBw = conn.getRequiredBandwidth();

            boolean allocated = false;

            // 1. Generar todos los pares posibles de Racks distintos (Inter-Rack obligatorio)
            List<RackPair> rackPairs = new ArrayList<>();
            for (Rack r1 : racks) {
                for (Rack r2 : racks) {
                    if (!r1.getId().equals(r2.getId())) {
                        rackPairs.add(new RackPair(r1, r2));
                    }
                }
            }

            // 2. Aleatorizar la selección de los Racks origen y destino
            Collections.shuffle(rackPairs);

            // 3. Probar las parejas de racks elegidas de forma aleatoria
            for (RackPair pair : rackPairs) {
                Rack rackOrigen = pair.r1;
                Rack rackDestino = pair.r2;

                // 3a. FIRST-FIT en Rack Origen: primer servidor con cómputo + red suficiente
                Server s1 = null;
                Link linkS1 = null;
                for (Server s : rackOrigen.getServers()) {
                    if (s.canHost(vm1)) {
                        Link l = getAccessLink(datacenter, s);
                        if (l == null || l.hasAvailableBandwidth(reqBw)) {
                            s1 = s;
                            linkS1 = l;
                            break; // Primer servidor encontrado en el Rack origen
                        }
                    }
                }
                if (s1 == null) continue; // Probar siguiente pareja de racks si este rack origen no tiene hueco

                // 3b. FIRST-FIT en Rack Destino: primer servidor con cómputo + red suficiente
                Server s2 = null;
                Link linkS2 = null;
                for (Server s : rackDestino.getServers()) {
                    if (s.canHost(vm2)) {
                        Link l = getAccessLink(datacenter, s);
                        if (l == null || l.hasAvailableBandwidth(reqBw)) {
                            s2 = s;
                            linkS2 = l;
                            break; // Primer servidor encontrado en el Rack destino
                        }
                    }
                }
                if (s2 == null) continue; // Probar siguiente pareja de racks

                // 4. Evaluar Ruta Física y RWA en los enlaces ópticos ToR-Spine
                Route route = Routing.findRoute(datacenter, s1, s2);
                if (route == null) continue;

                int assignedLambda = WavelengthAssignment.allocateWavelength(route, reqBw);
                if (assignedLambda == -1) continue; // Sin recursos ópticos disponibles

                // Asignar definitivamente los recursos
                s1.allocateVM(vm1);
                s2.allocateVM(vm2);
                if (linkS1 != null) linkS1.reserveBandwidth(reqBw);
                if (linkS2 != null) linkS2.reserveBandwidth(reqBw);

                result.incrementAllocated();
                allocated = true;

                System.out.println("[RandomRack+FirstFit] " + conn.getId() + " (" + vm1.getId() + " <-> " + vm2.getId() + ") -> ASIGNADA");
                System.out.println("                      Racks (Random): Origen=" + rackOrigen.getId() + " | Destino=" + rackDestino.getId());
                System.out.println("                      Servidores (First-Fit): Origen=" + s1.getId() + " | Destino=" + s2.getId());
                System.out.println("                      Lambda ToR-Spine: L" + assignedLambda);
                break;
            }

            if (!allocated) {
                System.out.println("[RandomRack+FirstFit] " + conn.getId() + " RECHAZADA -> Sin capacidad en Cómputo/Red/RWA");
                result.incrementRejected();
            }
        }

        return result;
    }

    private Link getAccessLink(DataCenter dc, Server server) {
        for (Link link : dc.getLinks()) {
            if (!link.isOptical() && (link.getSource().getId().equals(server.getId()) || link.getDestination().getId().equals(server.getId()))) {
                return link;
            }
        }
        return null;
    }

    private static class RackPair {
        Rack r1, r2;
        RackPair(Rack r1, Rack r2) {
            this.r1 = r1;
            this.r2 = r2;
        }
    }
}