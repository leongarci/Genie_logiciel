package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals("Ethnologie",      themes[1].trim());
        assertEquals("Histoire",        themes[2].trim());
    }

    @Test
    @DisplayName("getThemes() avec guillemets retourne les bons thèmes")
    public void testGetThemesAvecGuillemets() {
        Carte c = new Carte(1, "Carte", "Paris", "\"Arts décoratifs,Ethnologie\"",
                "Histoire", "Atout", "Intérêt", Rarete.COMMUN);
        String[] themes = c.getThemes();
        assertEquals(2, themes.length);
        assertEquals("Arts décoratifs", themes[0].trim().replace("\"", ""));
        assertEquals("Ethnologie",      themes[1].trim().replace("\"", ""));
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
    @DisplayName("toString() retourne le format attendu")
    public void testToString() {
        Carte c = new Carte(7, "Musée de Cluny", "Paris", "Moyen Âge", "Histoire", "Atout", "Intérêt", Rarete.RARE);
        assertEquals("Carte: Musée de Cluny (Paris) - Type: Moyen Âge", c.toString());
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
