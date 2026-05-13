package org.workflowmanager;

import org.workflowmanager.cli.AdminCLI;
import org.workflowmanager.cli.EmployeCLI;
import org.workflowmanager.enums.Role;
import org.workflowmanager.model.Admin;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Utilisateur;
import org.workflowmanager.service.AuthService;

import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("=== Workflow Manager ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String motDePasse = scanner.nextLine();

        try {
            Utilisateur utilisateur = authService.seConnecter(email, motDePasse);

            if (utilisateur == null) {
                System.out.println("Email ou mot de passe incorrect.");
                return;
            }

            if (utilisateur.getRole() == Role.ADMIN) {
                new AdminCLI((Admin) utilisateur, scanner).start();
            } else {
                new EmployeCLI((Employe) utilisateur, scanner).start();
            }

        } catch (SQLException e) {
            System.out.println("Erreur de base de données : " + e.getMessage());
        }

        scanner.close();
    }
}
