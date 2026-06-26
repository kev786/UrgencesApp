package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion_urgences";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection instance;

    private ConnexionDB() {}

    public static Connection getConnexion() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

    public static void testerConnexion() {
        try (Connection conn = getConnexion()) {
            System.out.println("Connexion reussie a : " + conn.getCatalog());
            System.out.println("Produit : " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Version : " + conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("Echec de connexion : " + e.getMessage());
            System.err.println("Code SQL : " + e.getSQLState());
        }
    }

    public static void main(String[] args) {
        testerConnexion();
    }
}
