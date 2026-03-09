package org.example;

public class Main {
    static void main() {
        Booster booster = new Booster();
        booster.enregistrerPourUtilisateur(1);
        booster.getCartes().forEach(carte -> {
            System.out.println(carte.toString());
        });
    }
}
