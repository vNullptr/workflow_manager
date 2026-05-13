package org.workflowmanager.model;

import org.workflowmanager.enums.Role;

public abstract class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motDePasse;
    private Role role;
    private String departement;

    public Utilisateur(int id, String nom, String email, String motDePasse, Role role, String departement) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.departement = departement;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getDepartement() { return departement; }

    public void setNom(String nom) { this.nom = nom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getMotDePasse() { return motDePasse; }

}
