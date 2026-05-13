package org.workflowmanager.dao;

import org.workflowmanager.enums.Priorite;
import org.workflowmanager.enums.StatutTache;
import org.workflowmanager.model.Projet;
import org.workflowmanager.model.Tache;
import org.workflowmanager.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {

    private final Connection conn = DatabaseService.getInstance().getConn();

    public Tache create(String titre, String description, Priorite priorite, Date dateLimite, Projet projet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Tache (titre, description, priorite, statut, dateLimite, idProjet) VALUES (?, ?, ?, 'A_FAIRE', ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, titre);
        stmt.setString(2, description);
        stmt.setString(3, priorite.toString());
        stmt.setDate(4, dateLimite);
        stmt.setInt(5, projet.getId());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            return new Tache(id, titre, description, priorite, StatutTache.A_FAIRE, dateLimite.toLocalDate(), projet, null, null);
        }
        return null;
    }

    public void delete(int idTache) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM Tache WHERE id = ?"
        );
        stmt.setInt(1, idTache);
        stmt.executeUpdate();
    }

    public Tache findById(int idTache, Projet projet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Tache WHERE id = ?"
        );
        stmt.setInt(1, idTache);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapResultSet(rs, projet);
        }
        return null;
    }

    public List<Tache> findByProjet(Projet projet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM Tache WHERE idProjet = ?"
        );
        stmt.setInt(1, projet.getId());
        return mapList(stmt.executeQuery(), projet);
    }

    public List<Tache> findByEmploye(int idEmploye, Projet projet) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT t.* FROM Tache t JOIN Affectation a ON t.id = a.idTache WHERE a.idEmploye = ?"
        );
        stmt.setInt(1, idEmploye);
        return mapList(stmt.executeQuery(), projet);
    }

    public void updateStatut(StatutTache statut, int idTache) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE Tache SET statut = ? WHERE id = ?"
        );
        stmt.setString(1, statut.toString());
        stmt.setInt(2, idTache);
        stmt.executeUpdate();
    }

    public void update(Tache tache) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE Tache SET titre = ?, description = ?, priorite = ?, statut = ?, dateLimite = ?, idProjet = ?, dateModification = GETDATE() WHERE id = ?"
        );
        stmt.setString(1, tache.getTitre());
        stmt.setString(2, tache.getDescription());
        stmt.setString(3, tache.getPriorite().toString());
        stmt.setString(4, tache.getStatut().toString());
        stmt.setDate(5, Date.valueOf(tache.getDateLimite()));
        stmt.setInt(6, tache.getProjet().getId());
        stmt.setInt(7, tache.getId());
        stmt.executeUpdate();
    }

    private Tache mapResultSet(ResultSet rs, Projet projet) throws SQLException {
        return new Tache(
            rs.getInt("id"),
            rs.getString("titre"),
            rs.getString("description"),
            Priorite.valueOf(rs.getString("priorite")),
            StatutTache.valueOf(rs.getString("statut")),
            rs.getDate("dateLimite").toLocalDate(),
            projet,
            rs.getTimestamp("dateCreation").toLocalDateTime(),
            rs.getTimestamp("dateModification").toLocalDateTime()
        );
    }

    private List<Tache> mapList(ResultSet rs, Projet projet) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        while (rs.next()) {
            taches.add(mapResultSet(rs, projet));
        }
        return taches;
    }
}
