
import auth.AuthService;
import auth.User;
import carte.Booster;
import musee.EnumRegion;

import java.util.List;
import java.util.Map;

import musee.MuseeStatsDAO;
import musee.RegionStats;
import carte.Carte;
import carte.Rarete;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        AuthService authService = new AuthService();
        User joueur = authService.login("leon", "mdp123");
        Booster booster = new Booster(EnumRegion.ILE_DE_FRANCE);
        booster.ouvrirBooster(joueur);

        // ══════════════════════════════════════════════════════════════
// TEST MuseeStatsDAO — à coller dans votre main() temporairement
// ══════════════════════════════════════════════════════════════

        MuseeStatsDAO statsDAO = new MuseeStatsDAO();

        System.out.println("\n========== STATS SIMPLES ==========");

// --- Stats par région ---
        System.out.println("\n[1] Stats par région :");
        List<RegionStats> regions = statsDAO.getRegionStats();
        for (RegionStats r : regions) {
            System.out.printf("  %-30s | musées: %3d | entrées: %,10d | richesse: %,8.0f | taux gratuit: %.1f%%%n",
                    r.getNomRegion(), r.getNombreTotalMusees(),
                    r.getTotalEntrees(), r.getRevenuFiscalMedian(),
                    r.getTauxGratuite() * 100);
        }

// --- Top 5 musées nationaux ---
        System.out.println("\n[2] Top 5 musées nationaux (entrées) :");
        List<Carte> topNational = statsDAO.getTopMuseesByValeur(5);
        for (int i = 0; i < topNational.size(); i++) {
            Carte c = topNational.get(i);
            System.out.printf("  %d. %-40s | %s | %,d entrées | %s%n",
                    i + 1, c.getNomOfficiel(), c.getRegion(),
                    c.getTotal(), c.getRarete());
        }

// --- Top 5 musées d'une région ---
        String regionTest = regions.isEmpty() ? "Île-de-France" : regions.get(0).getNomRegion();
        System.out.println("\n[3] Top 5 musées de la région : " + regionTest);
        List<Carte> topRegion = statsDAO.getTopMuseesByRegion(regionTest, 5);
        for (int i = 0; i < topRegion.size(); i++) {
            Carte c = topRegion.get(i);
            System.out.printf("  %d. %-40s | %,d entrées%n",
                    i + 1, c.getNomOfficiel(), c.getTotal());
        }

        System.out.println("\n========== STATS COMPLEXES ==========");

// --- STAT 1 : Corrélation richesse / taux gratuité ---
        System.out.println("\n[STAT 1] Corrélation richesse → taux gratuité :");
        Map<String, double[]> corr1 = statsDAO.correlationRichesseTauxGratuite();
        corr1.forEach((region, vals) ->
                System.out.printf("  %-30s | richesse: %,8.0f | gratuit: %.1f%%%n",
                        region, vals[0], vals[1] * 100));

// --- STAT 2 : Corrélation richesse / entrées par habitant ---
        System.out.println("\n[STAT 2] Corrélation richesse → entrées/habitant :");
        Map<String, double[]> corr2 = statsDAO.correlationRichesseEntreesParHabitant();
        corr2.forEach((region, vals) ->
                System.out.printf("  %-30s | richesse: %,8.0f | visites/hab: %.4f%n",
                        region, vals[0], vals[1]));

// --- STAT 3 : Top 5 musées accessibles jeunes ---
        System.out.println("\n[STAT 3] Top 5 musées accessibles aux jeunes (score ajusté richesse) :");
        List<Map.Entry<Carte, Double>> topJeunes = statsDAO.topMuseesAccessiblesJeunes(5);
        for (int i = 0; i < topJeunes.size(); i++) {
            Map.Entry<Carte, Double> e = topJeunes.get(i);
            System.out.printf("  %d. %-40s | %s | score: %.3f%n",
                    i + 1, e.getKey().getNomOfficiel(),
                    e.getKey().getRegion(), e.getValue());
        }

// --- STAT 4 : Diversité culturelle (Shannon) ---
        System.out.println("\n[STAT 4] Diversité culturelle par région (indice Shannon 0→1) :");
        Map<String, Double> diversite = statsDAO.diversiteCulturelleParRegion();
        diversite.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.printf("  %-30s | Shannon: %.4f%n", e.getKey(), e.getValue()));

// --- STAT 5 : Gini des entrées ---
        System.out.println("\n[STAT 5] Concentration des entrées par région (Gini 0=égal, 1=monopole) :");
        Map<String, Double> gini = statsDAO.giniEntreesParRegion();
        gini.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.printf("  %-30s | Gini: %.4f%n", e.getKey(), e.getValue()));

// --- STAT 6 : Effet levier touristique ---
        System.out.println("\n[STAT 6] Effet levier touristique (>1 = sur-performance vs. richesse) :");
        Map<String, Double> levier = statsDAO.effetLevierTouristique();
        levier.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.printf("  %-30s | levier: %.3f%s%n",
                        e.getKey(), e.getValue(),
                        e.getValue() > 1.0 ? " ★ sur-performance" : ""));

// --- STAT 7 : Rareté thématique ---
        System.out.println("\n[STAT 7] Musées upgradés RARE par rareté thématique (score > 0.9) :");
        List<Carte> raretes = statsDAO.getMuseesAvecRareteThematique();
        raretes.stream()
                .filter(c -> c.getRarete() == Rarete.RARE && c.getTotal() < 20_000)
                .limit(5)
                .forEach(c -> System.out.printf("  %-40s | %s | thème: %s%n",
                        c.getNomOfficiel(), c.getRegion(), c.getDomaineThematique()));

// --- STAT 8 : Population jeune / entrées scolaires ---
        System.out.println("\n[STAT 8] Corrélation population jeune → entrées scolaires :");
        Map<String, double[]> corr8 = statsDAO.correlationPopulationJeuneEntreesScolaires();
        corr8.forEach((region, vals) ->
                System.out.printf("  %-30s | pop jeune: %.1f%% | taux scolaires: %.1f%%%n",
                        region, vals[0] * 100, vals[1] * 100));

        System.out.println("\n========== FIN DES TESTS ==========");


    }
}
