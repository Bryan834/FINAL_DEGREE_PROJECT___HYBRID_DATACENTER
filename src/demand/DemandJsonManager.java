package demand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DemandJsonManager {

    public static void exportToJson(Demand demand, String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(demand, writer);
            System.out.println("[JSON] Demanda exportada a: " + filePath);
        } catch (IOException e) {
            System.err.println("[JSON] Error al exportar JSON: " + e.getMessage());
        }
    }

    public static Demand importFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Demand demand = gson.fromJson(reader, Demand.class);
            if (demand != null) {
                demand.normalize();
            }
            System.out.println("[JSON] Demanda cargada desde: " + filePath);
            return demand;
        } catch (IOException e) {
            System.err.println("[JSON] Error al leer JSON: " + e.getMessage());
            return null;
        }
    }
}