package org.workflowmanager.dao;

import org.workflowmanager.model.Affectation;
import org.workflowmanager.model.Employe;
import org.workflowmanager.model.Tache;
import org.workflowmanager.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AffectationDAO {

    private final Connection conn = DatabaseService.getInstance().getConn();

    public Affectation create(Tache tache, Employe employe) throws SQLException {
        return insert(tache, employe, null);
    }

    public Affectation create(Tache tache, Employe employe, String note) throws SQLException {
        return insert(tache, employe, note);
    }

    public void delete(int idTache, int idEmploye) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM Affectation WHERE idTache = ? AND idEmploye = ?"
        );
        stmt.setInt(1, idTache);
        stmt.setInt(2, idEmploye);
        stmt.executeUpdate();
    }

    public List<Affectation> findByEmploye(Employe employe, TacheDAO tacheDAO, ProjetDAO projetDAO) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT a.id, a.dateAffectation, a.note, a.idTache " +
            "FROM Affectation a WHERE a.idEmploye = ?"
        );
        stmt.setInt(1, employe.getId());
        ResultSet rs = stmt.executeQuery();

        List<Affectation> affectations = new ArrayList<>();
        while (rs.next()) {
            int idProjet = getIdProjetFromTache(rs.getInt("idTache"));
            Tache tache = tacheDAO.findById(rs.getInt("idTache"), projetDAO.findById(idProjet));
            affectations.add(new Affectation(
                rs.getInt("id"),
                rs.getDate("dateAffectation").toLocalDate(),
                rs.getString("note"),
                tache,
                employe
            ));
        }
        return affectations;
    }

    private Affectation insert(Tache tache, Employe employe, String note) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Affectation (dateAffectation, note, idTache, idEmploye) VALUES (CAST(GETDATE() AS DATE), ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, note);
        stmt.setInt(2, tache.getId());
        stmt.setInt(3, employe.getId());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            return new Affectation(id, java.time.LocalDate.now(), note, tache, employe);
        }
        return null;
    }

    private int getIdProjetFromTache(int idTache) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT idProjet FROM Tache WHERE id = ?"
        );
        stmt.setInt(1, idTache);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("idProjet");
        }
        throw new SQLException("Tache introuvable : " + idTache);
    }
}
