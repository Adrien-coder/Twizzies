package twizzies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Query extends JFrame implements ActionListener {
    // Une JFrame qui demande à l'utilisateur le path de l'image.

    JTextField text1;
    JButton btn;
    Frame frame;

    public Query(Frame f) {
        frame = f;
        text1 = new JTextField("entrer le path de l'image");
        text1.setBounds(20, 40, 200, 28);

        btn = new JButton("entrer");
        btn.setBounds(50, 140, 100, 40);
        btn.addActionListener(this);

        add(text1);
        add(btn);
        setTitle("Interface avec Image");
        setSize(500, 200);
        setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(230, 223, 204));
        // Affichage de la fenêtre
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        String result = "";
        if (e.getSource() == btn) {
            String name = text1.getText();
            result = name;
            frame.setImage(result);
            System.out.println(frame.getImage());
            frame.setVisible(true);
        }

    }
}
