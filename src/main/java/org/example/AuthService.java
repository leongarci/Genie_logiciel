package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AuthService {

    public User inscrireUtilisateur(String login, String motDePasseClair) {
        String mdpHache = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt());

        String sql = "INSERT INTO public.users (login, mdp) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, login);
            pstmt.setString(2, mdpHache);

            int lignesAffectees = pstmt.executeUpdate();

            if (lignesAffectees > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int nouvelId = rs.getInt(1);
                        System.out.println("Inscription réussie " + login);
                        return new User(nouvelId, login);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Ce pseudo est déjà pris.");
            } else {
                System.err.println("Erreur lors de l'inscription : " + e.getMessage());
            }
        }

        return null;
    }

    public User login(String pseudo, String motDePasseSaisi) {
        String sql = "SELECT id, mdp FROM public.users WHERE login = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pseudo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String mdpHacheEnBase = rs.getString("mdp");
                int userId = rs.getInt("id");

                if (BCrypt.checkpw(motDePasseSaisi, mdpHacheEnBase)) {
                    System.out.println("Connexion réussie");
                    return new User(userId, pseudo);
                } else {
                    System.out.println("Mot de passe incorrect.");
                }
            } else {
                System.out.println("Utilisateur introuvable.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }

        return null;
    }
}