package twizzies;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame extends JFrame {

    private JLabel imageLabel;

    public Frame() {
        // Configuration de la fenêtre
        setTitle("Interface avec Image");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel boutonPanel = new JPanel();
        Button button = new Button(this, "Charger image", boutonPanel, "image.png", 3, 3, 600, 400);

        // Ajout des composants à la fenêtre
        add(boutonPanel, BorderLayout.EAST);

        // Affichage de la fenêtre
        setVisible(true);
    }

}
