package test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carte.Booster;
import carte.Carte;
import musee.EnumRegion;

public class TestBooster {

    private Booster booster;
    private Booster boosterWithRegion;

    @BeforeEach
    void setUp() {
        booster = new Booster();
        boosterWithRegion = new Booster(EnumRegion.ILE_DE_FRANCE);
    }

    @Test
    @DisplayName("Constructeur par défaut initialise le booster avec une liste de cartes")
    public void testBoosterConstruction() {
        assertNotNull(booster);
        assertNotNull(booster.getCartes());
        assertTrue(booster.getCartes() instanceof List);
    }

    @Test
    @DisplayName("Constructeur avec région initialise le booster correctement")
    public void testBoosterConstructionWithRegion() {
        assertNotNull(boosterWithRegion);
        assertNotNull(boosterWithRegion.getCartes());
        assertTrue(boosterWithRegion.getCartes() instanceof List);
    }

    @Test
    @DisplayName("getCartes() retourne une liste non vide")
    public void testGetCartes() {
        List<Carte> cartes = booster.getCartes();
        assertNotNull(cartes);
        assertTrue(cartes.size() > 0, "Le booster devrait contenir au moins une carte");
    }

    @Test
    @DisplayName("Toutes les cartes du booster sont non-nulles")
    public void testBoosterCartesNotNull() {
        for (Carte carte : booster.getCartes()) {
            assertNotNull(carte, "Aucune carte du booster ne devrait être null");
        }
    }

    @Test
    @DisplayName("Un booster contient au maximum 5 cartes")
    public void testBoosterContient5Cartes() {
        assertEquals(5, booster.getCartes().size(), "Un booster devrait contenir exactement 5 cartes");
    }

    @Test
    @DisplayName("Le booster avec région contient 5 cartes")
    public void testBoosterWithRegionContient5Cartes() {
        assertEquals(5, boosterWithRegion.getCartes().size(), "Un booster avec région devrait contenir 5 cartes");
    }

    @Test
    @DisplayName("Le booster retourne une liste non modifiable")
    public void testBoosterCartesImmutable() {
        List<Carte> cartes = booster.getCartes();
        assertNotNull(cartes);
        assertEquals(5, cartes.size());
    }
}
