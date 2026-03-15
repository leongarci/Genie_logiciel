package org.example;

public class Carte {

    private int identifiant;
    private String nomOfficiel;
    private String idmusofile; // Code officiel du musée
    private String adresse;
    private String lieu;
    private String codePostal;
    private String ville;
    private String region;
    private String departement;
    private String coordonnees;
    private String domaineThematique;
    private String histoire;
    private String atout;
    private String interet;
    private Integer anneeCreation; // Integer (avec majuscule) permet d'accepter une valeur null si l'année est inconnue
    private Integer payant;
    private Integer gratuit;
    private Integer total;
    private Integer individuel;
    private Integer scolaires;
    private Integer groupesHorsScolaires;
    private Integer moins18AnsHorsScolaires;
    private Integer de18A25Ans;
    private Rarete rarete;

    public Carte(int identifiant, String nomOfficiel, String ville, String domaineThematique, String histoire, String atout, String interet,Rarete rarete) {
        this.identifiant = identifiant;
        this.nomOfficiel = nomOfficiel;
        this.ville = ville;
        this.domaineThematique = domaineThematique;
        this.histoire = histoire;
        this.atout = atout;
        this.interet = interet;
        this.rarete = rarete;
    }

    public int getIdentifiant() { return identifiant; }
    public String getNomOfficiel() { return nomOfficiel; }
    public String getVille() { return ville; }
    public String getDomaineThematique() { return domaineThematique; }
    public String getHistoire() { return histoire; }

    public void setIdentifiant(int identifiant) {
        this.identifiant = identifiant;
    }

    public void setNomOfficiel(String nomOfficiel) {
        this.nomOfficiel = nomOfficiel;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getIdmusofile() {
        return idmusofile;
    }

    public void setIdmusofile(String idmusofile) {
        this.idmusofile = idmusofile;
    }

    public String getCoordonnees() {
        return coordonnees;
    }

    public void setCoordonnees(String coordonnees) {
        this.coordonnees = coordonnees;
    }

    public void setDomaineThematique(String domaineThematique) {
        this.domaineThematique = domaineThematique;
    }

    public void setHistoire(String histoire) {
        this.histoire = histoire;
    }

    public String getAtout() {
        return atout;
    }

    public void setAtout(String atout) {
        this.atout = atout;
    }

    public String getInteret() {
        return interet;
    }

    public void setInteret(String interet) {
        this.interet = interet;
    }

    public Integer getAnneeCreation() {
        return anneeCreation;
    }

    public void setAnneeCreation(Integer anneeCreation) {
        this.anneeCreation = anneeCreation;
    }

    public Integer getPayant() {
        return payant;
    }

    public void setPayant(Integer payant) {
        this.payant = payant;
    }

    public Integer getGratuit() {
        return gratuit;
    }

    public void setGratuit(Integer gratuit) {
        this.gratuit = gratuit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getIndividuel() {
        return individuel;
    }

    public void setIndividuel(Integer individuel) {
        this.individuel = individuel;
    }

    public Integer getScolaires() {
        return scolaires;
    }

    public void setScolaires(Integer scolaires) {
        this.scolaires = scolaires;
    }

    public Integer getGroupesHorsScolaires() {
        return groupesHorsScolaires;
    }

    public void setGroupesHorsScolaires(Integer groupesHorsScolaires) {
        this.groupesHorsScolaires = groupesHorsScolaires;
    }

    public Integer getMoins18AnsHorsScolaires() {
        return moins18AnsHorsScolaires;
    }

    public void setMoins18AnsHorsScolaires(Integer moins18AnsHorsScolaires) {
        this.moins18AnsHorsScolaires = moins18AnsHorsScolaires;
    }

    public Integer getDe18A25Ans() {
        return de18A25Ans;
    }

    public void setDe18A25Ans(Integer de18A25Ans) {
        this.de18A25Ans = de18A25Ans;
    }

    @Override
    public String toString() {
        return "Carte: " + nomOfficiel + " (" + ville + ") - Type: " + domaineThematique;
    }

    public Rarete getRarete() {
        return rarete;
    }

    public void setRarete(Rarete rarete) {
        this.rarete = rarete;
    }
}