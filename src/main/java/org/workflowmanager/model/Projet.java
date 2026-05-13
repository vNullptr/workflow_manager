package org.workflowmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.workflowmanager.enums.StatutProjet;

public class Projet {

    private int id;
    private String nom;
    private String description;
    private LocalDate dateLimite;
    private StatutProjet statut;
    private Admin admin;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public Projet(int id, String nom, String description, LocalDate dateLimite, StatutProjet statut, Admin admin, LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateLimite = dateLimite;
        this.statut = statut;
        this.admin = admin;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public LocalDate getDateLimite() { return dateLimite; }
    public StatutProjet getStatut() { return statut; }
    public Admin getAdmin() { return admin; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }

    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }
    public void setStatut(StatutProjet statut) { this.statut = statut; }
    public void setAdmin(Admin admin) { this.admin = admin; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
