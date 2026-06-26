package ihm;

import dao.ArchivesDAO;
import dao.PatientDAO;
import metier.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class FenetrePrincipale extends JFrame {
    private final PatientDAO patientDAO = new PatientDAO();
    private final ArchivesDAO archivesDAO = new ArchivesDAO();
    private final TableModelPatient tableModel = new TableModelPatient();
    private final JTable table = new JTable(tableModel);
    private final JLabel labelTempsAttente = new JLabel("Temps d'attente moyen : 0.0 min");
    private final JLabel labelHorloge = new JLabel();
    private Timer refreshTimer;

    public FenetrePrincipale() {
        setTitle("Gestion des Urgences Hospitalieres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        initComposants();
        chargerDonnees();
        demarrerHorloge();
        demarrerAutoRefresh();
    }

    private void initComposants() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        labelHorloge.setFont(new Font("Monospaced", Font.BOLD, 16));
        labelHorloge.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(labelHorloge, BorderLayout.WEST);
        topPanel.add(labelTempsAttente, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new TitledBorder("Patients en attente"));
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setRowHeight(24);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnAppeler = new JButton("Appeler prochain patient");
        btnAppeler.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAppeler.setBackground(new Color(220, 80, 80));
        btnAppeler.setForeground(Color.WHITE);
        btnAppeler.addActionListener(e -> appelerProchain());

        JButton btnArchiver = new JButton("Archiver patient selectionne");
        btnArchiver.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnArchiver.addActionListener(e -> archiverSelectionne());

        JButton btnAjouter = new JButton("Nouveau patient");
        btnAjouter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAjouter.addActionListener(e -> ouvrirDialogueAjout());

        JButton btnRafraichir = new JButton("Rafraichir");
        btnRafraichir.addActionListener(e -> chargerDonnees());

        controlPanel.add(btnAppeler);
        controlPanel.add(btnArchiver);
        controlPanel.add(btnAjouter);
        controlPanel.add(btnRafraichir);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void chargerDonnees() {
        List<Patient> enAttente = patientDAO.getPatientsEnAttente();
        tableModel.setPatients(enAttente);
    }

    private void appelerProchain() {
        Patient prochain = patientDAO.appelerProchainPatient();
        if (prochain != null) {
            JOptionPane.showMessageDialog(this,
                    "Patient appele : " + prochain.getPrenom() + " " + prochain.getNom()
                            + " (" + prochain.getCodePriorite() + ")",
                    "Prochain patient", JOptionPane.INFORMATION_MESSAGE);
            chargerDonnees();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Aucun patient en attente.",
                    "File d'attente vide", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void archiverSelectionne() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez selectionner un patient dans le tableau.",
                    "Selection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Patient p = tableModel.getPatientAt(row);
        if (p != null) {
            patientDAO.archiverPatient(p.getIdPatient());
            archivesDAO.archiverDossier(p);
            chargerDonnees();
        }
    }

    private void ouvrirDialogueAjout() {
        DialogueAjoutPatient dialogue = new DialogueAjoutPatient(this);
        dialogue.setVisible(true);
        Patient nouveau = dialogue.getPatient();
        if (nouveau != null) {
            patientDAO.ajouterPatient(nouveau);
            chargerDonnees();
        }
    }

    private void demarrerHorloge() {
        thread.HorlogeInterne horloge = new thread.HorlogeInterne(labelTempsAttente, patientDAO);
        horloge.execute();

        Timer clockTimer = new Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            labelHorloge.setText(" " + now.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " ");
        });
        clockTimer.start();
    }

    private void demarrerAutoRefresh() {
        refreshTimer = new Timer(10000, e -> chargerDonnees());
        refreshTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // look and feel par defaut
            }
            new FenetrePrincipale().setVisible(true);
        });
    }
}
