package metier;

public class Medecin {
    private int idMedecin;
    private String nom;
    private String prenom;
    private String specialite;
    private boolean disponible;

    public Medecin() {}

    public Medecin(String nom, String prenom, String specialite, boolean disponible) {
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.disponible = disponible;
    }

    public int getIdMedecin() { return idMedecin; }
    public void setIdMedecin(int idMedecin) { this.idMedecin = idMedecin; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
