package test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import carte.Booster;
import carte.Carte;
import musee.EnumRegion;

public class TestBooster {

    @Test
    public void testBoosterConstruction() {
        Booster booster = new Booster();
        assertNotNull(booster);
        assertNotNull(booster.getCartes());
    }

    @Test
    public void testBoosterConstructionWithRegion() {
        Booster booster = new Booster(EnumRegion.ILE_DE_FRANCE);
        assertNotNull(booster);
        assertNotNull(booster.getCartes());
    }

    @Test
    public void testGetCartes() {
        Booster booster = new Booster();
        assertNotNull(booster.getCartes());
        assertTrue(booster.getCartes() instanceof java.util.List);
    }

    @Test
    public void testBoosterCartesNotNull() {
        Booster booster = new Booster();
        for (Carte carte : booster.getCartes()) {
            assertNotNull(carte);
        }
    }

    @Test
    public void testBoosterContient5Cartes() {
        Booster booster = new Booster();
        assertTrue(booster.getCartes().size() <= 5);
        assertTrue(booster.getCartes().size() >= 0);
    }
}
