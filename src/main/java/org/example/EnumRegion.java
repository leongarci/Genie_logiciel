package org.example;

public enum EnumRegion {
    ILE_DE_FRANCE("Ile-de-France",11),
    AUVERGNE("Auvergne-Rhône-Alpes",84),
    PROVENCE("Provence-Alpes-Côte d'Azur",93),
    LOIRE("Pays-de-la-Loire",52),
    BOURGOGNE("Bourgogne-Franche-Comté",27),
    BRETAGNE("Bretagne",28),
    CENTRE_VAL_DE_LOIRE("Centre-Val de Loire",24),
    CORSE("Corse",94),
    GRAND_EST("Grand Est",44),
    GUADELOUPE("Guadeloupe",1),
    GUYANE("Guyane",3),
    HAUTS_DE_FRANCE("Hautes-de-France",32),
    REUNION("La Réunion",4),
    MARTINIQUE("Maritinique",2),
    MAYOTTE("Mayotte",6),
    NORMANDIE("Normandie",28),
    AQUITAINE("Nouvelle-Aquitaine",75),
    OCCITANIE("Occitanie",76);


    private final String nomAffichage;
    private final int numerRegion;

    EnumRegion(String nomAffichage, int numerRegion) {
        this.nomAffichage = nomAffichage;
        this.numerRegion = numerRegion;
    }

    public int numeroReg() {return numerRegion;}

    public String getNomAffichage() {
        return nomAffichage;
    }
}
