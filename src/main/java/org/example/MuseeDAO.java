package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MuseeDAO {

    public Carte getRandomCarteByRarete(Rarete rareteCible) {

        String conditionRarete = "";
        switch (rareteCible) {
            case LEGENDAIRE: conditionRarete = "total >= 1000000"; break;
            case EPIQUE: conditionRarete = "total >= 100000 AND total < 1000000"; break;
            case RARE: conditionRarete = "total >= 20000 AND total < 100000"; break;
            case COMMUN: conditionRarete = "total < 20000 OR total IS NULL"; break;
        }

        String sql = "SELECT identifiant, nom_officiel, domaine_thematique, histoire, adresse, ville, interet " +
                "FROM public.musee WHERE " + conditionRarete + " ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return new Carte(
                        rs.getInt("identifiant"),
                        rs.getString("nom_officiel"),
                        rs.getString("ville"),
                        rs.getString("domaine_thematique"),
                        rs.getString("histoire"),
                        rs.getString("adresse"),
                        rs.getString("interet"),
                        rareteCible
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur MuseeDAO : " + e.getMessage());
        }
        return null;
    }

    public Carte getRandomCarteByRegion(String region) {
        String sql = "SELECT identifiant, nom_officiel, domaine_thematique, histoire, adresse, ville, interet, total " +
                "FROM public.musee WHERE region = ? ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, region);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
                    return new Carte(
                            rs.getInt("identifiant"),
                            rs.getString("nom_officiel"),
                            rs.getString("ville"),
                            rs.getString("domaine_thematique"),
                            rs.getString("histoire"),
                            rs.getString("adresse"),
                            rs.getString("interet"),
                            rareteCalculee
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur MuseeDAO (getRandomCarteByRegion) : " + e.getMessage());
        }
        return null;
    }
}