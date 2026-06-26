package dao;

import connexion.ConnexionDB;
import metier.Patient;
import metier.Priorite;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PatientDAO {

    public List<Patient> getAllPatients() {
        List<Patient> liste = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY " +
                "CASE code_priorite " +
                "  WHEN 'ROUGE' THEN 1 " +
                "  WHEN 'ORANGE' THEN 2 " +
                "  WHEN 'VERT' THEN 3 " +
                "END, heure_arrivee ASC";
        try (Connection conn = ConnexionDB.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(rsToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllPatients : " + e.getMessage());
        }
        return liste;
    }

    public List<Patient> getPatientsEnAttente() {
        List<Patient> liste = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE statut = 'EN_ATTENTE' ORDER BY " +
                "CASE code_priorite " +
                "  WHEN 'ROUGE' THEN 1 " +
                "  WHEN 'ORANGE' THEN 2 " +
                "  WHEN 'VERT' THEN 3 " +
                "END, heure_arrivee ASC";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(rsToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getPatientsEnAttente : " + e.getMessage());
        }
        return liste;
    }

    public PriorityQueue<Patient> getFileAttente() {
        PriorityQueue<Patient> pq = new PriorityQueue<>();
        List<Patient> liste = getPatientsEnAttente();
        pq.addAll(liste);
        return pq;
    }

    public void ajouterPatient(Patient p) {
        String sql = "INSERT INTO patients (nom, prenom, age, sexe, code_priorite, pathologie, heure_arrivee, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'EN_ATTENTE')";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getSexe());
            ps.setString(5, p.getCodePriorite().name());
            ps.setString(6, p.getPathologie());
            ps.setTimestamp(7, Timestamp.valueOf(p.getHeureArrivee()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdPatient(keys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouterPatient : " + e.getMessage());
        }
    }

    public Patient appelerProchainPatient() {
        PriorityQueue<Patient> pq = getFileAttente();
        if (pq.isEmpty()) return null;
        Patient prochain = pq.poll();
        String sql = "UPDATE patients SET statut = 'EN_COURS', heure_prise_en_charge = ? WHERE id_patient = ?";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, prochain.getIdPatient());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur appelerProchainPatient : " + e.getMessage());
        }
        prochain.setStatut("EN_COURS");
        prochain.setHeurePriseEnCharge(LocalDateTime.now());
        return prochain;
    }

    public void archiverPatient(int idPatient) {
        String sql = "UPDATE patients SET statut = 'ARCHIVE', heure_sortie = ? WHERE id_patient = ?";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idPatient);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur archiverPatient : " + e.getMessage());
        }
    }

    public double getTempsAttenteMoyenMinutes() {
        String sql = "SELECT TIMESTAMPDIFF(MINUTE, heure_arrivee, NOW()) AS duree " +
                "FROM patients WHERE statut = 'EN_ATTENTE'";
        double total = 0;
        int count = 0;
        try (Connection conn = ConnexionDB.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                total += rs.getInt("duree");
                count++;
            }
        } catch (SQLException e) {
            System.err.println("Erreur getTempsAttenteMoyen : " + e.getMessage());
        }
        return count > 0 ? total / count : 0.0;
    }

    public List<Patient> getPatientsEnCours() {
        List<Patient> liste = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE statut = 'EN_COURS'";
        try (Connection conn = ConnexionDB.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(rsToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getPatientsEnCours : " + e.getMessage());
        }
        return liste;
    }

    private Patient rsToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setIdPatient(rs.getInt("id_patient"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAge(rs.getInt("age"));
        p.setSexe(rs.getString("sexe"));
        p.setCodePriorite(Priorite.valueOf(rs.getString("code_priorite")));
        p.setPathologie(rs.getString("pathologie"));
        Timestamp ta = rs.getTimestamp("heure_arrivee");
        if (ta != null) p.setHeureArrivee(ta.toLocalDateTime());
        p.setStatut(rs.getString("statut"));
        Timestamp hpc = rs.getTimestamp("heure_prise_en_charge");
        if (hpc != null) p.setHeurePriseEnCharge(hpc.toLocalDateTime());
        Timestamp hs = rs.getTimestamp("heure_sortie");
        if (hs != null) p.setHeureSortie(hs.toLocalDateTime());
        return p;
    }
}
