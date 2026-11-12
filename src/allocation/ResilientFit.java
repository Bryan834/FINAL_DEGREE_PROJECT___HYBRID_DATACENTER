package allocation;

import datacenter.DataCenter;
import datacenter.Link;
import datacenter.Server;
import demand.Demand;
import demand.VM;
import demand.VMConnection;
import routing.Route;
import routing.Routing;
import routing.WavelengthAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResilientFit implements AllocationAlgorithm {

    @Override
    public String getName() {
        return "Resilient-Fit (Anti-Afinidad por Dominio de Fallo)";
    }

    @Override
    public AllocationResult execute(DataCenter datacenter, Demand demand) {
        AllocationResult result = new AllocationResult(demand.getConnections().size());
        Map<VM, Server> vmPlacement = new HashMap<>();

        for (VMConnection conn : demand.getConnections()) {
            VM vm1 = conn.getSourceVM();
            VM vm2 = conn.getTargetVM();
            double reqBw = conn.getRequiredBandwidth();

            boolean allocated = false;

            List<Server> candidatesS1 = vmPlacement.containsKey(vm1)
                    ? Collections.singletonList(vmPlacement.get(vm1))
                    : getServersByLeastLoad(datacenter.getServers());

            for (Server s1 : candidatesS1) {
                boolean vm1Placed = vmPlacement.containsKey(vm1);
                if (!vm1Placed && !s1.canHost(vm1)) continue;

                List<Server> candidatesS2 = vmPlacement.containsKey(vm2)
                        ? Collections.singletonList(vmPlacement.get(vm2))
                        : getServersByLeastLoad(datacenter.getServers());

                for (Server s2 : candidatesS2) {
                    // RESTRICCIÓN DE RESILIENCIA: distinto servidor y distinto dominio de fallo
                    if (s1.equals(s2)) continue;
                    if (s1.getFailureDomain().equalsIgnoreCase(s2.getFailureDomain())) continue;

                    boolean vm2Placed = vmPlacement.containsKey(vm2);
                    if (!vm2Placed && !s2.canHost(vm2)) continue;

                    // Path Computation con Dijkstra (JGraphT)
                    Route route = Routing.findRoute(datacenter, s1, s2);
                    if (route == null || !hasElectricalBandwidth(route, reqBw)) continue;

                    // RWA en tramos ópticos
                    int assignedLambda = 0;
                    boolean opticalRoute = route.hasOpticalLinks();
                    if (opticalRoute) {
                        assignedLambda = WavelengthAssignment.allocateWavelength(route, reqBw);
                        if (assignedLambda == -1) continue;
                    }

                    if (!vm1Placed) { s1.allocateVM(vm1); vmPlacement.put(vm1, s1); }
                    if (!vm2Placed) { s2.allocateVM(vm2); vmPlacement.put(vm2, s2); }
                    reserveElectricalBandwidth(route, reqBw);

                    result.incrementAllocated();
                    allocated = true;
                    System.out.println("[ResilientFit] " + conn.getId() + " (" + vm1.getId() + " <-> " + vm2.getId() + ") -> ASIGNADA");
                    System.out.println("               Dominios aislados: " + s1.getId() + " [" + s1.getFailureDomain() + "] <-> "
                            + s2.getId() + " [" + s2.getFailureDomain() + "]");
                    System.out.println("               " + route + (opticalRoute ? " | Lambda: L" + assignedLambda : " | Eléctrica"));
                    break;
                }
                if (allocated) break;
            }

            if (!allocated) {
                System.out.println("[ResilientFit] " + conn.getId() + " -> RECHAZADA (Sin recursos aislados suficientes)");
                result.incrementRejected();
            }
        }
        return result;
    }

    private List<Server> getServersByLeastLoad(List<Server> servers) {
        List<Server> sorted = new ArrayList<>(servers);
        Collections.sort(sorted, new Comparator<Server>() {
            @Override
            public int compare(Server a, Server b) {
                return Integer.compare(b.getAvailableXpu(), a.getAvailableXpu());
            }
        });
        return sorted;
    }

    private boolean hasElectricalBandwidth(Route route, double reqBw) {
        for (Link link : route.getLinks()) {
            if (!link.isOptical() && !link.hasAvailableBandwidth(reqBw)) return false;
        }
        return true;
    }

    private void reserveElectricalBandwidth(Route route, double reqBw) {
        for (Link link : route.getLinks()) {
            if (!link.isOptical()) link.reserveBandwidth(reqBw);
        }
    }
}