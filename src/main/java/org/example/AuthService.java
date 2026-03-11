package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public void register(String login, String mdp) {
        String sql = "INSERT INTO users (login, mdp) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, mdp);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("L'utilisateur a été inséré avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    public int login(String pseudo, String motDePasseSaisi) {
        String sql = "SELECT id, mdp FROM public.users WHERE login = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pseudo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String mdpEnBase = rs.getString("mdp");
                int userId = rs.getInt("id");

                if (mdpEnBase.equals(motDePasseSaisi)) {
                    System.out.println("Connexion réussie " + pseudo);
                    return userId;
                } else {
                    System.out.println("Mot de passe incorrect.");
                    return -1;
                }
            } else {
                System.out.println("Utilisateur introuvable.");
                return -1;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
            return -1;
        }
    }
}