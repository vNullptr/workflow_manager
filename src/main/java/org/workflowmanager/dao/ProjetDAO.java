package org.workflowmanager.dao;

import org.workflowmanager.enums.Role;
import org.workflowmanager.enums.StatutProjet;
import org.workflowmanager.model.Admin;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Projet;
import org.workflowmanager.model.Utilisateur;
import org.workflowmanager.service.DatabaseService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {

    private final Connection conn = DatabaseService.getInstance().getConn();

    public Projet create(String nom, String description, LocalDate dateLimite, Admin admin) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Projet (nom, description, dateLimite, statut, idAdmin) VALUES (?, ?, ?, 'PLANIFIE', ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, nom);
        stmt.setString(2, description);
        stmt.setDate(3, Date.valueOf(dateLimite));
        stmt.setInt(4, admin.getId());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            return new Projet(id, nom, description, dateLimite, StatutProjet.PLANIFIE, admin, LocalDateTime.now(), LocalDateTime.now());
        }
        return null;
    }

    public void delete(int idProjet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM Projet WHERE id = ?"
        );
        stmt.setInt(1, idProjet);
        stmt.executeUpdate();
    }

    public List<Projet> findAll() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Projet"
        );
        return mapList(stmt.executeQuery());
    }

    public List<Projet> findByAdmin(int idAdmin) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Projet WHERE idAdmin = ?"
        );
        stmt.setInt(1, idAdmin);
        return mapList(stmt.executeQuery());
    }

    public void addMembre(int idProjet, int idEmploye) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO ProjetMembre (idProjet, idEmploye) VALUES (?, ?)"
        );
        stmt.setInt(1, idProjet);
        stmt.setInt(2, idEmploye);
        stmt.executeUpdate();
    }

    public List<Employe> getMembres(int idProjet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT u.id, u.nom, u.email, u.motDePasse, u.departement, u.poste " +
            "FROM Utilisateur u JOIN ProjetMembre pm ON u.id = pm.idEmploye " +
            "WHERE pm.idProjet = ?"
        );
        stmt.setInt(1, idProjet);
        ResultSet rs = stmt.executeQuery();

        List<Employe> membres = new ArrayList<>();
        while (rs.next()) {
            membres.add(new Employe(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("email"),
                rs.getString("motDePasse"),
                rs.getString("departement"),
                rs.getString("poste")
            ));
        }
        return membres;
    }

    private List<Projet> mapList(ResultSet rs) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

        while (rs.next()) {
            int idAdmin = rs.getInt("idAdmin");
            Admin admin = (Admin) utilisateurDAO.findById(idAdmin);

            projets.add(new Projet(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getDate("dateLimite").toLocalDate(),
                StatutProjet.valueOf(rs.getString("statut")),
                admin,
                rs.getTimestamp("dateCreation").toLocalDateTime(),
                rs.getTimestamp("dateModification").toLocalDateTime()
            ));
        }
        return projets;
    }
}
