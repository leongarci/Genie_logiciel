package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionService {

    public List<CartePossedee> getCollectionUtilisateur(int userId) {
        List<CartePossedee> maCollection = new ArrayList<>();

        String sql = "SELECT m.nom_officiel, m.ville, m.domaine_thematique, c.quantite " +
                "FROM public.collection c " +
                "JOIN public.musee m ON c.musee_id = m.identifiant " +
                "WHERE c.user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Carte carte = new Carte(
                        rs.getString("nom_officiel"),
                        rs.getString("domaine_thematique"),
                        "", "", rs.getString("ville"), ""
                );

                int quantite = rs.getInt("quantite");
                maCollection.add(new CartePossedee(carte, quantite));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maCollection;
    }
}