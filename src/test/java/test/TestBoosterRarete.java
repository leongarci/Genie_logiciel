package test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import carte.Booster;
import carte.Rarete;

public class TestBoosterRarete {

    @ParameterizedTest
    @CsvSource({
            "0,   COMMUN",
            "69,  COMMUN",
            "70,  RARE",
            "89,  RARE",
            "90,  EPIQUE",
            "97,  EPIQUE",
            "98,  LEGENDAIRE",
            "99,  LEGENDAIRE"
    })
    public void testDeterminerRarete_bornes(int roll, String attendu) {
        assertEquals(Rarete.valueOf(attendu.trim()), Booster.determinerRarete(roll));
    }

    @Test
    public void testRoll_negatif_leveException() {
        assertThrows(IllegalArgumentException.class, () -> Booster.determinerRarete(-1));
    }

    @Test
    public void testRoll_100_leveException() {
        assertThrows(IllegalArgumentException.class, () -> Booster.determinerRarete(100));
    }
}