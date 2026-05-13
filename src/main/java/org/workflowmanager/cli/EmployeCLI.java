package org.workflowmanager.cli;

import org.workflowmanager.dao.AffectationDAO;
import org.workflowmanager.dao.ProjetDAO;
import org.workflowmanager.dao.TacheDAO;
import org.workflowmanager.dao.UtilisateurDAO;
import org.workflowmanager.enums.StatutTache;
import org.workflowmanager.model.Affectation;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Projet;
import org.workflowmanager.model.Tache;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class EmployeCLI {

    private final Employe employe;
    private final Scanner scanner;
    private final TacheDAO tacheDAO = new TacheDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final ProjetDAO projetDAO = new ProjetDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public EmployeCLI(Employe employe, Scanner scanner) {
        this.employe = employe;
        this.scanner = scanner;
    }

    private void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private void pause() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    public void start() throws SQLException {
        boolean running = true;
        while (running) {
            clearConsole();
            System.out.println("=== Menu Employé === [" + employe.getNom() + "]");
            System.out.println("1. Voir mes tâches");
            System.out.println("2. Mettre à jour le statut d'une tâche");
            System.out.println("3. Voir les détails d'une tâche");
            System.out.println("4. Voir les détails d'un projet");
            System.out.println("5. Modifier mon profil");
            System.out.println("0. Se déconnecter");
            System.out.print("Choix: ");

            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> voirMesTaches();
                case "2" -> mettreAJourStatut();
                case "3" -> voirDetailsTache();
                case "4" -> voirDetailsProjet();
                case "5" -> modifierProfil();
                case "0" -> running = false;
                default  -> System.out.println("Choix invalide.");
            }
            if (running) pause();
        }
    }

    private void voirMesTaches() throws SQLException {
        List<Affectation> affectations = affectationDAO.findByEmploye(employe, tacheDAO, projetDAO);
        if (affectations.isEmpty()) {
            System.out.println("Aucune tâche assignée.");
            return;
        }
        for (Affectation a : affectations) {
            Tache t = a.getTache();
            System.out.println("[" + t.getId() + "] " + t.getTitre() + " - " + t.getStatut() + " - " + t.getDateLimite());
        }
    }

    private void mettreAJourStatut() throws SQLException {
        System.out.print("ID de la tâche: ");
        int idTache = Integer.parseInt(scanner.nextLine());
        System.out.print("Nouveau statut (A_FAIRE/EN_COURS/TERMINE): ");
        StatutTache statut = StatutTache.valueOf(scanner.nextLine().toUpperCase());
        tacheDAO.updateStatut(statut, idTache);
        System.out.println("Statut mis à jour.");
    }

    private void voirDetailsTache() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        Projet projet = projetDAO.findById(idProjet);
        if (projet == null) { System.out.println("Projet introuvable."); return; }

        System.out.print("ID de la tâche: ");
        int idTache = Integer.parseInt(scanner.nextLine());
        Tache tache = tacheDAO.findById(idTache, projet);
        if (tache == null) { System.out.println("Tâche introuvable."); return; }

        System.out.println("Titre       : " + tache.getTitre());
        System.out.println("Description : " + tache.getDescription());
        System.out.println("Priorité    : " + tache.getPriorite());
        System.out.println("Statut      : " + tache.getStatut());
        System.out.println("Date limite : " + tache.getDateLimite());
    }

    private void voirDetailsProjet() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        Projet projet = projetDAO.findById(idProjet);
        if (projet == null) { System.out.println("Projet introuvable."); return; }

        System.out.println("Nom         : " + projet.getNom());
        System.out.println("Description : " + projet.getDescription());
        System.out.println("Statut      : " + projet.getStatut());
        System.out.println("Date limite : " + projet.getDateLimite());
        System.out.println("Admin       : " + projet.getAdmin().getNom());
    }

    private void modifierProfil() throws SQLException {
        System.out.print("Nouveau nom: ");
        String nom = scanner.nextLine();
        System.out.print("Nouvel email: ");
        String email = scanner.nextLine();
        utilisateurDAO.update(nom, email, employe.getId());
        System.out.println("Profil mis à jour.");
    }
}
