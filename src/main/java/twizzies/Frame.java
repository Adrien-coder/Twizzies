package twizzies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class Frame extends JFrame {

    private String image;
    JPanel boutonPanel;
    JPanel panel;
    private Mat matrice;
    private Vector<Mat> matricesHSV;
    private Vector<Mat> matricesContourRouge;
    private Vector<Mat> panneauxDetecte;
    private Vector<Mat> panneauxDetecteV2;

    public Frame() {
        // Configuration de la fenêtre

        setTitle("Interface avec Image");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ajout du panel pour l'image
        panel = new JPanel();
        panel.setBackground(new Color(250, 235, 215));
        panel.setBounds(70, 70, 100, 100);
        boutonPanel = new JPanel();

        // on impose une image par defaut si il n'y en a pas
        preparationImage();

        boutonPanel.setBackground(new Color(230, 223, 204));
        BouttonRefresh();

        add(boutonPanel, BorderLayout.NORTH);

        this.getContentPane().setBackground(new Color(230, 223, 204));
        // Affichage de la fenêtre
        setVisible(true);
    }

    public void preparationImage() {
        if (image == null) {
            image = "ref\\ref30.jpg";
        }
        String type = detectByExtension(image);

        if (type.equals("jpg") || type.equals("png")) {

            matrice = TraitementImage.readImage(image);
            matricesHSV = TraitementImage.transposeToHSV(matrice.clone());
            matricesContourRouge = TraitementImage.surroundCircles(matrice.clone(), 0, 10, 160, 180);
            panneauxDetecte = TraitementImage.DetectSign(image);
            panneauxDetecteV2 = TraitementImage.DetectSignV2(image);
            BouttonRefresh();
        } else if (type.equals("avi") || type.equals("mkv") || type.equals("mp4")) {
            Vector<Mat> video = TraitementImage.videoTreatment(image, (int)(1000.0/30));
            genererVideo(video);

        }

    }

    public void BouttonRefresh() {
        if (boutonPanel != null) {
            boutonPanel.removeAll();
        }
        if (panel != null) {
            panel.removeAll();
        }

        Button button = new Button(this, "Charger image", boutonPanel, 10, 30, 2000, 50);
        Button button1 = new Button(this, "Visualiser image", boutonPanel, panel, matrice.clone(), 30, 30, 2000, 50);
        Button button2 = new Button(this, "Image hsv", boutonPanel, panel, matricesHSV, 50, 30, 2000, 50);
        Button button3 = new Button(this, "Extraction des contours", boutonPanel, panel, matricesContourRouge, 70, 30,
                2000, 50);
        Button button4 = new Button(this, "Detection Panneaux methode1", boutonPanel, panel, panneauxDetecte, 90, 30,
                2000, 50);
        Button button5 = new Button(this, "Detection Panneaux methode2", boutonPanel, panel, panneauxDetecteV2, 30, 60,
                2000, 50);

        this.setVisible(true);
    }

    // reconnaitre le type de fichier
    public String detectByExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return (filename.substring(dotIndex + 1).toLowerCase());
        }
        return "Unknown";
    }

    public void genererVideo(Vector<Mat> images) {
        for (int i = 0; i < images.size(); i++) {
            Button.showImage(this, images.get(i), panel);
            try {
                Thread.sleep((int)(1000.0/30));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public void setImage(String img) {
        this.image = img;
        preparationImage();
    }

    public String getImage() {
        return this.image;
    }
}
