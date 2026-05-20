
import auth.AuthService;
import auth.User;
import carte.Booster;
import musee.EnumRegion;

import java.util.List;
import java.util.Map;

import musee.MuseeStatsDAO;
import musee.RegionStats;
import carte.Carte;
import carte.Rarete;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        AuthService authService = new AuthService();
        User joueur = authService.login("leon", "mdp123");
        Booster booster = new Booster(EnumRegion.ILE_DE_FRANCE);
        List<Carte> cartesObtenues = booster.ouvrirBooster(joueur);
        System.out.println("Vous avez obtenu les cartes suivantes :");
        cartesObtenues.forEach(c -> System.out.println(c.getNomOfficiel()));
    }
}
