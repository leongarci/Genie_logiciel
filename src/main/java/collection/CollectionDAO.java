package collection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import auth.DatabaseConfig;
import carte.Carte;
import carte.CartePossedee;
import carte.Rarete;

public class CollectionDAO {

    public void ajouterCartes(int userId, List<Integer> museeIds) {
        String sql = "INSERT INTO public.collection (user_id, musee_id, quantite) "
                + "VALUES (?, ?, 1) "
                + "ON CONFLICT (user_id, musee_id) "
                + "DO UPDATE SET quantite = public.collection.quantite + 1";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int museeId : museeIds) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, museeId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde collection : " + e.getMessage());
        }
    }

    public List<CartePossedee> getCollectionUtilisateur(int userId) {
        List<CartePossedee> maCollection = new ArrayList<>();
        String sql = "SELECT m.identifiant, m.nom_officiel, m.ville, m.domaine_thematique, "
                + "m.histoire, m.adresse, m.interet, m.total, c.quantite "
                + "FROM public.collection c "
                + "JOIN public.musee m ON c.musee_id = m.identifiant "
                + "WHERE c.user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int totalVisiteurs = rs.getInt("total");
                Rarete rareteCalculee;
                if (rs.wasNull() || totalVisiteurs < 20000) {
                    rareteCalculee = Rarete.COMMUN;
                } else if (totalVisiteurs < 100000) {
                    rareteCalculee = Rarete.RARE;
                } else if (totalVisiteurs < 1000000) {
                    rareteCalculee = Rarete.EPIQUE;
                } else {
                    rareteCalculee = Rarete.LEGENDAIRE;
                }
                Carte carte = new Carte(
                        rs.getInt("identifiant"),
                        rs.getString("nom_officiel"),
                        rs.getString("ville"),
                        rs.getString("domaine_thematique"),
                        rs.getString("histoire"),
                        rs.getString("adresse"),
                        rs.getString("interet"),
                        rareteCalculee
                );
                int quantite = rs.getInt("quantite");
                maCollection.add(new CartePossedee(carte, quantite));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans getCollectionUtilisateur : " + e.getMessage());
        }
        return maCollection;
    }
}
