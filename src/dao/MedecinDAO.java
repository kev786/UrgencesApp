package dao;

import connexion.ConnexionDB;
import metier.Medecin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

    public List<Medecin> getAllMedecins() {
        List<Medecin> liste = new ArrayList<>();
        String sql = "SELECT * FROM medecins";
        try (Connection conn = ConnexionDB.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medecin m = new Medecin();
                m.setIdMedecin(rs.getInt("id_medecin"));
                m.setNom(rs.getString("nom"));
                m.setPrenom(rs.getString("prenom"));
                m.setSpecialite(rs.getString("specialite"));
                m.setDisponible(rs.getBoolean("disponible"));
                liste.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllMedecins : " + e.getMessage());
        }
        return liste;
    }

    public List<Medecin> getMedecinsDisponibles() {
        List<Medecin> liste = new ArrayList<>();
        String sql = "SELECT * FROM medecins WHERE disponible = TRUE";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medecin m = new Medecin();
                m.setIdMedecin(rs.getInt("id_medecin"));
                m.setNom(rs.getString("nom"));
                m.setPrenom(rs.getString("prenom"));
                m.setSpecialite(rs.getString("specialite"));
                m.setDisponible(true);
                liste.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getMedecinsDisponibles : " + e.getMessage());
        }
        return liste;
    }
}
