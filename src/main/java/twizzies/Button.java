package twizzies;

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

    public Button(Frame f, String nom, JPanel pannel, String fichierImage, int x, int y, int width, int height) {
        this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);

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
            JPanel panel = new JPanel();
            panel.setBounds(50, 50, 100, 100);
            BufferedImage img = ImageIO.read(new File(fichierImage));
            JLabel pic = new JLabel(new ImageIcon(img));
            panel.add(pic);
            f.add(panel);
        } catch (IOException e) {
        }
    }

}
