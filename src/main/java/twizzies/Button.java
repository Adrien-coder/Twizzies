package twizzies;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Button {
    String fichierImage;

    // le bouton prend en compte le frame pour le placement de l'image au centre
    public Button(Frame f, String nom, JPanel pannel, String fichierImage, int x, int y, int width, int height) {
        this.fichierImage = fichierImage;
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

}
