package musee;

/**
 * Enum EnumRegion
 *
 * Liste toutes les régions de France avec leurs numéros respectifs
 */
public enum EnumRegion {
    ILE_DE_FRANCE("Ile-de-France",          11, "Ile-de-France",          null),
    AUVERGNE     ("Auvergne-Rhône-Alpes",   84, "Auvergne-Rhône-Alpes",   null),
    PROVENCE     ("Provence-Alpes-Côte d'Azur", 93, "Provence-Alpes-Côte d'Azur", null),
    LOIRE        ("Pays-de-la-Loire",        52, "Pays-de-la-Loire",        null),
    BOURGOGNE    ("Bourgogne-Franche-Comté", 27, "Bourgogne-Franche-Comté", null),
    BRETAGNE     ("Bretagne",                53, "Bretagne",                null),
    CENTRE_VAL_DE_LOIRE("Centre-Val de Loire", 24, "Centre-Val de Loire",  null),
    CORSE        ("Corse",                   94, "Corse",                   null),
    GRAND_EST    ("Grand Est",               44, "Grand Est",               null),
    HAUTS_DE_FRANCE("Hauts-de-France",       32, "Hauts-de-France",         null),
    NORMANDIE    ("Normandie",               28, "Normandie",               null),
    AQUITAINE    ("Nouvelle-Aquitaine",      75, "Nouvelle-Aquitaine",      null),
    OCCITANIE    ("Occitanie",               76, "Occitanie",               null),
    GUADELOUPE   ("Guadeloupe",  1, "DROM", "Guadeloupe"),
    GUYANE       ("Guyane",      3, "DROM", "Guyane"),
    MARTINIQUE   ("Martinique",  2, "DROM", "Martinique"),
    REUNION      ("La Réunion",  4, "DROM", "La Réunion"),
    MAYOTTE      ("Mayotte",     6, "DROM", "Mayotte");

    private final String nomAffichage;
    private final int numerRegion;
    private final String regionBDD;
    private final String departementBDD;

    EnumRegion(String nomAffichage, int numerRegion, String regionBDD, String departementBDD) {
        this.nomAffichage    = nomAffichage;
        this.numerRegion     = numerRegion;
        this.regionBDD       = regionBDD;
        this.departementBDD  = departementBDD;
    }

    public String getNomAffichage()  { return nomAffichage; }
    public int    numeroReg()        { return numerRegion; }
    public String getRegionBDD()     { return regionBDD; }
    public String getDepartementBDD(){ return departementBDD; }
    public boolean isDrom()          { return "DROM".equals(regionBDD); }
}
