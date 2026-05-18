package auth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe DatabaseConfig
 *
 * Configuration de la connexion à la base de données
 */
public class DatabaseConfig {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");


    private static final HikariDataSource ds;
    static {
        HikariConfig cfg = new HikariConfig();
        //System.out.println(URL + " / " + USER + " / " + PASSWORD);
        cfg.setJdbcUrl(URL);
        cfg.setUsername(USER);
        cfg.setPassword(PASSWORD);
        cfg.setMaximumPoolSize(10);
        ds = new HikariDataSource(cfg);
    }
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
