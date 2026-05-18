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

    // --- CONSTANTES DE RÉFÉRENCE POUR LE RESPONSIVE ---
    // C'est la taille du panneau quand la fenêtre fait 850x600
    private final double BASE_WIDTH = 834.0;
    private final double BASE_HEIGHT = 552.0;

    public MapPage(Interface parent) {
        this.anInterface = parent;
        setLayout(null);
        setOpaque(false);

        // Chargement de l'image (Triple sécurité)
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
        // Tes coordonnées actuelles (calibrées sur l'écran de base)
        regionPoints.put("Île-de-France", new Point(430, 220));
        regionPoints.put("Bretagne", new Point(200, 260));
        regionPoints.put("Provence-Alpes-Côte d'Azur", new Point(600, 480));
        regionPoints.put("Nouvelle-Aquitaine", new Point(320, 420));
        regionPoints.put("Occitanie", new Point(450, 490));
        regionPoints.put("Grand Est", new Point(620, 230));
        regionPoints.put("Reunion", new Point(600, 200));
        regionPoints.put("Corse", new Point(670, 230));
        regionPoints.put("Haut-De-France", new Point(520, 130));
        regionPoints.put("Pays de la Loire", new Point(420, 30));
        regionPoints.put("Normandie", new Point(120, 230));
        regionPoints.put("Centre-Val-De-Loire", new Point(620, 420));
        regionPoints.put("Mayotte", new Point(320, 430));
        regionPoints.put("Guyane", new Point(450, 320));
        regionPoints.put("Bourgogne-Franche-Comté", new Point(620, 230));
        regionPoints.put("Auvergne-Rhône-Alpes", new Point(320, 130));
        regionPoints.put("Guadeloupe", new Point(420, 69));
        regionPoints.put("Martinique", new Point(20, 230));
    }

    /**
     * Calcule la position d'un point proportionnellement à la taille actuelle du panneau
     */
    private Point getScaledPoint(Point originalPoint) {
        int scaledX = (int) (originalPoint.x * (getWidth() / BASE_WIDTH));
        int scaledY = (int) (originalPoint.y * (getHeight() / BASE_HEIGHT));
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

                // Clic sur une région (on vérifie avec les points mis à l'échelle)
                for (Map.Entry<String, Point> entry : regionPoints.entrySet()) {
                    Point scaledP = getScaledPoint(entry.getValue());
                    if (scaledP.distance(x, y) < 25) {
                        anInterface.showInventoryForRegion(entry.getKey());
                        break;
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

                // Curseur main sur les régions (avec les points mis à l'échelle)
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

        // Dessin de la carte en arrière-plan COMPLET
        if (franceMap != null) {
            g2d.drawImage(franceMap, 0, 0, width, height, this);
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

        // Dessin des balises de régions ADAPTATIVES
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        for (Map.Entry<String, Point> entry : regionPoints.entrySet()) {
            // On récupère le point recalculé pour la taille d'écran actuelle
            Point scaledP = getScaledPoint(entry.getValue());

            // Point repère (Bouton Or)
            g2d.setColor(new Color(162, 108, 39));
            g2d.fillOval(scaledP.x - 8, scaledP.y - 8, 16, 16);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(scaledP.x - 8, scaledP.y - 8, 16, 16);

            // Nom de la région (suit le point)
            g2d.drawString(entry.getKey(), scaledP.x + 15, scaledP.y + 5);
        }
    }
}