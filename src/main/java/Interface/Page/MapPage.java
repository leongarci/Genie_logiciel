package Interface.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import Interface.Interface;

public class MapPage extends JPanel {

    private final Interface anInterface;
    private final Map<String, Point> regionPoints = new HashMap<>();
    private Image franceMap;

    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15;
    private final int BORDER_SIZE = 75;
    private boolean BACK_BUTTON_HOVER = false;

    // --- CONSTANTES DE RÉFÉRENCE POUR LE RESPONSIVE & LE ZOOM ---
    private final double BASE_WIDTH = 834.0;
    private final double BASE_HEIGHT = 552.0;

    // Règle la valeur ici pour agrandir/réduire l'image (1.20 = 20% plus grand)
    private final double ZOOM_FACTOR = 1.18;

    public MapPage(Interface parent) {
        this.anInterface = parent;
        setLayout(null);
        setOpaque(false);

        // Chargement sécurisé de l'image
        java.net.URL imgURL = getClass().getResource("/Interface/Page/Images/carte-inventory-Photoroom.png");
        if (imgURL == null) {
            imgURL = getClass().getResource("Images/carte-inventory-Photoroom.png");
        }
        if (imgURL != null) {
            franceMap = new ImageIcon(imgURL).getImage();
        } else {
            franceMap = new ImageIcon("src/main/java/Interface/Page/Images/carte-inventory-Photoroom.png").getImage();
        }

        initRegions();
        setupMouseListeners();
    }

    private void initRegions() {
        // Coordonnées recalibrées visuellement pour le ZOOM_FACTOR = 1.18

        // --- FRANCE MÉTROPOLITAINE ---
        regionPoints.put("Haut-De-France", new Point(440, 120));
        regionPoints.put("Normandie", new Point(375, 163));
        regionPoints.put("Île-de-France", new Point(435, 176));
        regionPoints.put("Grand Est", new Point(500, 190));
        regionPoints.put("Bretagne", new Point(300, 193));
        regionPoints.put("Pays de la Loire", new Point(365, 221));
        regionPoints.put("Centre-Val-De-Loire", new Point(420, 235));
        regionPoints.put("Bourgogne-Franche-Comté", new Point(490, 250));
        regionPoints.put("Nouvelle-Aquitaine", new Point(385, 300));
        regionPoints.put("Auvergne-Rhône-Alpes", new Point(480, 320));
        regionPoints.put("Occitanie", new Point(420, 375));
        regionPoints.put("Provence-Alpes-Côte d'Azur", new Point(530, 380));
        regionPoints.put("Corse", new Point(589, 435));

        // --- OUTRE-MER ---
        regionPoints.put("Guyane", new Point(280, 375)); // Centré sur la grosse masse à gauche
        regionPoints.put("Guadeloupe", new Point(238, 172)); // Aligné avec les petits points à l'ouest
        regionPoints.put("Martinique", new Point(241, 220));
        regionPoints.put("Reunion", new Point(536, 449));    // Aligné avec les points au sud-est
        regionPoints.put("Mayotte", new Point(493, 462));
    }

    /**
     * Calcule la position d'un point proportionnellement à la taille actuelle du panneau
     * EN PRENANT EN COMPTE LE FACTEUR DE ZOOM.
     */
    private Point getScaledPoint(Point originalPoint) {
        // Taille de l'image une fois zoomée
        int imgW = (int) (getWidth() * ZOOM_FACTOR);
        int imgH = (int) (getHeight() * ZOOM_FACTOR);

        // Décalage pour centrer l'image qui déborde
        int offsetX = (getWidth() - imgW) / 2;
        int offsetY = (getHeight() - imgH) / 2;

        // Calcul de la nouvelle coordonnée X, Y
        int scaledX = offsetX + (int) (originalPoint.x * (imgW / BASE_WIDTH));
        int scaledY = offsetY + (int) (originalPoint.y * (imgH / BASE_HEIGHT));

        return new Point(scaledX, scaledY);
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Clic sur Bouton Retour
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
                if (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON) {
                    anInterface.show("HOME");
                    return;
                }

                // Clic sur une région
                for (Map.Entry<String, Point> entry : regionPoints.entrySet()) {
                    Point scaledP = getScaledPoint(entry.getValue());
                    if (scaledP.distance(x, y) < 25) {
                        anInterface.showInventoryForRegion(entry.getKey());
                        break; // On arrête la boucle si on a cliqué sur une région
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                boolean repaintNeeded = false;

                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
                boolean isBackHovered = (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON);

                if (BACK_BUTTON_HOVER != isBackHovered) {
                    BACK_BUTTON_HOVER = isBackHovered;
                    repaintNeeded = true;
                }

                // Curseur main sur les régions
                boolean onRegion = false;
                for (Point p : regionPoints.values()) {
                    Point scaledP = getScaledPoint(p);
                    if (scaledP.distance(x, y) < 25) {
                        onRegion = true;
                        break;
                    }
                }

                setCursor(Cursor.getPredefinedCursor((isBackHovered || onRegion) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                if (repaintNeeded) repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fond vert de secours
        g2d.setColor(new Color(0, 52, 21));
        g2d.fillRoundRect(0, 0, width, height, 15, 15);

        // --- DESSIN DE L'IMAGE ZOOMÉE ---
        if (franceMap != null) {
            int imgW = (int) (width * ZOOM_FACTOR);
            int imgH = (int) (height * ZOOM_FACTOR);
            int offsetX = (width - imgW) / 2;
            int offsetY = (height - imgH) / 2;
            g2d.drawImage(franceMap, offsetX, offsetY, imgW, imgH, this);
        }

        // Bouton RETOUR "<"
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
        g2d.setColor(new Color(86, 86, 86));
        g2d.fillRoundRect(MARGIN_BACK, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        g2d.setColor(BACK_BUTTON_HOVER ? new Color(1, 1, 35) : new Color(32, 32, 57));
        g2d.fillRoundRect(MARGIN_BACK + 3, backBtnY + 3, SIZE_BACK_BUTTON - 6, SIZE_BACK_BUTTON - 6, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("<", MARGIN_BACK + (SIZE_BACK_BUTTON - fm.stringWidth("<")) / 2, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());

        // Dessin des balises de régions
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        for (Map.Entry<String, Point> entry : regionPoints.entrySet()) {
            Point scaledP = getScaledPoint(entry.getValue());

            // Cercle couleur Or
            g2d.setColor(new Color(162, 108, 39));
            g2d.fillOval(scaledP.x - 8, scaledP.y - 8, 16, 16);

            // Contour Blanc
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(scaledP.x - 8, scaledP.y - 8, 16, 16);

            // Nom de la région
            g2d.drawString(entry.getKey(), scaledP.x + 15, scaledP.y + 5);
        }
    }
}