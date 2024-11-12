package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/pitssada",
                    "root", ""
            );
        } catch (SQLException ex) {
            System.out.println("Erro de conexão");
            return null; // Retorna null se houver falha na conexão
        }
    }
}
