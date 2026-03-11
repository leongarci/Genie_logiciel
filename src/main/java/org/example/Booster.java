package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Booster {
    private User user;
    private List<Carte> cartes = new ArrayList<>();
    private List<Integer> idsTires = new ArrayList<>();

    public Booster(User user) {
        this.user=user;
        genererCartes();
    }

    private void genererCartes() {
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

    public void enregistrerPourUtilisateur(User joueur) {
        if (joueur == null) {
            System.err.println("Erreur : Impossible d'enregistrer le booster, aucun joueur n'est connecté.");
            return;
        }

        String sql = "INSERT INTO public.collection (user_id, musee_id, quantite) " +
                "VALUES (?, ?, 1) " +
                "ON CONFLICT (user_id, musee_id) " +
                "DO UPDATE SET quantite = public.collection.quantite + 1";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (int museeId : idsTires) {
                    pstmt.setInt(1, joueur.getId());
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
            System.err.println("Erreur lors de la sauvegarde de la collection : " + e.getMessage());
        }
    }

    public List<Carte> getCartes() {
        return cartes;
    }
}