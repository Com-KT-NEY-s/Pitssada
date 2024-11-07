package Threading;

import static DB.Database.getConnection;
import Inicio.home;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Threading {

    private Connection connection = null;
    private ScheduledExecutorService connectionChecker;
    
    // Inicia o ScheduledExecutorService para checar a conexão do banco em background
    public void startDatabaseConnectionChecker() {
        connectionChecker = Executors.newScheduledThreadPool(1);
        connectionChecker.scheduleAtFixedRate(() -> {
            Thread.currentThread().setName("Timer");
            try {
                checkDatabaseConnection();
            } catch (Exception ex) {
                System.err.println("Erro na verificação de conexão: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void checkDatabaseConnection() {
        home h = new home();
        try {
            connection = getConnection();
            h.updateConnectionStatusPanel(connection != null && !connection.isClosed());
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException | NullPointerException ex) {
            System.err.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
            h.updateConnectionStatusPanel(false);
        }
    }

}
