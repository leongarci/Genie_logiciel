package carte;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import auth.User;
import collection.CollectionDAO;
import musee.EnumRegion;
import musee.MuseeDAO;

public class Booster {

    private List<Carte> cartes = new ArrayList<>();
    private List<Integer> idsTires = new ArrayList<>();

    private MuseeDAO museeDAO = new MuseeDAO();
    private CollectionDAO collectionDAO = new CollectionDAO();

    public Booster() {
        genererCartes();
    }

    public Booster(EnumRegion region) {
        genererCartes(region);
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
            System.out.println(c.toString()+" "+rareteTiree.toString());
            if (c != null) {
                cartes.add(c);
                idsTires.add(c.getIdentifiant());
            }
        }
    }

    private void genererCartes(EnumRegion region) {
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            Carte c = museeDAO.getRandomCarteByRegion(region.getNomAffichage());
            System.out.println("Booster région" + c.toString());
            if (c != null) {
                cartes.add(c);
                idsTires.add(c.getIdentifiant());
            }
        }
    }

    public boolean ouvrirBooster(User joueur) {
        if (joueur == null || idsTires.isEmpty()) {
            System.err.println("Erreur : Joueur non connecté ou booster vide.");
            return false;
        }
        BoosterDAO boosterDAO = new BoosterDAO();
        if (!boosterDAO.peutOuvrirBooster(joueur.getId())) {
            System.out.println("Désolé " + joueur.getLogin() + ", vous avez déjà ouvert vos 3 boosters aujourd'hui ! Revenez demain.");
            return false;
        }
        collectionDAO.ajouterCartes(joueur.getId(), idsTires);
        boosterDAO.enregistrerOuverture(joueur.getId());
        System.out.println("Booster ouvert avec succès par " + joueur.getLogin() + " !");
        return true;
    }

    public List<Carte> getCartes() {
        return cartes;
    }
}
