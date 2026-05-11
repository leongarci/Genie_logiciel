package auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe User
 *
 * Représente un utilisateur du système
 */
public class User {

    private int id;
    private String login;

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbBooster(){
        String sql = "SELECT nombre_ouverts FROM public.limite_booster WHERE user_id = ? AND date_ouverture = CURRENT_DATE";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int ouvertsAujourdhui = rs.getInt("nombre_ouverts");
                    return 3-ouvertsAujourdhui;
                }
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur compte booster : " + e.getMessage());
        }
        return 0;
    }

}
