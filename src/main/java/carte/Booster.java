package carte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import auth.User;
import collection.CollectionDAO;
import config.Config;
import musee.EnumRegion;
import musee.MuseeDAO;

/**
 * Classe Booster
 *
 * Représente un booster contenant plusieurs cartes
 */
public class Booster {

    private List<Carte> cartes = new ArrayList<>();
    private List<Integer> idsTires = new ArrayList<>();

    private MuseeDAO museeDAO = new MuseeDAO();
    private CollectionDAO collectionDAO = new CollectionDAO();

    // Génère un booster aléatoire
    public Booster() {
        genererCartes();
    }

    // Génère un booster aléatoire des cartes d'une région spécifique
    public Booster(EnumRegion region) {
        genererCartes(region);
    }

    // Génère les cartes du booster
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
            if (c != null) {
                System.out.println(c.toString() + " " + rareteTiree.toString());
                cartes.add(c);
                idsTires.add(c.getIdentifiant());
            }
        }
    }

    // Génère les cartes du booster pour une région spécifique
    private void genererCartes(EnumRegion region) {
        for (int i = 0; i < 5; i++) {
            Carte c = museeDAO.getRandomCarteByRegion(region);
            if (c != null) {
                System.out.println("Booster région" + c);
                cartes.add(c);
                idsTires.add(c.getIdentifiant());
            } else {
                System.err.println("Aucune carte trouvée pour la région : " + region.getNomAffichage());
            }
        }
    }

    // Ouvre un booster et attribue les cartes à un joueur
    public List<Carte> ouvrirBooster(User joueur) {
        if (joueur == null || idsTires.isEmpty()) {
            System.err.println("Erreur : Joueur non connecté ou booster vide.");
            return null;
        }
        BoosterDAO boosterDAO = new BoosterDAO();
        if (!boosterDAO.peutOuvrirBooster(joueur.getId())) {
            System.out.println("Désolé " + joueur.getLogin() + ", vous avez déjà ouvert vos " + Config.NBR_BOOSTER_MAX + " boosters aujourd'hui ! Revenez demain.");
            return null;
        }
        collectionDAO.ajouterCartes(joueur.getId(), idsTires);
        boosterDAO.enregistrerOuverture(joueur.getId());
        System.out.println("Booster ouvert avec succès par " + joueur.getLogin() + " !");
        return cartes;
    }

    public List<Carte> ouvrirBooster(User joueur, EnumRegion region) {
        if (joueur == null || idsTires.isEmpty()) {
            System.err.println("Erreur : Joueur non connecté ou booster vide.");
            return null;
        }
        BoosterDAO boosterDAO = new BoosterDAO();
        if (!boosterDAO.peutOuvrirBooster(joueur.getId())) {
            System.out.println("Désolé " + joueur.getLogin() + ", vous avez déjà ouvert vos " + Config.NBR_BOOSTER_MAX + " boosters aujourd'hui ! Revenez demain.");
            return null;
        }
        collectionDAO.ajouterCartes(joueur.getId(), idsTires);
        boosterDAO.enregistrerOuverture(joueur.getId());
        System.out.println("Booster ouvert avec succès par " + joueur.getLogin() + " !");
        return cartes;
    }

    // Récupération des cartes du booster
    public List<Carte> getCartes() {
        return Collections.unmodifiableList(cartes);
    }

    public static Rarete determinerRarete(int roll) {
        if (roll < 0 || roll >= 100)
            throw new IllegalArgumentException("roll doit être entre 0 et 99, obtenu : " + roll);
        if (roll < 70) return Rarete.COMMUN;
        if (roll < 90) return Rarete.RARE;
        if (roll < 98) return Rarete.EPIQUE;
        return Rarete.LEGENDAIRE;
    }
}
