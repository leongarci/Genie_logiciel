package musee;

public enum EnumRegion {
    ILE_DE_FRANCE("Île-de-France"),
    AUVERGNE("Auvergne-Rhône-Alpes"),
    PROVENCE("Provence-Alpes-Côte d'Azur"),
    LOIRE("Pays-de-la-Loire"),
    BOURGOGNE("Bourgogne-Franche-Comté"),
    BRETAGNE("Bretagne"),
    CENTRE_VAL_DE_LOIRE("Centre-Val de Loire"),
    CORSE("Corse"),
    GRAND_EST("Grand-Est"),
    GUADELOUPE("Guadeloupe"),
    GUYANE("Guyane"),
    HAUTS_DE_FRANCE("Hautes-de-France"),
    REUNION("La Réunion"),
    MARTINIQUE("Maritinique"),
    MAYOTTE("Mayotte"),
    NORMANDIE("Normandie"),
    AQUITAINE("Nouvelle-Aquitaine"),
    OCCITANIE("Occitanie");

    private final String nomAffichage;

    EnumRegion(String nomAffichage) {
        this.nomAffichage = nomAffichage;
    }

    public String getNomAffichage() {
        return nomAffichage;
    }
}
