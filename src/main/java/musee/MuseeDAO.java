package musee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import auth.DatabaseConfig;
import carte.Carte;
import carte.Rarete;

/**
 * Classe MuseeDAO
 *
 * Gère les opérations de base de données pour les musées
 */
public class MuseeDAO {

    // Récupère une carte aléatoire par rareté
    public Carte getRandomCarteByRarete(Rarete rareteCible) {

        String conditionRarete = "";
        switch (rareteCible) {
            case LEGENDAIRE:
                conditionRarete = "total >= 1000000";
                break;
            case EPIQUE:
                conditionRarete = "total >= 100000 AND total < 1000000";
                break;
            case RARE:
                conditionRarete = "total >= 20000 AND total < 100000";
                break;
            case COMMUN:
                conditionRarete = "total < 20000 OR total IS NULL";
                break;
        }

        String sql = "SELECT identifiant, nom_officiel, domaine_thematique, histoire, adresse, ville, interet "
                + "FROM public.musee WHERE " + conditionRarete + " ORDER BY RANDOM() LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

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

    public Carte getRandomCarteByRegion(EnumRegion enumRegion) {
        if (enumRegion.isDrom()) {
            Carte c = fetchCarteDrom(enumRegion.getDepartementBDD());
            if (c == null) {
                System.err.println("Aucune carte pour " + enumRegion.getNomAffichage() + ", fallback tous les DROM.");
                c = fetchCarteDrom(null);
            }
            return c;
        }

        String sql = "SELECT identifiant, nom_officiel, domaine_thematique, histoire, adresse, ville, interet, total "
                + "FROM public.musee WHERE region = ? ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, enumRegion.getRegionBDD());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapCarte(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getRandomCarteByRegion : " + e.getMessage());
        }
        return null;
    }

    private Carte fetchCarteDrom(String departement) {
        String sql = "SELECT identifiant, nom_officiel, domaine_thematique, histoire, adresse, ville, interet, total "
                + "FROM public.musee WHERE region = 'DROM'"
                + (departement != null ? " AND departement = ?" : "")
                + " ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (departement != null) pstmt.setString(1, departement);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapCarte(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur fetchCarteDrom : " + e.getMessage());
        }
        return null;
    }

    private Carte mapCarte(ResultSet rs) throws SQLException {
        int total = rs.getInt("total");
        Rarete r;
        if (rs.wasNull() || total < 20_000)  r = Rarete.COMMUN;
        else if (total < 100_000)             r = Rarete.RARE;
        else if (total < 1_000_000)           r = Rarete.EPIQUE;
        else                                  r = Rarete.LEGENDAIRE;
        return new Carte(
                rs.getInt("identifiant"), rs.getString("nom_officiel"), rs.getString("ville"),
                rs.getString("domaine_thematique"), rs.getString("histoire"),
                rs.getString("adresse"), rs.getString("interet"), r
        );
    }

}
