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

    public MapPage(Interface parent) {
        this.anInterface = parent;
        setLayout(null);
        setOpaque(false);

        // --- CHARGEMENT SÉCURISÉ DE L'IMAGE (Compatible Maven & IDE) ---
        // On cherche d'abord dans le classpath (src/main/resources/images/...)
        java.net.URL imgURL = getClass().getResource("/images/carte-inventory-Photoroom.png");
        if (imgURL != null) {
            franceMap = new ImageIcon(imgURL).getImage();
        } else {
            // Option de secours si le dossier resources est resté à la racine du projet
            franceMap = new ImageIcon("resources/images/carte-inventory-Photoroom.png").getImage();
        }

        initRegions();
        setupMouseListeners();
    }

    private void initRegions() {
        // Coordonnées X,Y des balises sur l'écran (à ajuster une fois l'image visible !)
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
                    Point p = entry.getValue();
                    if (p.distance(x, y) < 25) { // Rayon de tolérance du clic
                        anInterface.showInventoryForRegion(entry.getKey());
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
                    if (p.distance(x, y) < 25) {
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

        // Dessin de la carte en arrière-plan COMPLET (prend toute la place)
        if (franceMap != null) {
            g2d.drawImage(franceMap, 0, 0, width, height, this);
        }

        // [SUPPRIMÉ] Les lignes de séparation de 3px ont été enlevées ici

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
            Point p = entry.getValue();

            // Point repère (Bouton Or)
            g2d.setColor(new Color(162, 108, 39));
            g2d.fillOval(p.x - 8, p.y - 8, 16, 16);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(p.x - 8, p.y - 8, 16, 16);

            // Nom de la région
            g2d.drawString(entry.getKey(), p.x + 15, p.y + 5);
        }
    }
}