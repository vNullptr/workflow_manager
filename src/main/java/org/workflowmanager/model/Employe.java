package org.workflowmanager.model;

import org.workflowmanager.enums.Role;

public class Employe extends Utilisateur {

    private String poste;

    public Employe(int id, String nom, String email, String motDePasse, String departement, String poste) {
        super(id, nom, email, motDePasse, Role.EMPLOYE, departement);
        this.poste = poste;
    }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
}
