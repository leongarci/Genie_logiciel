package Interface.Page;


import Interface.Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class BoosterPage extends JPanel implements MouseListener, MouseMotionListener {
    private Interface anInterface;

    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color LINE_COLOR = new Color(86, 86, 86);
    private final Color BOOSTER_COLOR = new Color(50, 28, 86);
    private final Color BOOSTER_HOVER_COLOR = new Color(30, 17, 51);
    private final Color INVENTORY_COLOR = new Color(162, 108, 39);
    private final Color INVENTORY_HOVER_COLOR = new Color(112, 80, 28);

    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);

    private final int LINE_STROCK = 3;
    private final int BORDER_SIZE = 75;

    private final int WIDTH_BOOSTER = 200;
    private final int HEIGHT_BOOSTER = 300;

    private final int WIDTH_BUTTON = 200;
    private final int HEIGHT_BUTTON = 50;
    private final int SIZE_POLICE = 20;

    // Constantes pour les boutons carrousel
    private final int SIZE_CAROUSEL_BUTTON = 50;
    private final int MARGIN_CAROUSEL = 30;

    // Constantes pour le bouton de retour
    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15; // Marge par rapport au bord gauche

    private boolean BUTTON_HOVER = false;
    private boolean LEFT_BUTTON_HOVER = false;
    private boolean RIGHT_BUTTON_HOVER = false;
    private boolean BACK_BUTTON_HOVER = false;

    public BoosterPage(Interface anInterface) {
        super(null);
        this.anInterface = anInterface;
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond et lignes
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE * 2);

        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(LINE_STROCK));
        g2d.drawLine(0, BORDER_SIZE, getWidth(), BORDER_SIZE);
        g2d.drawLine(0, getHeight() - BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE);

        // --- Bouton RETOUR en haut à gauche ---
        int backBtnX = MARGIN_BACK;
        // Centré verticalement dans la zone du haut (qui fait la taille BORDER_SIZE)
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(backBtnX, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        if (BACK_BUTTON_HOVER) {
            g2d.setColor(RETURN_HOVER_COLOR); // Utilise la même couleur que le carrousel (modifiable si tu veux)
        } else {
            g2d.setColor(RETURN_COLOR);
        }
        g2d.fillRoundRect(backBtnX + LINE_STROCK, backBtnY + LINE_STROCK, SIZE_BACK_BUTTON - LINE_STROCK * 2, SIZE_BACK_BUTTON - LINE_STROCK * 2, 10, 10);

        Font policeArrow = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(policeArrow);
        FontMetrics metricsArrow = g2d.getFontMetrics(policeArrow);

        String textBack = "<";
        g2d.setColor(TEXT_COLOR);
        int textBackX = backBtnX + (SIZE_BACK_BUTTON - metricsArrow.stringWidth(textBack)) / 2;
        int textBackY = backBtnY + ((SIZE_BACK_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString(textBack, textBackX, textBackY);


        // Carte Booster
        g.setColor(Color.BLACK);
        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;
        int y_booster = getHeight() / 2 - HEIGHT_BOOSTER / 2;
        g2d.fillRoundRect(x_booster, y_booster, WIDTH_BOOSTER, HEIGHT_BOOSTER, 15, 15);

        // Bouton principal (Ouvrir)
        g.setColor(LINE_COLOR);
        int x_btn = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;
        g2d.fillRoundRect(x_btn, y_btn, WIDTH_BUTTON, HEIGHT_BUTTON, 15, 15);

        if (BUTTON_HOVER) {
            g2d.setColor(BOOSTER_HOVER_COLOR);
        } else {
            g2d.setColor(BOOSTER_COLOR);
        }
        g2d.fillRoundRect(x_btn + LINE_STROCK, y_btn + LINE_STROCK, WIDTH_BUTTON - LINE_STROCK * 2, HEIGHT_BUTTON - LINE_STROCK * 2, 10, 10);

        String text = "Ouvrir";
        Font police = new Font("Arial", Font.BOLD, SIZE_POLICE);
        g2d.setFont(police);
        FontMetrics metrics = g2d.getFontMetrics(police);
        g2d.setColor(TEXT_COLOR);
        int textX = x_btn + (WIDTH_BUTTON - metrics.stringWidth(text)) / 2;
        int textY = y_btn + ((HEIGHT_BUTTON - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);

        // --- Bouton carrousel GAUCHE "<" ---
        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(leftBtnX, leftBtnY, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON, 15, 15);
        if (LEFT_BUTTON_HOVER) {
            g2d.setColor(INVENTORY_HOVER_COLOR);
        } else {
            g2d.setColor(INVENTORY_COLOR);
        }
        g2d.fillRoundRect(leftBtnX + LINE_STROCK, leftBtnY + LINE_STROCK, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, 10, 10);

        String textLeft = "<";
        g2d.setFont(policeArrow);
        g2d.setColor(TEXT_COLOR);
        int textLeftX = leftBtnX + (SIZE_CAROUSEL_BUTTON - metricsArrow.stringWidth(textLeft)) / 2;
        int textLeftY = leftBtnY + ((SIZE_CAROUSEL_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString(textLeft, textLeftX, textLeftY);

        // --- Bouton carrousel DROIT ">" ---
        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(rightBtnX, rightBtnY, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON, 15, 15);
        if (RIGHT_BUTTON_HOVER) {
            g2d.setColor(INVENTORY_HOVER_COLOR);
        } else {
            g2d.setColor(INVENTORY_COLOR);
        }
        g2d.fillRoundRect(rightBtnX + LINE_STROCK, rightBtnY + LINE_STROCK, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, 10, 10);

        String textRight = ">";
        g2d.setColor(TEXT_COLOR);
        int textRightX = rightBtnX + (SIZE_CAROUSEL_BUTTON - metricsArrow.stringWidth(textRight)) / 2;
        int textRightY = rightBtnY + ((SIZE_CAROUSEL_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString(textRight, textRightX, textRightY);

        // Gestion globale du curseur
        if (BUTTON_HOVER || LEFT_BUTTON_HOVER || RIGHT_BUTTON_HOVER || BACK_BUTTON_HOVER) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();

        // Variables de position
        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;
        int y_booster = getHeight() / 2 - HEIGHT_BOOSTER / 2;

        int x_btn_main = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn_main = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;

        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        // Clic sur Bouton Retour
        if (isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON)) {
            System.out.println("Bouton Retour <");
            anInterface.show("HOME");
        }

        // Clic sur Booster
        if (isOnArea(x_booster, y_booster, x_mouse, y_mouse, WIDTH_BOOSTER, HEIGHT_BOOSTER)) {
            System.out.println("Booster cliqué");
        }

        // Clic sur Bouton Ouvrir
        if (isOnArea(x_btn_main, y_btn_main, x_mouse, y_mouse, WIDTH_BUTTON, HEIGHT_BUTTON)) {
            System.out.println("Bouton OUVRIR cliqué");
        }

        // Clic sur Bouton Gauche <
        if (isOnArea(leftBtnX, leftBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON)) {
            System.out.println("Bouton Précédent <");
        }

        // Clic sur Bouton Droit >
        if (isOnArea(rightBtnX, rightBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON)) {
            System.out.println("Bouton Suivant >");
        }
    }

    private boolean isOnArea(int x_init, int y_init, int x_mouse, int y_mouse, int width, int height) {
        return (x_mouse > x_init && x_mouse < x_init + width && y_mouse > y_init && y_mouse < y_init + height);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();

        boolean repaintNeeded = false;

        // Positions des zones
        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;

        int x_btn_main = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn_main = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;

        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;

        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        // Vérification survol Bouton Retour
        boolean isBackHovered = isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON);
        if (BACK_BUTTON_HOVER != isBackHovered) {
            BACK_BUTTON_HOVER = isBackHovered;
            repaintNeeded = true;
        }

        // Vérification survol Bouton Principal
        boolean isMainHovered = isOnArea(x_btn_main, y_btn_main, x_mouse, y_mouse, WIDTH_BUTTON, HEIGHT_BUTTON);
        if (BUTTON_HOVER != isMainHovered) {
            BUTTON_HOVER = isMainHovered;
            repaintNeeded = true;
        }

        // Vérification survol Bouton Gauche
        boolean isLeftHovered = isOnArea(leftBtnX, leftBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON);
        if (LEFT_BUTTON_HOVER != isLeftHovered) {
            LEFT_BUTTON_HOVER = isLeftHovered;
            repaintNeeded = true;
        }

        // Vérification survol Bouton Droit
        boolean isRightHovered = isOnArea(rightBtnX, rightBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON);
        if (RIGHT_BUTTON_HOVER != isRightHovered) {
            RIGHT_BUTTON_HOVER = isRightHovered;
            repaintNeeded = true;
        }

        if (repaintNeeded) {
            repaint();
        }
    }
}