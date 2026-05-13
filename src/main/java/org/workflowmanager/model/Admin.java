package org.workflowmanager.model;

import org.workflowmanager.enums.Role;

public class Admin extends Utilisateur {

    public Admin(int id, String nom, String email, String motDePasse, String departement) {
        super(id, nom, email, motDePasse, Role.ADMIN, departement);
    }

}
