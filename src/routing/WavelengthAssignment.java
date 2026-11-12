package routing;

import datacenter.Link;

public class WavelengthAssignment {

    public static int allocateWavelength(Route route, double requiredBandwidth) {
        if (route == null || route.getLinks().isEmpty()) return -1;

        int numLambdas = 0;
        for (Link link : route.getLinks()) {
            if (link.isOptical()) {
                numLambdas = link.getNumWavelengths();
                break;
            }
        }

        if (numLambdas == 0) return 0; // Ruta sin tramo óptico WDM

        // FASE 1: Reutilizar una lambda activa en el camino si dispone de capacidad suficiente (Traffic Grooming)
        for (int lambda = 0; lambda < numLambdas; lambda++) {
            boolean isUsedOnRoute = false;
            boolean canFitOnAllLinks = true;

            for (Link link : route.getLinks()) {
                if (link.isOptical()) {
                    if (link.isLambdaActive(lambda)) {
                        isUsedOnRoute = true;
                    }
                    if (!link.isLambdaAvailable(lambda, requiredBandwidth)) {
                        canFitOnAllLinks = false;
                        break;
                    }
                }
            }

            if (isUsedOnRoute && canFitOnAllLinks) {
                for (Link link : route.getLinks()) {
                    if (link.isOptical()) {
                        link.reserveLambda(lambda, requiredBandwidth);
                    }
                }
                return lambda;
            }
        }

        // FASE 2: Asignar una nueva lambda si las activas no disponen de capacidad suficiente
        for (int lambda = 0; lambda < numLambdas; lambda++) {
            boolean canFitOnAllLinks = true;

            for (Link link : route.getLinks()) {
                if (link.isOptical()) {
                    if (!link.isLambdaAvailable(lambda, requiredBandwidth)) {
                        canFitOnAllLinks = false;
                        break;
                    }
                }
            }

            if (canFitOnAllLinks) {
                for (Link link : route.getLinks()) {
                    if (link.isOptical()) {
                        link.reserveLambda(lambda, requiredBandwidth);
                    }
                }
                return lambda;
            }
        }

        return -1; // Bloqueo RWA: capacidad física agotada en las 40 lambdas
    }

    public static void releaseWavelength(Route route, int lambda, double requiredBandwidth) {
        if (route == null || lambda < 0) return;
        for (Link link : route.getLinks()) {
            if (link.isOptical()) {
                link.releaseLambda(lambda, requiredBandwidth);
            }
        }
    }
}