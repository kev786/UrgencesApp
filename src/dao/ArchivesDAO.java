package dao;

import connexion.ConnexionDB;
import metier.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArchivesDAO {

    public void archiverDossier(Patient p) {
        String insert = "INSERT INTO archives_dossiers " +
                "(id_patient, nom, prenom, code_priorite, pathologie, heure_arrivee, heure_sortie) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, p.getIdPatient());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getPrenom());
            ps.setString(4, p.getCodePriorite().name());
            ps.setString(5, p.getPathologie());
            ps.setTimestamp(6, Timestamp.valueOf(p.getHeureArrivee()));
            ps.setTimestamp(7, Timestamp.valueOf(p.getHeureSortie() != null
                    ? p.getHeureSortie() : LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur archiverDossier : " + e.getMessage());
        }
    }

    public List<Patient> getAllArchives() {
        List<Patient> liste = new ArrayList<>();
        String sql = "SELECT * FROM archives_dossiers ORDER BY date_archivage DESC";
        try (Connection conn = ConnexionDB.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Patient p = new Patient();
                p.setIdPatient(rs.getInt("id_patient"));
                p.setNom(rs.getString("nom"));
                p.setPrenom(rs.getString("prenom"));
                p.setCodePriorite(metier.Priorite.valueOf(rs.getString("code_priorite")));
                p.setPathologie(rs.getString("pathologie"));
                Timestamp ta = rs.getTimestamp("heure_arrivee");
                if (ta != null) p.setHeureArrivee(ta.toLocalDateTime());
                Timestamp hs = rs.getTimestamp("heure_sortie");
                if (hs != null) p.setHeureSortie(hs.toLocalDateTime());
                p.setStatut("ARCHIVE");
                liste.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllArchives : " + e.getMessage());
        }
        return liste;
    }
}
