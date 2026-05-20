package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carte.Carte;
import carte.Rarete;

public class TestCarte {

    private Carte carte;

    @BeforeEach
    void setUp() {
        carte = new Carte(1, "Musée du Louvre", "Paris", "Arts", "Histoire du musée", "Atout 1", "Intérêt 1", Rarete.COMMUN);
    }

    @Test
    @DisplayName("Constructeur initialise tous les champs obligatoires")
    public void testNewCarte() {
        assertEquals(1, carte.getIdentifiant());
        assertEquals("Musée du Louvre", carte.getNomOfficiel());
        assertEquals("Paris", carte.getVille());
        assertEquals("Arts", carte.getDomaineThematique());
        assertEquals("Histoire du musée", carte.getHistoire());
        assertEquals("Atout 1", carte.getAtout());
        assertEquals("Intérêt 1", carte.getInteret());
        assertEquals(Rarete.COMMUN, carte.getRarete());
    }

    @Test
    @DisplayName("Tous les champs Integer sont null par défaut")
    public void testNullIntegersParDefaut() {
        assertNull(carte.getAnneeCreation());
        assertNull(carte.getPayant());
        assertNull(carte.getGratuit());
        assertNull(carte.getTotal());
        assertNull(carte.getIndividuel());
        assertNull(carte.getScolaires());
        assertNull(carte.getGroupesHorsScolaires());
        assertNull(carte.getMoins18AnsHorsScolaires());
        assertNull(carte.getDe18A25Ans());
    }

    @Test
    @DisplayName("Setters et getters modifient correctement tous les champs")
    public void testSettersEtGetters() {
        carte.setIdentifiant(42);
        carte.setNomOfficiel("Musée d'Orsay");
        carte.setLieu("7e arrondissement");
        carte.setAdresse("1 Rue de la Légion d'Honneur");
        carte.setCodePostal("75007");
        carte.setVille("Paris");
        carte.setRegion("Île-de-France");
        carte.setDepartement("Paris");
        carte.setIdmusofile("M0001");
        carte.setCoordonnees("48.8600,2.3266");
        carte.setDomaineThematique("Arts,Peinture");
        carte.setHistoire("Ancienne gare");
        carte.setAtout("Chef-d'oeuvre");
        carte.setInteret("Monet");
        carte.setAnneeCreation(1986);
        carte.setPayant(1);
        carte.setGratuit(0);
        carte.setTotal(100);
        carte.setIndividuel(60);
        carte.setScolaires(10);
        carte.setGroupesHorsScolaires(20);
        carte.setMoins18AnsHorsScolaires(5);
        carte.setDe18A25Ans(15);
        carte.setRarete(Rarete.EPIQUE);

        assertEquals(42, carte.getIdentifiant());
        assertEquals("Musée d'Orsay", carte.getNomOfficiel());
        assertEquals("7e arrondissement", carte.getLieu());
        assertEquals("1 Rue de la Légion d'Honneur", carte.getAdresse());
        assertEquals("75007", carte.getCodePostal());
        assertEquals("Paris", carte.getVille());
        assertEquals("Île-de-France", carte.getRegion());
        assertEquals("Paris", carte.getDepartement());
        assertEquals("M0001", carte.getIdmusofile());
        assertEquals("48.8600,2.3266", carte.getCoordonnees());
        assertEquals("Arts,Peinture", carte.getDomaineThematique());
        assertEquals("Ancienne gare", carte.getHistoire());
        assertEquals("Chef-d'oeuvre", carte.getAtout());
        assertEquals("Monet", carte.getInteret());
        assertEquals(1986, carte.getAnneeCreation());
        assertEquals(1, carte.getPayant());
        assertEquals(0, carte.getGratuit());
        assertEquals(100, carte.getTotal());
        assertEquals(60, carte.getIndividuel());
        assertEquals(10, carte.getScolaires());
        assertEquals(20, carte.getGroupesHorsScolaires());
        assertEquals(5, carte.getMoins18AnsHorsScolaires());
        assertEquals(15, carte.getDe18A25Ans());
        assertEquals(Rarete.EPIQUE, carte.getRarete());
    }

    @Test
    @DisplayName("getThemes() découpe correctement un domaine multi-thème (trim inclus)")
    public void testGetThemes() {
        Carte c = new Carte(1, "Carte", "Paris", "Arts décoratifs,Ethnologie,Histoire",
                "Histoire", "Atout", "Intérêt", Rarete.LEGENDAIRE);
        String[] themes = c.getThemes();
        assertEquals(3, themes.length);
        // trim attendu sur chaque thème
        assertEquals("Arts décoratifs", themes[0].trim());
        assertEquals("Ethnologie", themes[1].trim());
        assertEquals("Histoire", themes[2].trim());
    }

    @Test
    @DisplayName("getThemes() avec guillemets retourne les bons thèmes")
    public void testGetThemesAvecGuillemets() {
        Carte c = new Carte(1, "Carte", "Paris", "\"Arts décoratifs,Ethnologie\"",
                "Histoire", "Atout", "Intérêt", Rarete.COMMUN);
        String[] themes = c.getThemes();
        assertEquals(2, themes.length);
        assertEquals("Arts décoratifs", themes[0].trim().replace("\"", ""));
        assertEquals("Ethnologie", themes[1].trim().replace("\"", ""));
    }

    @Test
    @DisplayName("getThemes() avec domaine null ne lève pas d'exception")
    public void testGetThemesDomaineNull() {
        Carte c = new Carte(1, "Carte", "Paris", null, "Histoire", "Atout", "Intérêt", Rarete.COMMUN);
        String[] themes = c.getThemes();
        assertNotNull(themes);
        assertEquals(0, themes.length);
    }

    @Test
    @DisplayName("toString() retourne la chaîne correcte avec tous les champs")
    public void testToString() {
        // Créer une carte avec tous les champs remplis
        Carte c = new Carte(1, "Musée du Louvre", "Paris", "Arts", "Histoire du musée", "Atout 1", "Intérêt 1", Rarete.COMMUN);
        c.setIdmusofile("M0001");
        c.setAdresse("1 Rue de la Légion d'Honneur");
        c.setLieu("7e arrondissement");
        c.setCodePostal("75001");
        c.setVille("Paris");
        c.setRegion("Île-de-France");
        c.setDepartement("Paris");
        c.setCoordonnees("48.8600,2.3266");
        c.setDomaineThematique("Arts");
        c.setHistoire("Histoire du musée");
        c.setAtout("Atout 1");
        c.setInteret("Intérêt 1");
        c.setAnneeCreation(1986);
        c.setPayant(1000000);
        c.setGratuit(500000);
        c.setTotal(1500000);
        c.setIndividuel(1200000);
        c.setScolaires(200000);
        c.setGroupesHorsScolaires(50000);
        c.setMoins18AnsHorsScolaires(100000);
        c.setDe18A25Ans(150000);
        c.setRarete(Rarete.EPIQUE);

        String expected = "Carte{"
                + "identifiant=1, "
                + "nomOfficiel='Musée du Louvre', "
                + "idmusofile='M0001', "
                + "adresse='1 Rue de la Légion d'Honneur', "
                + "lieu='7e arrondissement', "
                + "codePostal='75001', "
                + "ville='Paris', "
                + "region='Île-de-France', "
                + "departement='Paris', "
                + "coordonnees='48.8600,2.3266', "
                + "domaineThematique='Arts', "
                + "histoire='Histoire du musée', "
                + "atout='Atout 1', "
                + "interet='Intérêt 1', "
                + "anneeCreation=1986, "
                + "payant=1000000, "
                + "gratuit=500000, "
                + "total=1500000, "
                + "individuel=1200000, "
                + "scolaires=200000, "
                + "groupesHorsScolaires=50000, "
                + "moins18AnsHorsScolaires=100000, "
                + "de18A25Ans=150000, "
                + "rarete=EPIQUE}";

        assertEquals(expected, c.toString());
    }

    @Test
    @DisplayName("setRarete modifie la rareté")
    public void testSetRarete() {
        assertEquals(Rarete.COMMUN, carte.getRarete());
        carte.setRarete(Rarete.LEGENDAIRE);
        assertEquals(Rarete.LEGENDAIRE, carte.getRarete());
    }

    @Test
    @DisplayName("setTotal à null remet la valeur à null")
    public void testSetTotalNull() {
        carte.setTotal(500);
        assertEquals(500, carte.getTotal());
        carte.setTotal(null);
        assertNull(carte.getTotal());
    }

    @Test
    @DisplayName("getThemes() avec domaine vide retourne tableau vide")
    public void testGetThemesDomaineVide() {
        Carte c = new Carte(1, "Carte", "Paris", "", "hist", "atout", "interet", Rarete.COMMUN);
        String[] themes = c.getThemes();
        assertEquals(0, themes.length); // va échouer si non géré dans Carte.java
    }
}
