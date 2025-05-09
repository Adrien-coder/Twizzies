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

        // ajout du panel pour l'image
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 223, 204));

        panel.setBounds(70, 70, 100, 100);

        Mat matrice = TraitementImage.readImage("panneaux\\p10.jpg");
        Mat matrice2 = TraitementImage.readImage("panneaux\\p10.jpg");
        Vector<Mat> matricesHSV = TraitementImage.transposeToHSV(matrice);
        Vector<Mat> matricesContourRouge = TraitementImage.surroundCircles(matrice2, 0, 10, 160, 180);
        JPanel boutonPanel = new JPanel();
        boutonPanel.setBackground(new Color(230, 223, 204));

        Button button = new Button(this, "Charger image", boutonPanel, panel, matrice, 10, 30, 2000, 20);
        Button button2 = new Button(this, "Image hsv", boutonPanel, panel, matricesHSV, 40, 30, 2000, 20);
        Button button3 = new Button(this, "Extraction des contours", boutonPanel, panel, matricesContourRouge, 60, 30,
                2000, 20);
        add(boutonPanel, BorderLayout.NORTH);

        this.getContentPane().setBackground(new Color(230, 223, 204));
        // Affichage de la fenêtre
        setVisible(true);
    }

}
