package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Booster {
    private List<Carte> cartes = new ArrayList<>();
    private List<Integer> idsTires = new ArrayList<>();

    private MuseeDAO museeDAO = new MuseeDAO();
    private CollectionDAO collectionDAO = new CollectionDAO();

    public Booster() {
        genererCartes();
    }

    private void genererCartes() {
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            int roll = rand.nextInt(100);
            Rarete rareteTiree;

            if (roll < 70) {
                rareteTiree = Rarete.COMMUN;
            } else if (roll < 90) {
                rareteTiree = Rarete.RARE;
            } else if (roll < 98) {
                rareteTiree = Rarete.EPIQUE;
            } else {
                rareteTiree = Rarete.LEGENDAIRE;
            }

            Carte c = museeDAO.getRandomCarteByRarete(rareteTiree);
            //System.out.println(c.toString());
            if (c != null) {
                cartes.add(c);
                idsTires.add(c.getIdentifiant());
            }
        }
    }

    public void ouvrirBooster(User joueur) {
        if (joueur != null && !idsTires.isEmpty()) {
            collectionDAO.ajouterCartes(joueur.getId(), idsTires);
        }
    }

    public List<Carte> getCartes() {
        return cartes;
    }
}