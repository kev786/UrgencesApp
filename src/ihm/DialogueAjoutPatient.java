package ihm;

import metier.Patient;
import metier.Priorite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

public class DialogueAjoutPatient extends JDialog {
    private final JTextField txtNom = new JTextField(15);
    private final JTextField txtPrenom = new JTextField(15);
    private final JSpinner spinnerAge = new JSpinner(new SpinnerNumberModel(30, 0, 150, 1));
    private final JComboBox<String> comboGenre = new JComboBox<>(new String[]{"M", "F"});
    private final JComboBox<String> comboPriorite = new JComboBox<>(
            new String[]{"ROUGE", "ORANGE", "VERT"});
    private final JTextField txtPathologie = new JTextField(20);
    private Patient patient;

    public DialogueAjoutPatient(Frame parent) {
        super(parent, "Nouveau patient", true);
        initComposants();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComposants() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        panel.add(txtNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Prenom :"), gbc);
        gbc.gridx = 1;
        panel.add(txtPrenom, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Age :"), gbc);
        gbc.gridx = 1;
        panel.add(spinnerAge, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Sexe :"), gbc);
        gbc.gridx = 1;
        panel.add(comboGenre, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Priorite :"), gbc);
        gbc.gridx = 1;
        panel.add(comboPriorite, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Pathologie :"), gbc);
        gbc.gridx = 1;
        panel.add(txtPathologie, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnOk = new JButton("Ajouter");
        btnOk.addActionListener(e -> valider());
        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        btnPanel.add(btnOk);
        btnPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        add(panel);
    }

    private void valider() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String pathologie = txtPathologie.getText().trim();
        if (nom.isEmpty() || prenom.isEmpty() || pathologie.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int age = (int) spinnerAge.getValue();
        String sexe = (String) comboGenre.getSelectedItem();
        Priorite priorite = Priorite.valueOf((String) comboPriorite.getSelectedItem());
        patient = new Patient(nom, prenom, age, sexe, priorite, pathologie, LocalDateTime.now());
        dispose();
    }

    public Patient getPatient() { return patient; }
}
