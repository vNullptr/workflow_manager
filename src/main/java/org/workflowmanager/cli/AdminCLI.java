package org.workflowmanager.cli;

import org.workflowmanager.dao.AffectationDAO;
import org.workflowmanager.dao.ProjetDAO;
import org.workflowmanager.dao.TacheDAO;
import org.workflowmanager.dao.UtilisateurDAO;
import org.workflowmanager.enums.Priorite;
import org.workflowmanager.enums.Role;
import org.workflowmanager.model.Admin;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Projet;
import org.workflowmanager.model.Tache;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class AdminCLI {

    private final Admin admin;
    private final Scanner scanner;
    private final ProjetDAO projetDAO = new ProjetDAO();
    private final TacheDAO tacheDAO = new TacheDAO();
    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public AdminCLI(Admin admin, Scanner scanner) {
        this.admin = admin;
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
            System.out.println("=== Menu Admin === [" + admin.getNom() + "]");
            System.out.println("1.  Créer un projet");
            System.out.println("2.  Supprimer un projet");
            System.out.println("3.  Voir tous les projets");
            System.out.println("4.  Ajouter un membre à un projet");
            System.out.println("5.  Voir les membres d'un projet");
            System.out.println("6.  Ajouter une tâche");
            System.out.println("7.  Supprimer une tâche");
            System.out.println("8.  Assigner une tâche");
            System.out.println("9.  Créer un employé");
            System.out.println("10. Voir tous les employés");
            System.out.println("11. Modifier mon profil");
            System.out.println("0.  Se déconnecter");
            System.out.print("Choix: ");

            String choix = scanner.nextLine();
            switch (choix) {
                case "1"  -> creerProjet();
                case "2"  -> supprimerProjet();
                case "3"  -> voirProjets();
                case "4"  -> ajouterMembre();
                case "5"  -> voirMembres();
                case "6"  -> ajouterTache();
                case "7"  -> supprimerTache();
                case "8"  -> assignerTache();
                case "9"  -> creerEmploye();
                case "10" -> voirEmployes();
                case "11" -> modifierProfil();
                case "0"  -> running = false;
                default   -> System.out.println("Choix invalide.");
            }
            if (running) pause();
        }
    }

    private void creerProjet() throws SQLException {
        System.out.print("Nom du projet: ");
        String nom = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Date limite (YYYY-MM-DD): ");
        LocalDate dateLimite = LocalDate.parse(scanner.nextLine());

        Projet projet = projetDAO.create(nom, description, dateLimite, admin);
        System.out.println("Projet créé : " + projet.getNom());
    }

    private void supprimerProjet() throws SQLException {
        System.out.print("ID du projet à supprimer: ");
        int id = Integer.parseInt(scanner.nextLine());
        projetDAO.delete(id);
        System.out.println("Projet supprimé.");
    }

    private void voirProjets() throws SQLException {
        List<Projet> projets = projetDAO.findAll();
        if (projets.isEmpty()) {
            System.out.println("Aucun projet.");
            return;
        }
        for (Projet p : projets) {
            System.out.println("[" + p.getId() + "] " + p.getNom() + " - " + p.getStatut() + " - " + p.getDateLimite());
        }
    }

    private void ajouterMembre() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        System.out.print("ID de l'employé: ");
        int idEmploye = Integer.parseInt(scanner.nextLine());
        projetDAO.addMembre(idProjet, idEmploye);
        System.out.println("Membre ajouté.");
    }

    private void voirMembres() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        List<Employe> membres = projetDAO.getMembres(idProjet);
        if (membres.isEmpty()) {
            System.out.println("Aucun membre.");
            return;
        }
        for (Employe e : membres) {
            System.out.println("[" + e.getId() + "] " + e.getNom() + " - " + e.getPoste());
        }
    }

    private void ajouterTache() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        Projet projet = projetDAO.findById(idProjet);
        if (projet == null) { System.out.println("Projet introuvable."); return; }

        System.out.print("Titre: ");
        String titre = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Priorité (BAS/MOYEN/HAUT): ");
        Priorite priorite = Priorite.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Date limite (YYYY-MM-DD): ");
        Date dateLimite = Date.valueOf(scanner.nextLine());

        Tache tache = tacheDAO.create(titre, description, priorite, dateLimite, projet);
        System.out.println("Tâche créée : " + tache.getTitre());
    }

    private void supprimerTache() throws SQLException {
        System.out.print("ID de la tâche à supprimer: ");
        int id = Integer.parseInt(scanner.nextLine());
        tacheDAO.delete(id);
        System.out.println("Tâche supprimée.");
    }

    private void assignerTache() throws SQLException {
        System.out.print("ID du projet: ");
        int idProjet = Integer.parseInt(scanner.nextLine());
        Projet projet = projetDAO.findById(idProjet);
        if (projet == null) { System.out.println("Projet introuvable."); return; }

        System.out.print("ID de la tâche: ");
        int idTache = Integer.parseInt(scanner.nextLine());
        Tache tache = tacheDAO.findById(idTache, projet);
        if (tache == null) { System.out.println("Tâche introuvable."); return; }

        System.out.print("ID de l'employé: ");
        int idEmploye = Integer.parseInt(scanner.nextLine());
        Employe employe = (Employe) utilisateurDAO.findById(idEmploye);
        if (employe == null) { System.out.println("Employé introuvable."); return; }

        System.out.print("Note (optionnel, appuyez sur Entrée pour ignorer): ");
        String note = scanner.nextLine();
        if (note.isBlank()) {
            affectationDAO.create(tache, employe);
        } else {
            affectationDAO.create(tache, employe, note);
        }
        System.out.println("Tâche assignée.");
    }

    private void creerEmploye() throws SQLException {
        System.out.print("Nom: ");
        String nom = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String motDePasse = scanner.nextLine();
        System.out.print("Département: ");
        String departement = scanner.nextLine();
        System.out.print("Poste: ");
        String poste = scanner.nextLine();

        utilisateurDAO.create(nom, email, motDePasse, Role.EMPLOYE, departement, poste);
        System.out.println("Employé créé.");
    }

    private void voirEmployes() throws SQLException {
        List<Employe> employes = utilisateurDAO.findAllEmployes();
        if (employes.isEmpty()) {
            System.out.println("Aucun employé.");
            return;
        }
        for (Employe e : employes) {
            System.out.println("[" + e.getId() + "] " + e.getNom() + " - " + e.getPoste() + " - " + e.getDepartement());
        }
    }

    private void modifierProfil() throws SQLException {
        System.out.print("Nouveau nom: ");
        String nom = scanner.nextLine();
        System.out.print("Nouvel email: ");
        String email = scanner.nextLine();
        utilisateurDAO.update(nom, email, admin.getId());
        System.out.println("Profil mis à jour.");
    }
}
