package carte;

import auth.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoosterDAO {

    // Vérifie si le joueur a ouvert moins de 3 boosters aujourd'hui
    public boolean peutOuvrirBooster(int userId) {
        String sql = "SELECT nombre_ouverts FROM public.limite_booster WHERE user_id = ? AND date_ouverture = CURRENT_DATE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int ouvertsAujourdhui = rs.getInt("nombre_ouverts");
                    return ouvertsAujourdhui < 3; // Renvoie vrai si strictement inférieur à 3
                }
                // Si aucune ligne n'est trouvée pour aujourd'hui, c'est qu'il en a ouvert 0
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification limite booster : " + e.getMessage());
        }
        return false; // Par sécurité, on bloque en cas d'erreur BDD
    }

    // Incrémente le compteur d'ouverture pour la journée
    public void enregistrerOuverture(int userId) {
        // ON CONFLICT met à jour la ligne si elle existe déjà pour aujourd'hui, sinon elle l'insère
        String sql = "INSERT INTO public.limite_booster (user_id, date_ouverture, nombre_ouverts) " +
                "VALUES (?, CURRENT_DATE, 1) " +
                "ON CONFLICT (user_id, date_ouverture) " +
                "DO UPDATE SET nombre_ouverts = public.limite_booster.nombre_ouverts + 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur enregistrement ouverture booster : " + e.getMessage());
        }
    }
}