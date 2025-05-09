package twizzies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class Frame extends JFrame {

    private JLabel imageLabel;
    // variable temporaire
    private String image = "ref\\ref50.jpg";

    public Frame() {
        // Configuration de la fenêtre

        setTitle("Interface avec Image");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel boutonPanel = new JPanel();

        Mat matrice = TraitementImage.readImage("ref\\ref50.jpg");

        Vector<Mat> matrices = TraitementImage.transposeToHSV(matrice);

        Button button = new Button(this, "Charger image", boutonPanel, matrice, 10, 30, 2000, 20);
        Button button2 = new Button(this, "Image hsv", boutonPanel, matrices, 40, 30, 2000, 20);
        add(boutonPanel, BorderLayout.NORTH);

        this.getContentPane().setBackground(new Color(230, 223, 204));
        // Affichage de la fenêtre
        setVisible(true);
    }

}
