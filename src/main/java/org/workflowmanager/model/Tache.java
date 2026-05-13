package org.workflowmanager.model;

import org.workflowmanager.enums.Priorite;
import org.workflowmanager.enums.StatutTache;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Tache {

    private int id;
    private String titre;
    private String description;
    private Priorite priorite;
    private StatutTache statut;
    private LocalDate dateLimite;
    private Projet projet;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public Tache(int id, String titre, String description, Priorite priorite, StatutTache statut, LocalDate dateLimite, Projet projet, LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.statut = statut;
        this.dateLimite = dateLimite;
        this.projet = projet;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public Priorite getPriorite() { return priorite; }
    public StatutTache getStatut() { return statut; }
    public LocalDate getDateLimite() { return dateLimite; }
    public Projet getProjet() { return projet; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite; }
    public void setStatut(StatutTache statut) { this.statut = statut; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
