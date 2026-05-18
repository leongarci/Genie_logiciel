// TestCartePossedee.java
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carte.Carte;
import carte.CartePossedee;
import carte.Rarete;

public class TestCartePossedee {

    private Carte carte1;
    private Carte carte2;
    private CartePossedee cartePossedee;

    @BeforeEach
    void setUp() {
        carte1 = new Carte(1, "Carte 1", "Paris", "Arts", "Histoire", "Atout", "Intérêt", Rarete.RARE);
        carte2 = new Carte(2, "Carte 2", "Lyon", "Histoire", "Histoire 2", "Atout 2", "Intérêt 2", Rarete.EPIQUE);
        cartePossedee = new CartePossedee(carte1, 1);
    }

    @Test
    @DisplayName("Constructeur initialise correctement la carte et la quantité")
    public void testNewCartePossedee() {
        CartePossedee cp = new CartePossedee(carte1, 3);

        assertNotNull(cp);
        assertEquals(carte1, cp.getCarte(), "La carte devrait être celle initialisée");
        assertEquals(3, cp.getQuantite(), "La quantité devrait être 3");
    }

    @Test
    @DisplayName("Setters et getters modifient correctement la carte et la quantité")
    public void testSettersEtGetters() {
        assertEquals(carte1, cartePossedee.getCarte());
        assertEquals(1, cartePossedee.getQuantite());

        cartePossedee.setCarte(carte2);
        cartePossedee.setQuantite(5);

        assertEquals(carte2, cartePossedee.getCarte(), "La carte devrait être modifiée");
        assertEquals(5, cartePossedee.getQuantite(), "La quantité devrait être 5");
    }

    @Test
    @DisplayName("La quantité peut être augmentée")
    public void testAugmenterQuantite() {
        cartePossedee.setQuantite(cartePossedee.getQuantite() + 2);
        assertEquals(3, cartePossedee.getQuantite(), "La quantité devrait passer à 3");
    }

    @Test
    @DisplayName("La quantité peut être remise à zéro")
    public void testQuantiteZero() {
        cartePossedee.setQuantite(0);
        assertEquals(0, cartePossedee.getQuantite(), "La quantité devrait être 0");
    }
}
