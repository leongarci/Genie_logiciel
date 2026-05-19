package Interface.Page;

import Interface.Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class HomePage extends JPanel implements MouseListener, MouseMotionListener {
    private Interface anInterface;

    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color LINE_COLOR = new Color(86, 86, 86);

    // Couleurs des boutons
    private final Color INVENTORY_COLOR = new Color(162, 108, 39);
    private final Color INVENTORY_HOVER_COLOR = new Color(112, 80, 28);
    private final Color STATS_COLOR = new Color(28, 86, 120);
    private final Color STATS_HOVER_COLOR = new Color(17, 51, 80);
    private final Color BOOSTER_COLOR = new Color(50, 28, 86);
    private final Color BOOSTER_HOVER_COLOR = new Color(30, 17, 51);

    private final int WIDTH_BUTTON = 200;
    private final int HEIGHT_BUTTON = 45;

    private boolean inventiryHover, statsHover, boosterHover;

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

        // --- CALCUL DYNAMIQUE POUR 3 BOUTONS ---
        int gap = (getWidth() - (3 * WIDTH_BUTTON)) / 4;
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);

        int xButton1 = gap;
        int xButton2 = xButton1 + WIDTH_BUTTON + gap;
        int xButton3 = xButton2 + WIDTH_BUTTON + gap;

        // Bouton 1 : INVENTORY
        drawButton(g2d, xButton1, yButton, "INVENTORY", inventiryHover ? INVENTORY_HOVER_COLOR : INVENTORY_COLOR);

        // Bouton 2 : STATS
        drawButton(g2d, xButton2, yButton, "DONNÉES", statsHover ? STATS_HOVER_COLOR : STATS_COLOR);

        // Bouton 3 : BOOSTER
        drawButton(g2d, xButton3, yButton, "BOOSTER", boosterHover ? BOOSTER_HOVER_COLOR : BOOSTER_COLOR);

        if(inventiryHover || statsHover || boosterHover) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void drawButton(Graphics2D g2d, int x, int y, String text, Color color) {
        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(x, y, WIDTH_BUTTON, HEIGHT_BUTTON, 15, 15);
        g2d.setColor(color);
        g2d.fillRoundRect(x + 3, y + 3, WIDTH_BUTTON - 6, HEIGHT_BUTTON - 6, 12, 12);

        g2d.setColor(TEXT_COLOR);
        Font police = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(police);
        FontMetrics metrics = g2d.getFontMetrics(police);
        int textX = x + (WIDTH_BUTTON - metrics.stringWidth(text)) / 2;
        int textY = y + ((HEIGHT_BUTTON - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int gap = (getWidth() - (3 * WIDTH_BUTTON)) / 4;
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);
        int xButton1 = gap;
        int xButton2 = xButton1 + WIDTH_BUTTON + gap;
        int xButton3 = xButton2 + WIDTH_BUTTON + gap;

        int x = e.getX();
        int y = e.getY();

        if (x > xButton1 && x < xButton1+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            anInterface.show("MAP");
        } else if (x > xButton2 && x < xButton2+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            anInterface.show("STATS");
        } else if (x > xButton3 && x < xButton3+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON) {
            anInterface.show("BOOSTER");
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int gap = (getWidth() - (3 * WIDTH_BUTTON)) / 4;
        int yButton = getHeight() - BORDER_SIZE - (HEIGHT_BUTTON / 2);
        int xButton1 = gap;
        int xButton2 = xButton1 + WIDTH_BUTTON + gap;
        int xButton3 = xButton2 + WIDTH_BUTTON + gap;

        int x = e.getX();
        int y = e.getY();

        inventiryHover = (x > xButton1 && x < xButton1+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON);
        statsHover = (x > xButton2 && x < xButton2+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON);
        boosterHover = (x > xButton3 && x < xButton3+WIDTH_BUTTON && y > yButton && y < yButton+HEIGHT_BUTTON);

        repaint();
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
}