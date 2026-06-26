package ihm;

import metier.Patient;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableModelPatient extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Nom", "Prenom", "Age", "Sexe",
            "Priorite", "Pathologie", "Arrivee", "Statut"};
    private List<Patient> patients;

    public TableModelPatient() {
        this.patients = new ArrayList<>();
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
        fireTableDataChanged();
    }

    public Patient getPatientAt(int row) {
        if (row >= 0 && row < patients.size()) return patients.get(row);
        return null;
    }

    @Override
    public int getRowCount() { return patients.size(); }

    @Override
    public int getColumnCount() { return colonnes.length; }

    @Override
    public String getColumnName(int col) { return colonnes[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Patient p = patients.get(row);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        return switch (col) {
            case 0 -> p.getIdPatient();
            case 1 -> p.getNom();
            case 2 -> p.getPrenom();
            case 3 -> p.getAge();
            case 4 -> p.getSexe();
            case 5 -> p.getCodePriorite().name();
            case 6 -> p.getPathologie();
            case 7 -> p.getHeureArrivee() != null ? p.getHeureArrivee().format(fmt) : "";
            case 8 -> p.getStatut();
            default -> "";
        };
    }
}
