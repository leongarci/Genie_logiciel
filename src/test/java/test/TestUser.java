package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import auth.User;

public class TestUser {

    @Test
    public void testConstructeur_stockeIdEtLogin() {
        User u = new User(42, "alice");
        assertEquals(42, u.getId());
        assertEquals("alice", u.getLogin());
    }

    @Test
    public void testSetLogin_modifieLaValeur() {
        User u = new User(1, "bob");
        u.setLogin("charlie");
        assertEquals("charlie", u.getLogin());
    }

    @Test
    public void testSetId_modifieLaValeur() {
        User u = new User(1, "bob");
        u.setId(99);
        assertEquals(99, u.getId());
    }
}