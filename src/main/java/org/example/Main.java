package org.example;

import org.example.Interface.Interface;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        AuthService auth = new AuthService();
        User joueur=auth.login("leon", "mdp123");
        int countCommun = 0;
        int countRare = 0;
        int countEpique = 0;
        int countLegendaire = 0;

        for(int i = 0; i < 100; i++){
            Booster booster = new Booster();
            booster.ouvrirBooster(joueur);

            for (Carte carte : booster.getCartes()) {
                if (carte.getRarete() != null) {
                    switch (carte.getRarete()) {
                        case COMMUN:
                            countCommun++;
                            break;
                        case RARE:
                            countRare++;
                            break;
                        case EPIQUE:
                            countEpique++;
                            break;
                        case LEGENDAIRE:
                            countLegendaire++;
                            break;
                    }
                }
            }
        }

        System.out.println("=== Résultats de l'ouverture de 100 boosters (500 cartes) ===");
        System.out.println("Communes   : " + countCommun);
        System.out.println("Rares      : " + countRare);
        System.out.println("Épiques    : " + countEpique);
        System.out.println("Légendaires: " + countLegendaire);
        System.out.println("Total      : " + (countCommun + countRare + countEpique + countLegendaire));

    }


}
