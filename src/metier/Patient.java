package metier;

import java.time.LocalDateTime;

public class Patient implements Comparable<Patient> {
    private int idPatient;
    private String nom;
    private String prenom;
    private int age;
    private String sexe;
    private Priorite codePriorite;
    private String pathologie;
    private LocalDateTime heureArrivee;
    private String statut;
    private LocalDateTime heurePriseEnCharge;
    private LocalDateTime heureSortie;

    public Patient() {}

    public Patient(String nom, String prenom, int age, String sexe,
                   Priorite codePriorite, String pathologie,
                   LocalDateTime heureArrivee) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.sexe = sexe;
        this.codePriorite = codePriorite;
        this.pathologie = pathologie;
        this.heureArrivee = heureArrivee;
        this.statut = "EN_ATTENTE";
    }

    @Override
    public int compareTo(Patient autre) {
        int cmp = Integer.compare(autre.codePriorite.getNiveau(),
                                   this.codePriorite.getNiveau());
        if (cmp != 0) return cmp;
        return this.heureArrivee.compareTo(autre.heureArrivee);
    }

    public int getIdPatient() { return idPatient; }
    public void setIdPatient(int idPatient) { this.idPatient = idPatient; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public Priorite getCodePriorite() { return codePriorite; }
    public void setCodePriorite(Priorite codePriorite) { this.codePriorite = codePriorite; }
    public String getPathologie() { return pathologie; }
    public void setPathologie(String pathologie) { this.pathologie = pathologie; }
    public LocalDateTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalDateTime heureArrivee) { this.heureArrivee = heureArrivee; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getHeurePriseEnCharge() { return heurePriseEnCharge; }
    public void setHeurePriseEnCharge(LocalDateTime heurePriseEnCharge) { this.heurePriseEnCharge = heurePriseEnCharge; }
    public LocalDateTime getHeureSortie() { return heureSortie; }
    public void setHeureSortie(LocalDateTime heureSortie) { this.heureSortie = heureSortie; }
}
