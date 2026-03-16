package org.example.Interface.Page;

import org.example.Interface.Interface;
import org.example.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Home extends JPanel implements MouseListener {
    private Interface anInterface;
    private BufferedImage back;

    private int boosterWidth = 120;
    private int boosterHeight = 200;

    public Home(Interface anInterface, Path pathBackground) {
        super(null);
        this.anInterface = anInterface;
        if (pathBackground != null) {
            try {
                this.back = ImageIO.read(pathBackground.toFile());
            } catch (IOException e) {
                System.err.println("Error loading background: " + e.getMessage());
            }
        }
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (back != null) {
            g.drawImage(back, 0, 0, getWidth(), getHeight(), this);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        int x = (getWidth() - boosterWidth) / 2;
        int y = (getHeight() - boosterHeight) / 2;
        g2d.fillRoundRect(x, y, boosterWidth, boosterHeight, 15, 15);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = (getWidth() - boosterWidth) / 2;
        int y = (getHeight() - boosterHeight) / 2;
        System.out.println(x + " / " + y);
        if (e.getX() > x && e.getX() < x + boosterWidth && e.getY() > y && e.getY() < y + boosterHeight) {
            System.out.println("true");
        }else {
            System.out.println("false");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}