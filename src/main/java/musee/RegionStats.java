package musee;

/**
 * Classe RegionStats
 * Données agrégées d'une région — entrées musées + données démographiques.
 * Tous les scores sont calculés dans le constructeur à partir des données brutes.
 * Utilisé par MuseeStatsDAO.
 */
public class RegionStats {

    // Données brutes
    private final String nomRegion;
    private final int    population;       // total_general de la table region
    private final double revenuFiscalMedian; // richesse de la table region
    private final int    nombreTotalMusees;
    private final long   totalEntrees;
    private final long   entreesPayantes;
    private final long   entreesGratuites;
    private final long   entreesJeunes;      // moins18 + 18-25 ans
    private final long   entreesScolaires;

    // Scores calculés

    /** Part d'entrées gratuites (0–1). Corrélation avec richesse. */
    private final double tauxGratuite;

    /** Entrées pour 1 habitant. Corrélation avec richesse. */
    private final double entreesParHabitant;

    /** Entrées moyennes par musée dans la région. */
    private final double entreesParMusee;

    /** Part d'entrées jeunes (<25 ans + scolaires) sur le total. */
    private final double tauxJeunes;

    private final double scoreAccessibilite;

    public RegionStats(String nomRegion, int population, double revenuFiscalMedian,
                       int nombreTotalMusees, long totalEntrees, long entreesPayantes,
                       long entreesGratuites, long entreesJeunes, long entreesScolaires) {
        this.nomRegion          = nomRegion;
        this.population         = population;
        this.revenuFiscalMedian = revenuFiscalMedian;
        this.nombreTotalMusees  = nombreTotalMusees;
        this.totalEntrees       = totalEntrees;
        this.entreesPayantes    = entreesPayantes;
        this.entreesGratuites   = entreesGratuites;
        this.entreesJeunes      = entreesJeunes;
        this.entreesScolaires   = entreesScolaires;

        this.tauxGratuite       = totalEntrees > 0 ? (double) entreesGratuites / totalEntrees : 0;
        this.entreesParHabitant = population   > 0 ? (double) totalEntrees     / population   : 0;
        this.entreesParMusee    = nombreTotalMusees > 0 ? (double) totalEntrees / nombreTotalMusees : 0;
        this.tauxJeunes         = totalEntrees > 0
                ? (double)(entreesJeunes + entreesScolaires) / totalEntrees : 0;
        this.scoreAccessibilite = nombreTotalMusees > 0
                ? tauxGratuite * Math.log1p(entreesParMusee) : 0;
    }

    // Getters
    public String getNomRegion()           { return nomRegion; }
    public int    getPopulation()          { return population; }
    public double getRevenuFiscalMedian()  { return revenuFiscalMedian; }
    public double getPibParHabitant()      { return revenuFiscalMedian; }
    public int    getNombreTotalMusees()   { return nombreTotalMusees; }
    public long   getTotalEntrees()        { return totalEntrees; }
    public long   getEntreesPayantes()     { return entreesPayantes; }
    public long   getEntreesGratuites()    { return entreesGratuites; }
    public long   getEntreesJeunes()       { return entreesJeunes; }
    public long   getEntreesScolaires()    { return entreesScolaires; }
    public double getTauxGratuite()        { return tauxGratuite; }
    public double getEntreesParHabitant()  { return entreesParHabitant; }
    public double getEntreesParMusee()     { return entreesParMusee; }
    public double getTauxJeunes()          { return tauxJeunes; }
    public double getScoreAccessibilite()  { return scoreAccessibilite; }
}
