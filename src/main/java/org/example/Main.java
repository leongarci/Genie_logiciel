package org.example;

import org.example.Interface.Interface;

import java.util.Scanner;

public class Main {
    static void main() {
        new Interface();
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("=== CONNEXION ===");
        System.out.print("Login : ");
        String login = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine();

        // On tente la connexion
        int userIdConnecte = authService.login(login, mdp);

        // Si la connexion a réussi (l'ID n'est pas -1)
        if (userIdConnecte != -1) {
            System.out.println("Génération de votre booster en cours...");

            Booster monBooster = new Booster();
            // On utilise le VRAI id de l'utilisateur connecté !
            monBooster.enregistrerPourUtilisateur(userIdConnecte);

            System.out.println("Voici vos nouvelles cartes :");
            for (Carte c : monBooster.getCartes()) {
                System.out.println("- " + c);
            }
        } else {
            System.out.println("Accès refusé. Fin du programme.");
        }

        scanner.close();
    }
}
