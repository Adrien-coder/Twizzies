package twizzies;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Button {
    String fichierImage;

    public Button(String nom, JPanel pannel, String fichierImage, int x, int y, int width, int height) {
        this.fichierImage = fichierImage;
        JButton bouton = new JButton(nom);
        bouton.setBounds(x, y, width, height);
        pannel.add(bouton);

        bouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                // inserer ici le traitement d'image
            }
        });

    }

}
