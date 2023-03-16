package root;

import root.utils.Organization;

public class Company {
    private String nom;
    private String adresse;

    public Company(){

    }
    public Company(String nom){

    }

    public String getNomOrganization() {
        return nom;
    }

    public void setNomOrganization(String nomOrganization) {
        this.nom = nomOrganization;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}
