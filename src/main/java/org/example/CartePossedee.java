package org.example;

public class CartePossedee {
    private Carte carte;
    private int quantite;

    public CartePossedee(Carte carte, int quantite) {
        this.carte = carte;
        this.quantite = quantite;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
