package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import musee.RegionStats;

public class TestRegionStats {

    @Test
    public void testRegionStatsConstruction() {
        RegionStats stats = new RegionStats("Île-de-France", 12300000, 45000.0,
                200, 50000000, 30000000, 20000000, 5000000, 3000000);

        assertNotNull(stats);
        assertEquals("Île-de-France", stats.getNomRegion());
    }

    @Test
    public void testGettersData() {
        RegionStats stats = new RegionStats("Île-de-France", 12300000, 45000.0,
                200, 50000000, 30000000, 20000000, 5000000, 3000000);

        assertEquals("Île-de-France", stats.getNomRegion());
        assertEquals(12300000, stats.getPopulation());
        assertEquals(45000.0, stats.getRevenuFiscalMedian());
        assertEquals(45000.0, stats.getPibParHabitant());
        assertEquals(200, stats.getNombreTotalMusees());
        assertEquals(50000000L, stats.getTotalEntrees());
        assertEquals(30000000L, stats.getEntreesPayantes());
        assertEquals(20000000L, stats.getEntreesGratuites());
        assertEquals(5000000L, stats.getEntreesJeunes());
        assertEquals(3000000L, stats.getEntreesScolaires());
    }

    @Test
    public void testTauxGratuite() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 10, 5);

        assertEquals(0.4, stats.getTauxGratuite(), 0.0001);
    }

    @Test
    public void testEntreesParHabitant() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 10, 5);

        assertEquals(0.1, stats.getEntreesParHabitant(), 0.0001);
    }

    @Test
    public void testEntreesParMusee() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 10, 5);

        assertEquals(10.0, stats.getEntreesParMusee(), 0.0001);
    }

    @Test
    public void testTauxJeunes() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 20, 5);

        assertEquals(0.25, stats.getTauxJeunes(), 0.0001);
    }

    @Test
    public void testScoreAccessibilite() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 100, 60, 40, 10, 5);

        double expectedScore = 0.4 * Math.log1p(10.0);
        assertEquals(expectedScore, stats.getScoreAccessibilite(), 0.0001);
    }

    @Test
    public void testDivisionParZeroTauxGratuite() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                10, 0, 0, 0, 0, 0);

        assertEquals(0.0, stats.getTauxGratuite(), 0.0001);
    }

    @Test
    public void testDivisionParZeroEntreesParHabitant() {
        RegionStats stats = new RegionStats("Test", 0, 100.0,
                10, 100, 60, 40, 10, 5);

        assertEquals(0.0, stats.getEntreesParHabitant(), 0.0001);
    }

    @Test
    public void testDivisionParZeroEntreesParMusee() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                0, 100, 60, 40, 10, 5);

        assertEquals(0.0, stats.getEntreesParMusee(), 0.0001);
    }

    @Test
    public void testScoreAccessibiliteAvecZeroMusees() {
        RegionStats stats = new RegionStats("Test", 1000, 100.0,
                0, 100, 60, 40, 10, 5);

        assertEquals(0.0, stats.getScoreAccessibilite(), 0.0001);
    }

    @Test
    public void testGrandesValeurs() {
        RegionStats stats = new RegionStats("Île-de-France", 12000000, 50000.0,
                250, 100000000, 50000000, 50000000, 10000000, 8000000);

        assertEquals(12000000, stats.getPopulation());
        assertEquals(250, stats.getNombreTotalMusees());
        assertEquals(100000000L, stats.getTotalEntrees());
        assertEquals(0.5, stats.getTauxGratuite(), 0.0001);
        assertEquals(100000000.0 / 12000000.0, stats.getEntreesParHabitant(), 0.0001);
        assertEquals(100000000.0 / 250.0, stats.getEntreesParMusee(), 0.0001);
    }
}
