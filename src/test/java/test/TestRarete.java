package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;
import carte.Rarete;

public class TestRarete {

    @Test
    public void testContient4Valeurs() {
        assertEquals(4, Rarete.values().length);
    }

    @Test
    public void testOrdreOrdinal() {
        assertTrue(Rarete.COMMUN.ordinal()    < Rarete.RARE.ordinal());
        assertTrue(Rarete.RARE.ordinal()      < Rarete.EPIQUE.ordinal());
        assertTrue(Rarete.EPIQUE.ordinal()    < Rarete.LEGENDAIRE.ordinal());
    }

    @ParameterizedTest
    @EnumSource(Rarete.class)
    public void testValueOfFonctionne(Rarete r) {
        assertEquals(r, Rarete.valueOf(r.name()));
    }

    @Test
    public void testValueOfNomInvalide_leveException() {
        assertThrows(IllegalArgumentException.class, () -> Rarete.valueOf("MYTHIQUE"));
    }
}