package test;

import carte.Carte;
import carte.Rarete;

public class TestCarte {

    public void testNewCarte() {
        Carte carte = new Carte(1, "Musée du Louvre", "Paris", "Arts", "Histoire du musée", "Atout 1", "Intérêt 1", Rarete.COMMUN);

        assert carte.getIdentifiant() == 1;
        assert carte.getNomOfficiel().equals("Musée du Louvre");
        assert carte.getVille().equals("Paris");
        assert carte.getDomaineThematique().equals("Arts");
        assert carte.getHistoire().equals("Histoire du musée");
        assert carte.getAtout().equals("Atout 1");
        assert carte.getInteret().equals("Intérêt 1");
        assert carte.getRarete() == Rarete.COMMUN;
    }

    public void testSettersEtGetters() {
        Carte carte = new Carte(1, "Carte initiale", "Paris", "Arts", "Histoire", "Atout", "Intérêt", Rarete.RARE);

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

        assert carte.getIdentifiant() == 42;
        assert carte.getNomOfficiel().equals("Musée d'Orsay");
        assert carte.getLieu().equals("7e arrondissement");
        assert carte.getAdresse().equals("1 Rue de la Légion d'Honneur");
        assert carte.getCodePostal().equals("75007");
        assert carte.getVille().equals("Paris");
        assert carte.getRegion().equals("Île-de-France");
        assert carte.getDepartement().equals("Paris");
        assert carte.getIdmusofile().equals("M0001");
        assert carte.getCoordonnees().equals("48.8600,2.3266");
        assert carte.getDomaineThematique().equals("Arts,Peinture");
        assert carte.getHistoire().equals("Ancienne gare");
        assert carte.getAtout().equals("Chef-d'oeuvre");
        assert carte.getInteret().equals("Monet");
        assert carte.getAnneeCreation() == 1986;
        assert carte.getPayant() == 1;
        assert carte.getGratuit() == 0;
        assert carte.getTotal() == 100;
        assert carte.getIndividuel() == 60;
        assert carte.getScolaires() == 10;
        assert carte.getGroupesHorsScolaires() == 20;
        assert carte.getMoins18AnsHorsScolaires() == 5;
        assert carte.getDe18A25Ans() == 15;
        assert carte.getRarete() == Rarete.EPIQUE;
    }

    public void testGetThemes() {
        Carte carte = new Carte(1, "Carte", "Paris", "Arts décoratifs, Ethnologie , Histoire", "Histoire", "Atout", "Intérêt", Rarete.LEGENDAIRE);

        assert carte.getThemes().length == 3;
        assert carte.getThemes()[0].equals("Artsdécoratifs");
        assert carte.getThemes()[1].equals("Ethnologie");
        assert carte.getThemes()[2].equals("Histoire");
    }

    public void testGetThemesAvecGuillemets() {
        Carte carte = new Carte(1, "Carte", "Paris", "\"Arts décoratifs, Ethnologie\"", "Histoire", "Atout", "Intérêt", Rarete.COMMUN);

        assert carte.getThemes().length == 2;
        assert carte.getThemes()[0].equals("Artsdécoratifs");
        assert carte.getThemes()[1].equals("Ethnologie");
    }

    public void testToString() {
        Carte carte = new Carte(7, "Musée de Cluny", "Paris", "Moyen Âge", "Histoire", "Atout", "Intérêt", Rarete.RARE);

        assert carte.toString().equals("Carte: Musée de Cluny (Paris) - Type: Moyen Âge");
    }

    public void testNullIntegersParDefaut() {
        Carte carte = new Carte(1, "Carte", "Paris", "Arts", "Histoire", "Atout", "Intérêt", Rarete.COMMUN);

        assert carte.getAnneeCreation() == null;
        assert carte.getPayant() == null;
        assert carte.getGratuit() == null;
        assert carte.getTotal() == null;
        assert carte.getIndividuel() == null;
        assert carte.getScolaires() == null;
        assert carte.getGroupesHorsScolaires() == null;
        assert carte.getMoins18AnsHorsScolaires() == null;
        assert carte.getDe18A25Ans() == null;
    }
}
