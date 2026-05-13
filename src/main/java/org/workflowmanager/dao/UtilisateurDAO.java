package org.workflowmanager.dao;

import org.workflowmanager.enums.Role;
import org.workflowmanager.model.Admin;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Utilisateur;
import org.workflowmanager.service.AuthService;
import org.workflowmanager.service.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UtilisateurDAO {

    private final Connection conn = DatabaseService.getInstance().getConn();

    public Utilisateur addUtilisateur(String nom, String email, String motDePasse, Role role, String departement, String poste) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Utilisateur (nom, email, motDePasse, role, departement, poste) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        );

        String hashed_pass = AuthService.hacherMotDePasse(motDePasse);

        stmt.setString(1, nom);
        stmt.setString(2, email);
        stmt.setString(3, hashed_pass);
        stmt.setString(4, role.toString());
        stmt.setString(5, departement);
        stmt.setString(6, poste);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();

        if (rs.next()) {
            int id = rs.getInt(1);
            if (role == Role.ADMIN) {
                return new Admin(id, nom, email, hashed_pass, departement);
            } else {
                return new Employe(id, nom, email, hashed_pass, departement, poste);
            }
        }
        return null;
    }

    public Utilisateur findByEmail(String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT id, nom, email, motDePasse, role, departement, poste FROM Utilisateur WHERE email = ?"
        );
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapResultSet(rs);
        }
        return null;
    }

    public void updateProfil(String nom, String email, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE Utilisateur SET nom = ?, email = ? WHERE id = ?"
        );
        stmt.setString(1, nom);
        stmt.setString(2, email);
        stmt.setInt(3, id);
        stmt.executeUpdate();
    }

    private Utilisateur mapResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String email = rs.getString("email");
        String motDePasse = rs.getString("motDePasse");
        Role role = Role.valueOf(rs.getString("role"));
        String departement = rs.getString("departement");
        String poste = rs.getString("poste");

        if (role == Role.ADMIN) {
            return new Admin(id, nom, email, motDePasse, departement);
        } else {
            return new Employe(id, nom, email, motDePasse, departement, poste);
        }
    }
}
