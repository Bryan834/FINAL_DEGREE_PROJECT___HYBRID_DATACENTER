package routing;

import datacenter.Link;
import java.util.ArrayList;
import java.util.List;

public class WavelengthAssignment {

    private static List<Lightpath> activeLightpaths = new ArrayList<>();
    private static int lightpathCounter = 1;

    public static void reset() {
        activeLightpaths.clear();
        lightpathCounter = 1;
    }

    public static List<Lightpath> getActiveLightpaths() {
        return activeLightpaths;
    }

    public static int allocateWavelength(Route route, double requiredBandwidth) {
        if (route == null || route.getLinks().isEmpty()) return -1;

        // Extraer únicamente los enlaces ópticos de la ruta
        List<Link> opticalLinks = new ArrayList<>();
        int minLambdas = Integer.MAX_VALUE;
        double minLambdaCapacity = Double.MAX_VALUE;

        for (Link link : route.getLinks()) {
            if (link.isOptical()) {
                opticalLinks.add(link);
                minLambdas = Math.min(minLambdas, link.getNumWavelengths());
                minLambdaCapacity = Math.min(minLambdaCapacity, link.getWavelengthCapacity());
            }
        }

        if (opticalLinks.isEmpty()) {
            return 0; // Ruta 100% eléctrica, no requiere RWA
        }

        // FASE 1: Buscar si ya existe un Lightpath en ESTE MISMO CAMINO ÓPTICO con capacidad libre
        for (Lightpath lp : activeLightpaths) {
            if (lp.matchesOpticalPath(opticalLinks) && lp.canFit(requiredBandwidth)) {
                int lambda = lp.getLambda();
                lp.allocateBandwidth(requiredBandwidth);
                for (Link link : opticalLinks) {
                    link.reserveLambda(lambda, requiredBandwidth);
                }
                return lambda;
            }
        }

        // FASE 2: Si no existe Lightpath o los existentes están llenos, abrir uno nuevo con First-Fit
        for (int lambda = 0; lambda < minLambdas; lambda++) {
            boolean completelyFreeOnAllLinks = true;

            for (Link link : opticalLinks) {
                // La lambda debe estar totalmente libre en toda la ruta para evitar colisiones con otros caminos
                if (link.isLambdaActive(lambda)) {
                    completelyFreeOnAllLinks = false;
                    break;
                }
            }

            if (completelyFreeOnAllLinks && minLambdaCapacity >= requiredBandwidth) {
                String lpId = "LP-" + String.format("%02d", lightpathCounter++);
                Lightpath newLp = new Lightpath(lpId, opticalLinks, lambda, minLambdaCapacity);
                newLp.allocateBandwidth(requiredBandwidth);
                activeLightpaths.add(newLp);

                for (Link link : opticalLinks) {
                    link.reserveLambda(lambda, requiredBandwidth);
                }
                return lambda;
            }
        }

        return -1; // Bloqueo RWA: no hay lambdas libres disponibles para este camino
    }

    public static void releaseWavelength(Route route, int lambda, double requiredBandwidth) {
        if (route == null || lambda < 0) return;

        List<Link> opticalLinks = new ArrayList<>();
        for (Link link : route.getLinks()) {
            if (link.isOptical()) {
                opticalLinks.add(link);
                link.releaseLambda(lambda, requiredBandwidth);
            }
        }

        for (int i = 0; i < activeLightpaths.size(); i++) {
            Lightpath lp = activeLightpaths.get(i);
            if (lp.getLambda() == lambda && lp.matchesOpticalPath(opticalLinks)) {
                lp.releaseBandwidth(requiredBandwidth);
                if (lp.getConnectionCount() == 0) {
                    activeLightpaths.remove(i);
                }
                break;
            }
        }
    }
}