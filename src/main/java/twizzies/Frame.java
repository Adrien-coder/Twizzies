package twizzies;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame extends JFrame {

    private JLabel imageLabel;

    public Frame() {
        // Configuration de la fenêtre

        setTitle("Interface avec Image");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel boutonPanel = new JPanel();
        Button button = new Button(this, "Charger image", boutonPanel, "image.png", 10, 30, 2000, 20);
        Button button2 = new Button(this, "Image hsv", boutonPanel, "image.png", 40, 30, 2000, 20);

        add(boutonPanel, BorderLayout.NORTH);

        this.getContentPane().setBackground(new Color(230, 223, 204));
        // Affichage de la fenêtre
        setVisible(true);
    }

}
