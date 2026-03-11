package org.example.Interface.Page;

import org.example.Interface.Interface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Home extends JPanel {
    private Interface anInterface;
    private BufferedImage back;

    public Home(Interface anInterface, Path pathBackground) {
        super(null);
        this.anInterface = anInterface;
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
