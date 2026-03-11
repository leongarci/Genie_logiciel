package org.example.Interface.Page;

import org.example.Interface.Interface;
import org.example.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Home extends JPanel {
    private Interface anInterface;
    private BufferedImage back;
    private User user;
    public Home(Interface anInterface, Path pathBackground, User user) {
        super(null);
        this.anInterface = anInterface;
        this.user = user;
        // Background
        if (pathBackground != null) {
            try {
                this.back = ImageIO.read(pathBackground.toFile());
            } catch (IOException e) {
                System.err.println("Error loading background: " + e.getMessage());
            }
        }


    }
}
