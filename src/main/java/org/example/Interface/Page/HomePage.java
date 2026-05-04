package org.example.Interface.Page;

import org.example.Interface.Interface;
import org.example.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class HomePage extends JPanel implements MouseListener, MouseMotionListener {
    private Interface anInterface;

    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color LINE_COLOR = new Color(86, 86, 86);
    private final Color INVENTORY_COLOR = new Color(162, 108, 39);
    private final Color BOOSTER_COLOR = new Color(50, 28, 86);
    private final Color INVENTORY_HOVER_COLOR = new Color(112, 80, 28);
    private final Color BOOSTER_HOVER_COLOR = new Color(30, 17, 51);
    private final int WIDTH_BUTTON = 200;
    private final int HEIGHT_BUTTON = 45;

    private boolean inventiryHover, boosterHover;

    private final int BORDER_SIZE = 75;
    public HomePage(Interface anInterface) {
        super(null);
        this.anInterface = anInterface;
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond et lignes
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE * 2);

        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(0, BORDER_SIZE, getWidth(), BORDER_SIZE);
        g2d.drawLine(0, getHeight() - BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE);

        // Titre "KULTURO"
        g2d.setColor(TEXT_COLOR);
        Font police = new Font("Arial", Font.BOLD, 50);
        g2d.setFont(police);
        FontMetrics metrics = g2d.getFontMetrics(police);
        g2d.drawString("KULTURO", (getWidth() - metrics.stringWidth("KULTURO")) / 2, getHeight() / 2);

        // --- DESSIN DES BOUTONS ---
        g2d.setColor(LINE_COLOR);
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);
        int xButton1 = (getWidth() / 3) - (WIDTH_BUTTON / 2);
        g2d.fillRoundRect(xButton1, yButton, WIDTH_BUTTON, HEIGHT_BUTTON, 15, 15);
        if (!inventiryHover) {
            g2d.setColor(INVENTORY_COLOR);
        }else {
            g2d.setColor(INVENTORY_HOVER_COLOR);
        }
        g2d.fillRoundRect(xButton1 + 3, yButton + 3, WIDTH_BUTTON - 6, HEIGHT_BUTTON - 6, 12, 12);
        g2d.setColor(TEXT_COLOR);
        police = new Font("Arial", Font.PLAIN, 20);
        g2d.setFont(police);
        g2d.drawString("INVENTORY", xButton1 + 3 + 5, yButton + 3 + 27);

        g2d.setColor(LINE_COLOR);
        int xButton2 = (2 * getWidth() / 3) - (WIDTH_BUTTON / 2);
        g2d.fillRoundRect(xButton2, yButton, WIDTH_BUTTON, HEIGHT_BUTTON, 15, 15);
        if (!boosterHover) {
            g2d.setColor(BOOSTER_COLOR);
        }else {
            g2d.setColor(BOOSTER_HOVER_COLOR);
        }
        g2d.fillRoundRect(xButton2 + 3, yButton + 3, WIDTH_BUTTON - 6, HEIGHT_BUTTON - 6, 12, 12);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("BOOSTER", xButton2 + 3 + 5, yButton + 3 + 27);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);
        int xButton1 = (getWidth() / 3) - (WIDTH_BUTTON / 2);
        int xButton2 = (2 * getWidth() / 3) - (WIDTH_BUTTON / 2);
        int x = e.getX();
        int y = e.getY();
        if (x > xButton1 && x < xButton1+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            System.out.println("Inventory");
            anInterface.show("INVENTORY");
        }else if (x > xButton2 && x < xButton2+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            System.out.println("Booster");
            anInterface.show("BOOSTER");
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
        setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);
        int xButton1 = (getWidth() / 3) - (WIDTH_BUTTON / 2);
        int xButton2 = (2 * getWidth() / 3) - (WIDTH_BUTTON / 2);
        int x = e.getX();
        int y = e.getY();
        if (x > xButton1 && x < xButton1+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            inventiryHover = true;
            repaint();
        }else if (x > xButton2 && x < xButton2+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            boosterHover = true;
            repaint();
        }else {
            inventiryHover = false;
            boosterHover = false;
            repaint();
        }
    }
}