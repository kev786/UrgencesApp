package thread;

import dao.PatientDAO;

import javax.swing.*;
import java.text.DecimalFormat;

public class HorlogeInterne extends SwingWorker<Void, String> {

    private final JLabel labelTempsAttente;
    private final PatientDAO patientDAO;
    private volatile boolean actif;

    public HorlogeInterne(JLabel labelTempsAttente, PatientDAO patientDAO) {
        this.labelTempsAttente = labelTempsAttente;
        this.patientDAO = patientDAO;
        this.actif = true;
    }

    public void arreter() {
        actif = false;
    }

    @Override
    protected Void doInBackground() {
        DecimalFormat df = new DecimalFormat("#0.0");
        while (actif) {
            double moyenne = patientDAO.getTempsAttenteMoyenMinutes();
            String texte = "Temps d'attente moyen : " + df.format(moyenne) + " min";
            publish(texte);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        String dernier = chunks.get(chunks.size() - 1);
        labelTempsAttente.setText(dernier);
    }
}
