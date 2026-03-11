package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Booster {
    private List<Carte> cartes = new ArrayList<>();
    private List<Integer> idsTires = new ArrayList<>();

    public Booster() {
        String sql = "SELECT identifiant,nom_officiel, domaine_thematique, histoire, adresse, ville, interet " +
                "FROM public.musee " +
                "ORDER BY RANDOM() " +
                "LIMIT 5";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Carte c = new Carte(
                        rs.getString("nom_officiel"),
                        rs.getString("domaine_thematique"),
                        rs.getString("histoire"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("interet")
                );
                idsTires.add(rs.getInt("identifiant"));
                cartes.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void enregistrerPourUtilisateur(int userId) {
        // Requête PostgreSQL "UPSERT" : Si conflit sur la clé primaire, on incrémente la quantité
        String sql = "INSERT INTO public.collection (user_id, musee_id, quantite) " +
                "VALUES (?, ?, 1) " +
                "ON CONFLICT (user_id, musee_id) " +
                "DO UPDATE SET quantite = public.collection.quantite + 1";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Désactiver l'auto-commit pour faire une transaction (plus propre et rapide)
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int museeId : idsTires) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, museeId);
                    pstmt.addBatch(); // On prépare les 5 commandes d'un coup
                }
                pstmt.executeBatch(); // On envoie tout au serveur
                conn.commit();        // On valide la transaction
                System.out.println("Collection mise à jour pour l'utilisateur " + userId);
            } catch (SQLException e) {
                conn.rollback(); // En cas d'erreur, on annule tout
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde collection : " + e.getMessage());
        }
    }

    public List<Carte> getCartes() {
        return cartes;
    }
}