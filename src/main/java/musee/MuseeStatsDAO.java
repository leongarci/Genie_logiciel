package musee;

import auth.DatabaseConfig;
import carte.Carte;
import carte.Rarete;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class MuseeStatsDAO {

    /**
     * Stats agrégées par région (entrées + démographie).
     * Jointure sur newreg_l pour récupérer richesse et population
     * directement depuis la table region.
     */
    public List<RegionStats> getRegionStats() {
        String sql = """
            SELECT
                m.region,
                COALESCE(r.total_general, 0)           AS population,
                COALESCE(r.richesse, 0)                AS revenu,
                COUNT(*)                               AS nb_musees,
                COALESCE(SUM(m.total),   0)            AS total_entrees,
                COALESCE(SUM(m.payant),  0)            AS entrees_payantes,
                COALESCE(SUM(m.gratuit), 0)            AS entrees_gratuites,
                COALESCE(SUM(m.moins_18_ans_hors_scolaires), 0)
                    + COALESCE(SUM(m._18_25_ans), 0)   AS entrees_jeunes,
                COALESCE(SUM(m.scolaires), 0)          AS entrees_scolaires
            FROM public.musee m
            LEFT JOIN public.region r ON r.newreg_l = m.region
            GROUP BY m.region, r.total_general, r.richesse
            ORDER BY total_entrees DESC
            """;

        List<RegionStats> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new RegionStats(
                        rs.getString("region"),
                        rs.getInt("population"),
                        rs.getDouble("revenu"),
                        rs.getInt("nb_musees"),
                        rs.getLong("total_entrees"),
                        rs.getLong("entrees_payantes"),
                        rs.getLong("entrees_gratuites"),
                        rs.getLong("entrees_jeunes"),
                        rs.getLong("entrees_scolaires")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getRegionStats : " + e.getMessage());
        }
        return result;
    }

    /**
     * Top N musées par nombre d'entrées total (national).
     */
    public List<Carte> getTopMuseesByValeur(int topN) {
        String sql = """
            SELECT identifiant, nom_officiel, ville, region, departement,
                   domaine_thematique, histoire, atout, interet, adresse,
                   code_postal, annee_creation, idmusofile,
                   payant, gratuit, total, individuel, scolaires,
                   groupes_hors_scolaires, moins_18_ans_hors_scolaires, _18_25_ans
            FROM public.musee
            WHERE total > 0
            ORDER BY total DESC
            LIMIT ?
            """;
        return fetchCartes(sql, topN);
    }

    /**
     * Top N musées d'une région triés par entrées.
     */
    public List<Carte> getTopMuseesByRegion(String region, int topN) {
        String sql = """
            SELECT identifiant, nom_officiel, ville, region, departement,
                   domaine_thematique, histoire, atout, interet, adresse,
                   code_postal, annee_creation, idmusofile,
                   payant, gratuit, total, individuel, scolaires,
                   groupes_hors_scolaires, moins_18_ans_hors_scolaires, _18_25_ans
            FROM public.musee
            WHERE region = ? AND total > 0
            ORDER BY total DESC
            LIMIT ?
            """;

        List<Carte> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, region);
            ps.setInt(2, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapCarte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getTopMuseesByRegion : " + e.getMessage());
        }
        return result;
    }


    /**
     * Corrélation richesse / taux de gratuité.
     * "Les régions les plus riches sont-elles celles
     *  où la part d'entrées gratuites est la plus élevée ?"
     *
     * @return map région → [richesse, tauxGratuite]
     */
    public Map<String, double[]> correlationRichesseTauxGratuite() {
        Map<String, double[]> result = new LinkedHashMap<>();
        for (RegionStats r : getRegionStats()) {
            if (r.getRevenuFiscalMedian() > 0) {
                result.put(r.getNomRegion(), new double[]{
                        r.getRevenuFiscalMedian(),
                        r.getTauxGratuite()
                });
            }
        }
        return result;
    }

    /**
     * Corrélation richesse / entrées par habitant.
     * "Les habitants des régions riches vont-ils davantage au musée ?"
     *
     * @return map région → [richesse, entreesParHabitant]
     */
    public Map<String, double[]> correlationRichesseEntreesParHabitant() {
        Map<String, double[]> result = new LinkedHashMap<>();
        for (RegionStats r : getRegionStats()) {
            if (r.getRevenuFiscalMedian() > 0 && r.getPopulation() > 0) {
                result.put(r.getNomRegion(), new double[]{
                        r.getRevenuFiscalMedian(),
                        r.getEntreesParHabitant()
                });
            }
        }
        return result;
    }

    /**
     * Top N musées accessibles aux jeunes, ajusté par richesse régionale.
     * "Quels musées attirent le plus de jeunes même dans des régions à faible revenu ?"
     * Score = tauxJeunes × (29 000 / richesseRégion)
     * Valeur > 1 = musée atypiquement accessible aux jeunes vs. son contexte régional.
     *
     * @return liste de paires (Carte, score) triée par score décroissant
     */
    public List<Map.Entry<Carte, Double>> topMuseesAccessiblesJeunes(int topN) {
        Map<String, Double> revenuParRegion = getRegionStats().stream()
                .collect(Collectors.toMap(RegionStats::getNomRegion,
                                          RegionStats::getRevenuFiscalMedian,
                                          (a, b) -> a));

        String sql = """
            SELECT identifiant, nom_officiel, ville, region, departement,
                   domaine_thematique, histoire, atout, interet, adresse,
                   code_postal, annee_creation, idmusofile,
                   payant, gratuit, total, individuel, scolaires,
                   groupes_hors_scolaires, moins_18_ans_hors_scolaires, _18_25_ans
            FROM public.musee
            WHERE total > 0
            """;

        List<Map.Entry<Carte, Double>> scored = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Carte carte = mapCarte(rs);
                long jeunes = safeInt(carte.getMoins18AnsHorsScolaires())
                            + safeInt(carte.getDe18A25Ans())
                            + safeInt(carte.getScolaires());
                long total  = safeInt(carte.getTotal());
                if (total == 0) continue;

                double tauxJeunes = (double) jeunes / total;
                double revenu     = revenuParRegion.getOrDefault(carte.getRegion(), 29_000.0);
                double score      = revenu > 0 ? tauxJeunes * (29_000.0 / revenu) : 0;
                scored.add(Map.entry(carte, score));
            }
        } catch (SQLException e) {
            System.err.println("Erreur topMuseesAccessiblesJeunes : " + e.getMessage());
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Diversité culturelle (indice de Shannon normalisé) par région.
     * "Les régions riches ont-elles une offre muséale plus diversifiée ?"
     * H normalisé ∈ [0, 1] — 1 = tous les thèmes également représentés.
     *
     * @return map région → indice de Shannon normalisé
     */
    public Map<String, Double> diversiteCulturelleParRegion() {
        String sql = """
            SELECT region, domaine_thematique, COUNT(*) AS nb
            FROM public.musee
            WHERE domaine_thematique IS NOT NULL
            GROUP BY region, domaine_thematique
            """;

        Map<String, Map<String, Integer>> regionThemes = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                regionThemes
                    .computeIfAbsent(rs.getString("region"), k -> new HashMap<>())
                    .put(rs.getString("domaine_thematique"), rs.getInt("nb"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur diversiteCulturelle : " + e.getMessage());
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : regionThemes.entrySet()) {
            Map<String, Integer> themes = entry.getValue();
            double total = themes.values().stream().mapToInt(Integer::intValue).sum();
            double shannon = themes.values().stream()
                    .mapToDouble(n -> { double p = n / total; return -p * Math.log(p); })
                    .sum();
            double maxShannon = Math.log(themes.size());
            result.put(entry.getKey(), maxShannon > 0
                    ? Math.round(shannon / maxShannon * 10000) / 10000.0 : 0.0);
        }
        return result;
    }

    /**
     * Indice de Gini des entrées intra-région.
     * "Dans les régions riches, les entrées sont-elles concentrées
     *  sur 1-2 musées phares ou bien réparties équitablement ?"
     * Gini = 0 : égalité parfaite. Gini = 1 : un musée capte tout.
     *
     * @return map région → indice de Gini
     */
    public Map<String, Double> giniEntreesParRegion() {
        String sql = "SELECT region, COALESCE(total,0) AS total " +
                     "FROM public.musee WHERE total > 0 ORDER BY region, total";

        Map<String, List<Long>> regionEntrees = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                regionEntrees
                    .computeIfAbsent(rs.getString("region"), k -> new ArrayList<>())
                    .add(rs.getLong("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur giniEntrees : " + e.getMessage());
        }

        Map<String, Double> result = new LinkedHashMap<>();
        regionEntrees.forEach((region, vals) -> result.put(region, computeGini(vals)));
        return result;
    }

    /**
     * Effet de levier touristique par région.
     * "Certaines régions moins riches génèrent-elles autant de visites
     *  par habitant qu'une région riche ?"
     * Ratio = (entrées/habitant) / (richesse / 29 000)
     * Valeur > 1 = sur-performance touristique vs. niveau de vie.
     *
     * @return map région → ratio de levier touristique
     */
    public Map<String, Double> effetLevierTouristique() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (RegionStats r : getRegionStats()) {
            if (r.getRevenuFiscalMedian() > 0 && r.getPopulation() > 0
                    && r.getTotalEntrees() > 0) {
                double levier = r.getEntreesParHabitant()
                                / (r.getRevenuFiscalMedian() / 29_000.0);
                result.put(r.getNomRegion(), Math.round(levier * 1000) / 1000.0);
            }
        }
        return result;
    }

    /**
     * Musées avec score de rareté thématique.
     * "Ce musée est-il le seul de son thème dans sa région ?"
     * rarete = 1 - (nb musées même thème région / nb musées région)
     * Un musée COMMUN en entrées mais unique thématiquement est upgradé à RARE.
     *
     * @return liste de Carte avec Rarete éventuellement upgradée
     */
    public List<Carte> getMuseesAvecRareteThematique() {
        Map<String, Double> rareteMap = computeRareteThematique();

        String sql = """
            SELECT identifiant, nom_officiel, ville, region, departement,
                   domaine_thematique, histoire, atout, interet, adresse,
                   code_postal, annee_creation, idmusofile,
                   payant, gratuit, total, individuel, scolaires,
                   groupes_hors_scolaires, moins_18_ans_hors_scolaires, _18_25_ans
            FROM public.musee
            WHERE total > 0
            ORDER BY total DESC
            """;

        List<Carte> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Carte carte = mapCarte(rs);
                String key = carte.getRegion() + "|" + carte.getDomaineThematique();
                double rareteScore = rareteMap.getOrDefault(key, 0.0);
                if (rareteScore > 0.9 && carte.getRarete() == Rarete.COMMUN) {
                    carte.setRarete(Rarete.RARE);
                }
                result.add(carte);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMuseesAvecRareteThematique : " + e.getMessage());
        }
        return result;
    }

    /**
     * Corrélation population jeune / entrées scolaires par région.
     * "Les régions avec une population jeune plus importante
     *  ont-elles proportionnellement plus d'entrées scolaires ?"
     * Exploite les colonnes age_* de la table region.
     *
     * @return map région → [tauxPopJeune (0-24 ans / total), tauxEntreesScolaires]
     */
    public Map<String, double[]> correlationPopulationJeuneEntreesScolaires() {
        String sql = """
            SELECT
                m.region,
                COALESCE(r.age_0_4  + r.age_5_9  + r.age_10_14
                       + r.age_15_19 + r.age_20_24, 0)   AS pop_jeune,
                COALESCE(r.total_general, 0)              AS pop_totale,
                COALESCE(SUM(m.scolaires), 0)             AS entrees_scolaires,
                COALESCE(SUM(m.total),     0)             AS total_entrees
            FROM public.musee m
            LEFT JOIN public.region r ON r.newreg_l = m.region
            GROUP BY m.region, r.age_0_4, r.age_5_9, r.age_10_14,
                     r.age_15_19, r.age_20_24, r.total_general
            HAVING SUM(m.total) > 0
            """;

        Map<String, double[]> result = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long popJeune  = rs.getLong("pop_jeune");
                long popTotale = rs.getLong("pop_totale");
                long scolaires = rs.getLong("entrees_scolaires");
                long total     = rs.getLong("total_entrees");

                double tauxPopJeune  = popTotale  > 0 ? (double) popJeune  / popTotale : 0;
                double tauxScolaires = total      > 0 ? (double) scolaires / total      : 0;

                result.put(rs.getString("region"), new double[]{tauxPopJeune, tauxScolaires});
            }
        } catch (SQLException e) {
            System.err.println("Erreur correlationPopulationJeune : " + e.getMessage());
        }
        return result;
    }

    private Carte mapCarte(ResultSet rs) throws SQLException {
        int total = rs.getInt("total");

        Rarete rarete;
        if (rs.wasNull() || total < 20_000)  rarete = Rarete.COMMUN;
        else if (total < 100_000)             rarete = Rarete.RARE;
        else if (total < 1_000_000)           rarete = Rarete.EPIQUE;
        else                                  rarete = Rarete.LEGENDAIRE;

        Carte c = new Carte(
                rs.getInt("identifiant"),
                rs.getString("nom_officiel"),
                rs.getString("ville"),
                rs.getString("domaine_thematique"),
                rs.getString("histoire"),
                rs.getString("atout"),
                rs.getString("interet"),
                rarete
        );
        c.setRegion(rs.getString("region"));
        c.setDepartement(rs.getString("departement"));
        c.setAdresse(rs.getString("adresse"));
        c.setCodePostal(rs.getString("code_postal"));
        c.setIdmusofile(rs.getString("idmusofile"));
        c.setPayant(rs.getInt("payant"));
        c.setGratuit(rs.getInt("gratuit"));
        c.setTotal(total);
        c.setIndividuel(rs.getInt("individuel"));
        c.setScolaires(rs.getInt("scolaires"));
        c.setGroupesHorsScolaires(rs.getInt("groupes_hors_scolaires"));
        c.setMoins18AnsHorsScolaires(rs.getInt("moins_18_ans_hors_scolaires"));
        c.setDe18A25Ans(rs.getInt("_18_25_ans"));
        try { c.setAnneeCreation(rs.getInt("annee_creation")); } catch (Exception ignored) {}
        return c;
    }

    private List<Carte> fetchCartes(String sql, int limit) {
        List<Carte> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapCarte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur fetchCartes : " + e.getMessage());
        }
        return result;
    }

    private Map<String, Double> computeRareteThematique() {
        String sql = "SELECT region, domaine_thematique, COUNT(*) AS nb " +
                     "FROM public.musee WHERE domaine_thematique IS NOT NULL " +
                     "GROUP BY region, domaine_thematique";

        Map<String, Integer> themeCount  = new HashMap<>();
        Map<String, Integer> regionCount = new HashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String region = rs.getString("region");
                String theme  = rs.getString("domaine_thematique");
                int    nb     = rs.getInt("nb");
                themeCount.put(region + "|" + theme, nb);
                regionCount.merge(region, nb, Integer::sum);
            }
        } catch (SQLException e) {
            System.err.println("Erreur computeRarete : " + e.getMessage());
        }

        Map<String, Double> rareteMap = new HashMap<>();
        themeCount.forEach((key, nbTheme) -> {
            String region = key.split("\\|")[0];
            int    total  = regionCount.getOrDefault(region, 1);
            rareteMap.put(key, Math.round((1.0 - (double) nbTheme / total) * 10000) / 10000.0);
        });
        return rareteMap;
    }

    private static double computeGini(List<Long> values) {
        if (values.size() <= 1) return 0.0;
        long[] sorted = values.stream().mapToLong(Long::longValue).sorted().toArray();
        int  n = sorted.length;
        long sum = 0, weightedSum = 0;
        for (int i = 0; i < n; i++) {
            sum         += sorted[i];
            weightedSum += (long)(i + 1) * sorted[i];
        }
        if (sum == 0) return 0.0;
        return Math.round(((2.0 * weightedSum) / (n * sum) - (double)(n + 1) / n) * 10000) / 10000.0;
    }

    private static int safeInt(Integer v) { return v == null ? 0 : v; }
}
