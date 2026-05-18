// TestRegionStats.java
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import musee.RegionStats;

public class TestRegionStats {

    private RegionStats statsStandard;
    private RegionStats statsZero;
    private RegionStats statsGrandes;

    @BeforeEach
    void setUp() {
        statsStandard = new RegionStats("Île-de-France", 12300000, 45000.0,
                200, 50000000, 30000000, 20000000, 5000000, 3000000);
        statsZero = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 10, 5);
        statsGrandes = new RegionStats("Île-de-France", 12000000, 50000.0,
                250, 100000000, 50000000, 50000000, 10000000, 8000000);
    }

    @Test
    @DisplayName("Constructeur initialise correctement RegionStats")
    public void testRegionStatsConstruction() {
        assertNotNull(statsStandard);
        assertEquals("Île-de-France", statsStandard.getNomRegion(), "Le nom de la région devrait être Île-de-France");
    }

    @Test
    @DisplayName("Tous les getters retournent les bonnes valeurs")
    public void testGettersData() {
        assertEquals("Île-de-France", statsStandard.getNomRegion());
        assertEquals(12300000, statsStandard.getPopulation());
        assertEquals(45000.0, statsStandard.getRevenuFiscalMedian());
        assertEquals(45000.0, statsStandard.getPibParHabitant());
        assertEquals(200, statsStandard.getNombreTotalMusees());
        assertEquals(50000000L, statsStandard.getTotalEntrees());
        assertEquals(30000000L, statsStandard.getEntreesPayantes());
        assertEquals(20000000L, statsStandard.getEntreesGratuites());
        assertEquals(5000000L, statsStandard.getEntreesJeunes());
        assertEquals(3000000L, statsStandard.getEntreesScolaires());
    }

    @Test
    @DisplayName("getTauxGratuite() calcule correctement le taux")
    public void testTauxGratuite() {
        assertEquals(0.4, statsZero.getTauxGratuite(), 0.0001, "Le taux de gratuité devrait être 0.4 (40%)");
    }

    @Test
    @DisplayName("getEntreesParHabitant() calcule correctement")
    public void testEntreesParHabitant() {
        assertEquals(0.1, statsZero.getEntreesParHabitant(), 0.0001, "Les entrées par habitant devraient être 0.1");
    }

    @Test
    @DisplayName("getEntreesParMusee() calcule correctement")
    public void testEntreesParMusee() {
        assertEquals(10.0, statsZero.getEntreesParMusee(), 0.0001, "Les entrées par musée devraient être 10");
    }

    @Test
    @DisplayName("getTauxJeunes() calcule correctement le pourcentage de jeunes")
    public void testTauxJeunes() {
        RegionStats statsJeunes = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 20, 5);
        assertEquals(0.25, statsJeunes.getTauxJeunes(), 0.0001, "Le taux de jeunes devrait être 0.25 (25%)");
    }

    @Test
    @DisplayName("getScoreAccessibilite() calcule correctement le score")
    public void testScoreAccessibilite() {
        double expectedScore = 0.4 * Math.log1p(10.0);
        assertEquals(expectedScore, statsZero.getScoreAccessibilite(), 0.0001, "Le score d'accessibilité ne correspond pas");
    }

    @Test
    @DisplayName("getTauxGratuite() retourne 0 si totalEntrees est zéro")
    public void testDivisionParZeroTauxGratuite() {
        RegionStats statsDivZero = new RegionStats("Test", 1000, 100.0,
                10, 0, 0, 0, 0, 0);
        assertEquals(0.0, statsDivZero.getTauxGratuite(), 0.0001, "Le taux devrait être 0 pour éviter la division par zéro");
    }

    @Test
    @DisplayName("getEntreesParHabitant() retourne 0 si population est zéro")
    public void testDivisionParZeroEntreesParHabitant() {
        RegionStats statsDivZero = new RegionStats("Test", 0, 100.0,
                10, 100, 60, 40, 10, 5);
        assertEquals(0.0, statsDivZero.getEntreesParHabitant(), 0.0001, "Le résultat devrait être 0 pour éviter la division par zéro");
    }

    @Test
    @DisplayName("getEntreesParMusee() retourne 0 si nombreMusees est zéro")
    public void testDivisionParZeroEntreesParMusee() {
        RegionStats statsDivZero = new RegionStats("Test", 1000, 100.0,
                0, 100, 60, 40, 10, 5);
        assertEquals(0.0, statsDivZero.getEntreesParMusee(), 0.0001, "Le résultat devrait être 0 pour éviter la division par zéro");
    }

    @Test
    @DisplayName("getScoreAccessibilite() retourne 0 si nombreMusees est zéro")
    public void testScoreAccessibiliteAvecZeroMusees() {
        RegionStats statsDivZero = new RegionStats("Test", 1000, 100.0,
                0, 100, 60, 40, 10, 5);
        assertEquals(0.0, statsDivZero.getScoreAccessibilite(), 0.0001, "Le score devrait être 0 avec zéro musée");
    }

    @Test
    @DisplayName("Tous les calculs fonctionnent correctement avec de grandes valeurs")
    public void testGrandesValeurs() {
        assertEquals(12000000, statsGrandes.getPopulation());
        assertEquals(250, statsGrandes.getNombreTotalMusees());
        assertEquals(100000000L, statsGrandes.getTotalEntrees());
        assertEquals(0.5, statsGrandes.getTauxGratuite(), 0.0001, "Taux de gratuité = 50M/100M");
        assertEquals(100000000.0 / 12000000.0, statsGrandes.getEntreesParHabitant(), 0.0001, "Entrées par habitant incorrect");
        assertEquals(100000000.0 / 250.0, statsGrandes.getEntreesParMusee(), 0.0001, "Entrées par musée incorrect");
    }
}
