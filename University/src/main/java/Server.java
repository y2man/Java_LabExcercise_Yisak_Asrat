import java.sql.Connection;
import java.sql.DriverManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/university",
                    "root",
                    ""
            );
            return con;
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e);
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            UniversityService service = new UniversityServiceImpl();
            registry.rebind("UniversityService", service);
            System.out.println("University RMI service bound on port 1099 as 'UniversityService'.");
        } catch (Exception ex) {
            System.out.println("Failed to start RMI server: " + ex);
        }
    }
}
