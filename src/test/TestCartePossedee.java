
import carte.Carte;
import carte.CartePossedee;
import carte.Rarete;

public class TestCartePossedee {

    public void testNewCartePossedee() {
        Carte carte = new Carte(1, "Musée du Louvre", "Paris", "Arts", "Histoire", "Atout", "Intérêt", Rarete.COMMUN);
        CartePossedee cartePossedee = new CartePossedee(carte, 3);

        assert cartePossedee.getCarte() == carte;
        assert cartePossedee.getQuantite() == 3;
    }

    public void testSettersEtGetters() {
        Carte carte1 = new Carte(1, "Carte 1", "Paris", "Arts", "Histoire", "Atout", "Intérêt", Rarete.RARE);
        Carte carte2 = new Carte(2, "Carte 2", "Lyon", "Histoire", "Histoire 2", "Atout 2", "Intérêt 2", Rarete.EPIQUE);

        CartePossedee cartePossedee = new CartePossedee(carte1, 1);

        cartePossedee.setCarte(carte2);
        cartePossedee.setQuantite(5);

        assert cartePossedee.getCarte() == carte2;
        assert cartePossedee.getQuantite() == 5;
    }
}
