package org.example.Interface.Page;

import org.example.Interface.Interface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class Login extends JPanel {
    private Interface anInterface;
    private BufferedImage back;
    private JTextField textField; // Déclarer en tant qu'attribut

    public Login(Interface anInterface, Path pathBackground) {
        // Utiliser un layout pour organiser les composants
        super(new FlowLayout());
        this.anInterface = anInterface;

        if (pathBackground != null) {
            try {
                this.back = ImageIO.read(pathBackground.toFile());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        usernameBox(10, 10, 200, 30);
    }
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;
        if (back != null) {
            g.drawImage(back, getWidth(), getHeight(), this);
        }
    }

    public void usernameBox(int x, int y, int w, int h) {
        textField = new JTextField();
        textField.setBounds(x, y, w, h);
        this.add(textField);
        this.revalidate();
        this.repaint();
    }

}

