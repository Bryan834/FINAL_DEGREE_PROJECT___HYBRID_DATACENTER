package datacenter;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCenterJsonManager {

    public static DataCenter importFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            TopologyConfig config = gson.fromJson(reader, TopologyConfig.class);
            if (config == null) {
                System.err.println("[JSON] El archivo de topología está vacío: " + filePath);
                return null;
            }

            DataCenter dc = new DataCenter();
            Map<String, Node> nodesById = new HashMap<>();

            // 1. Instanciar servidores con su capacidad y dominio de fallo
            if (config.servers != null) {
                for (ServerConfig sCfg : config.servers) {
                    Server server = new Server(sCfg.id, sCfg.failureDomain);
                    server.setCapacity(sCfg.xpu, sCfg.ram, sCfg.disk);
                    dc.addServer(server);
                    nodesById.put(server.getId(), server);
                }
            }

            // 2. Instanciar switches genéricos (eléctricos u ópticos)
            if (config.switches != null) {
                for (SwitchConfig swCfg : config.switches) {
                    Switch sw = new Switch(swCfg.id);
                    dc.addSwitch(sw);
                    nodesById.put(sw.getId(), sw);
                }
            }

            // 3. Instanciar enlaces resolviendo referencias por ID
            if (config.links != null) {
                for (LinkConfig lCfg : config.links) {
                    Node src = nodesById.get(lCfg.source);
                    Node dst = nodesById.get(lCfg.target);

                    if (src == null || dst == null) {
                        System.err.println("[JSON] Enlace ignorado (" + lCfg.id + "): nodo origen o destino no encontrado.");
                        continue;
                    }

                    if ("OPTICAL".equalsIgnoreCase(lCfg.type)) {
                        dc.addLink(new Link(lCfg.id, src, dst, lCfg.lambdas, lCfg.capacityPerLambda));
                    } else {
                        dc.addLink(new Link(lCfg.id, src, dst, lCfg.bandwidth));
                    }
                }
            }

            System.out.println("[JSON] Topología cargada desde: " + filePath);
            return dc;

        } catch (IOException e) {
            System.err.println("[JSON] Error al leer la topología desde " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    // --- Clases DTO internas para deserialización directa del JSON ---
    private static class TopologyConfig {
        List<ServerConfig> servers;
        List<SwitchConfig> switches;
        List<LinkConfig> links;
    }

    private static class ServerConfig {
        String id;
        String failureDomain;
        int xpu;
        int ram;
        int disk;
    }

    private static class SwitchConfig {
        String id;
    }

    private static class LinkConfig {
        String id;
        String source;
        String target;
        String type;              // "ELECTRICAL" u "OPTICAL"
        double bandwidth;         // Para enlaces eléctricos
        int lambdas;              // Para enlaces ópticos WDM
        double capacityPerLambda; // Capacidad de cada lambda (Gbps)
    }
}