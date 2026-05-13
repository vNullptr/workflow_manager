package org.workflowmanager.service;

import org.mindrot.jbcrypt.BCrypt;
import org.workflowmanager.dao.UtilisateurDAO;
import org.workflowmanager.model.Utilisateur;

import java.sql.SQLException;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur seConnecter(String email, String motDePasse) throws SQLException {
        Utilisateur utilisateur = utilisateurDAO.findByEmail(email);

        if (utilisateur == null) {
            return null;
        }

        if (!BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())) {
            return null;
        }

        return utilisateur;
    }

    public static String hacherMotDePasse(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
    }
}
