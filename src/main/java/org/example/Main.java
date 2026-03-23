package org.example;

public class Main {
    public static void main(String[] args) {

        AuthService authService = new AuthService();
        User joueur=authService.login("leon", "mdp123");
        Booster booster=new Booster();
        booster.ouvrirBooster(joueur);

    }
}
