package org.example;

import org.example.Interface.Interface;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Interface();
        Scanner scanner = new Scanner(System.in);

        AuthService authService = new AuthService();
        CollectionService collectionService = new CollectionService();

        int utilisateurConnecteId = -1;

        System.out.println("=== BIENVENUE DANS GENIE CARDS ===");

        boolean programmeEnCours = true;

        while (programmeEnCours) {

            if (utilisateurConnecteId == -1) {
                System.out.println("\nQue souhaitez-vous faire ?");
                System.out.println("1. Se connecter");
                System.out.println("2. S'inscrire");
                System.out.println("3. Quitter");
                System.out.print("Votre choix : ");

                String choix = scanner.nextLine();

                switch (choix) {
                    case "1":
                        System.out.print("Login : ");
                        String loginLog = scanner.nextLine();
                        System.out.print("Mot de passe : ");
                        String mdpLog = scanner.nextLine();

                        utilisateurConnecteId = authService.login(loginLog, mdpLog);
                        break;

                    case "2":
                        System.out.print("Choisissez un login : ");
                        String loginReg = scanner.nextLine();
                        System.out.print("Choisissez un mot de passe : ");
                        String mdpReg = scanner.nextLine();

                        utilisateurConnecteId = authService.register(loginReg, mdpReg);
                        break;

                    case "3":
                        programmeEnCours = false;
                        System.out.println("À bientôt !");
                        break;

                    default:
                        System.out.println("Choix invalide.");
                }
            }
            else {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1. Ouvrir un Booster (5 cartes)");
                System.out.println("2. Voir ma collection");
                System.out.println("3. Se déconnecter");
                System.out.print("Votre choix : ");

                String choix = scanner.nextLine();

                switch (choix) {
                    case "1":
                        System.out.println("\nOuverture du booster en cours...");
                        Booster booster = new Booster();

                        booster.enregistrerPourUtilisateur(utilisateurConnecteId);

                        System.out.println("Cartes obtenues :");
                        for (Carte c : booster.getCartes()) {
                            System.out.println("- " + c.toString());
                        }
                        break;

                    case "2":
                        System.out.println("\n--- VOTRE COLLECTION ---");
                        List<CartePossedee> maCollection = collectionService.getCollectionUtilisateur(utilisateurConnecteId);

                        if (maCollection.isEmpty()) {
                            System.out.println("Votre collection est vide. Allez ouvrir des boosters !");
                        } else {
                            for (CartePossedee cp : maCollection) {
                                System.out.println(cp.getQuantite() + "x " + cp.getCarte().toString());
                            }
                        }
                        break;

                    case "3":
                        System.out.println("Déconnexion réussie.");
                        utilisateurConnecteId = -1;
                        break;

                    default:
                        System.out.println("Choix invalide.");
                }
            }
        }

        scanner.close();
    }
}
