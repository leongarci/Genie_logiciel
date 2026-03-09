package org.example;

public class Carte {
    private String nom;
    private String type;
    private String[] themes;
    private String description;
    private String localisation;
    private String ville;
    private float[] prix;
    private float[] frequentations;
    private String description_lieu;

    public Carte(String nom, String type, String description, String localisation, String ville, String description_lieu) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.localisation = localisation;
        this.ville = ville;
        this.description_lieu = description_lieu;
        this.themes = new String[]{};
        this.prix = new float[]{0.0f};
        this.frequentations = new float[]{0.0f};
    }

    @Override
    public String toString() {
        return "Carte: " + nom + " (" + ville + ")";
    }
}
