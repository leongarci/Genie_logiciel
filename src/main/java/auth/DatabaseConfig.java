package auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe DatabaseConfig
 *
 * Configuration de la connexion à la base de données
 */
public class DatabaseConfig {

    private static final String URL = "jdbc:postgresql://my-astre.com:5433/dbGenie";
    private static final String USER = "userGenie";
    private static final String PASSWORD = "passwordGenie1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
