package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection getConnection() {
        try {
            // Use a URL correta para o MySQL
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/pitssada", // Alterado para MySQL
                    "root", // Usuário do MySQL (alterar se necessário)
                    "" // Senha do MySQL (vazia no XAMPP, altere conforme necessário)
            );
        } catch (SQLException ex) {
            System.out.println("Erro de conexão: " + ex.getMessage()); // Log do erro
            return null; // Retorna null se houver falha na conexão
        }
    }
}
