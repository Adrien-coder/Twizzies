package twizzies;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Button {
    String fichierImage;

    public Button(Frame f, String nom, JPanel pannel, int x, int y, int width, int height) {
        // this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBackground(Color.pink);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);
        pannel.setBackground(new Color(230, 223, 204));
        bouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // inserer ici le traitement d'image
                Query q = new Query(f);

            }
        });

    }

    // le bouton prend en compte le frame pour le placement de l'image au centre
    public Button(Frame f, String nom, JPanel pannel, String fichierImage, int x, int y, int width, int height) {
        // this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBackground(Color.pink);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);
        pannel.setBackground(new Color(230, 223, 204));
        bouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // inserer ici le traitement d'image

                insererImage(f, fichierImage);

            }
        });

    }

    public Button(Frame f, String nom, JPanel pannel, JPanel impanel, Mat img, int x, int y, int width, int height) {
        // this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBackground(Color.pink);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);
        pannel.setBackground(new Color(230, 223, 204));
        bouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // inserer ici le traitement d'image
                impanel.removeAll();
                impanel.repaint();
                showImage(f, img, impanel);

            }
        });

    }

    public Button(Frame f, String nom, JPanel pannel, JPanel impanel, Vector<Mat> imgs, int x, int y, int width,
            int height) {
        // this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBackground(Color.pink);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);
        pannel.setBackground(new Color(230, 223, 204));
        bouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // inserer ici le traitement d'image
                impanel.removeAll();
                impanel.repaint();
                showImageVector(f, imgs, impanel);

            }
        });

    }

    // fonction pour inserer l'image
    public void insererImage(JFrame f, String fichierImage) {
        try {
            // création d'un Jpanel pour placer l'image
            JPanel panel = new JPanel();
            panel.setBackground(new Color(230, 223, 204));
            panel.setBounds(70, 70, 100, 100);
            BufferedImage img = ImageIO.read(new File(fichierImage));
            JLabel pic = new JLabel(new ImageIcon(img));
            panel.add(pic);
            f.add(panel);
            // recharger la frame pour faire apparaitre l'image
            f.setVisible(true);
        } catch (IOException e) {
        }
    }

    public static void showImage(Frame f, Mat img, JPanel panel) {

        Size sz = new Size(300, 300);

        Mat imageResized = new Mat();
        Imgproc.resize(img, imageResized, sz);

        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".png", imageResized, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            // BufferedImage img = ImageIO.read(new File(fichierImage));
            JLabel pic = new JLabel(new ImageIcon(bufImage));
            panel.add(pic);
            f.add(panel);
            // recharger la frame pour faire apparaitre l'image
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showImageVector(Frame f, Vector<Mat> imgs, JPanel panel) {
        for (int i = 0; i < imgs.size(); i++) {
            showImage(f, imgs.get(i), panel);
        }
    }

}
