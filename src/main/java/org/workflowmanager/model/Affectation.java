package org.workflowmanager.model;

import java.time.LocalDate;

public class Affectation {

    private int id;
    private LocalDate dateAffectation;
    private String note;
    private Tache tache;
    private Employe employe;

    public Affectation(int id, LocalDate dateAffectation, String note, Tache tache, Employe employe) {
        this.id = id;
        this.dateAffectation = dateAffectation;
        this.note = note;
        this.tache = tache;
        this.employe = employe;
    }

    public int getId() { return id; }
    public LocalDate getDateAffectation() { return dateAffectation; }
    public String getNote() { return note; }
    public Tache getTache() { return tache; }
    public Employe getEmploye() { return employe; }

    public void setNote(String note) { this.note = note; }
}
