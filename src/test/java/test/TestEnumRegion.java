package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;
import musee.EnumRegion;

public class TestEnumRegion {

    @Test
    public void testContient18Valeurs() {
        assertEquals(18, EnumRegion.values().length);
    }

    @ParameterizedTest
    @EnumSource(EnumRegion.class)
    public void testNomAffichageNonVide(EnumRegion r) {
        assertNotNull(r.getNomAffichage());
        assertFalse(r.getNomAffichage().isBlank());
    }

    @ParameterizedTest
    @EnumSource(EnumRegion.class)
    public void testNumeroPositif(EnumRegion r) {
        assertTrue(r.numeroReg() >= 1);
    }

    @Test
    public void testNumerosUniques() {
        long nbUniques = java.util.Arrays.stream(EnumRegion.values())
                .mapToInt(EnumRegion::numeroReg)
                .distinct()
                .count();
        assertEquals(EnumRegion.values().length, nbUniques,
                "Deux régions partagent le même numéro");
    }

    @Test
    public void testMartiniqueOrtho() {
        assertEquals("Martinique", EnumRegion.MARTINIQUE.getNomAffichage());
    }

    @Test
    public void testHautsDeFranceOrtho() {
        assertEquals("Hauts-de-France", EnumRegion.HAUTS_DE_FRANCE.getNomAffichage());
    }
}