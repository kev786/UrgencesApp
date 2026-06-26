import ihm.FenetrePrincipale;

import javax.swing.*;

public class Main {
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
