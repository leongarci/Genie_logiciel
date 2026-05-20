package Interface.Page;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import Interface.Interface;
import carte.Carte;
import musee.MuseeStatsDAO;

public class StatsPage extends JPanel {

    private Interface anInterface;

    // Couleurs de base de l'application
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color LINE_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);

    // Couleurs Funky pour les graphiques
    private final Color ACCENT_GOLD = new Color(162, 108, 39);
    private final Color ACCENT_BLUE = new Color(28, 86, 120);

    private final int BORDER_SIZE = 75;
    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15;
    private boolean BACK_BUTTON_HOVER = false;

    private JPanel chartsContainer;
    private JScrollPane scrollPane;

    public StatsPage(Interface anInterface) {
        this.anInterface = anInterface;
        setLayout(null);
        setOpaque(false);

        initUI();
        loadDataAndBuildCharts();
        setupMouseListeners();
    }

    private void initUI() {
        chartsContainer = new JPanel();
        chartsContainer.setLayout(new BoxLayout(chartsContainer, BoxLayout.Y_AXIS));
        chartsContainer.setOpaque(false);

        scrollPane = new JScrollPane(chartsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        applyModernScrollBar(scrollPane); // <-- LIGNE AJOUTÉE ICI

        add(scrollPane);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scrollPane.setBounds(30, BORDER_SIZE + 20, getWidth() - 60, getHeight() - (BORDER_SIZE * 2) - 40);
            }
        });
    }

    private void loadDataAndBuildCharts() {
        MuseeStatsDAO statsDAO = new MuseeStatsDAO();
        chartsContainer.removeAll();

        // --- 1. LE TOP 5 (Barres Modernes recalibrées) ---
        List<Carte> topMusees = statsDAO.getTopMuseesByValeur(5);
        Map<String, Double> mapTop = new LinkedHashMap<>();
        for (Carte c : topMusees) mapTop.put(c.getNomOfficiel(), (double) c.getTotal());
        chartsContainer.add(new ModernBarChart("• LES GÉANTS (Entrées Totales Globales)", mapTop, ACCENT_GOLD));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 2. FOCUS SUR LE GINI (Jauges d'inégalité) ---
        Map<String, Double> giniData = statsDAO.giniEntreesParRegion();
        chartsContainer.add(new GiniMeterSection("• INDICE D'INÉGALITÉ DE FRÉQUENTATION (GINI GLOBAL)", giniData));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 3. DIVERSITÉ CULTURELLE COMPLET (Donut Chart à 100%) ---
        Map<String, Double> mapDiv = statsDAO.diversiteCulturelleParRegion();
        chartsContainer.add(new DonutChartPanel("• RÉPARTITION DE LA DIVERSITÉ CULTURELLE GLOBALE", mapDiv));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        chartsContainer.revalidate();
        chartsContainer.repaint();
    }

    // ==========================================
    // COMPOSANT 1 : BARRES MODERNES (SANS OVERLAP)
    // ==========================================
    private class ModernBarChart extends JPanel {
        private String title;
        private Map<String, Double> data;
        private Color color;

        public ModernBarChart(String title, Map<String, Double> data, Color color) {
            this.title = title; this.data = data; this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(700, 320));
            setMaximumSize(new Dimension(2000, 320));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            int y = 90; // Abaissé à 90 pour éviter tout chevauchement avec le titre
            for (Map.Entry<String, Double> e : data.entrySet()) {
                int barW = (int) ((e.getValue() / max) * (getWidth() - 420));
                if (barW < 5) barW = 5;

                g2d.setColor(new Color(0,0,0,100));
                g2d.fillRoundRect(240, y+4, barW, 20, 10, 10);

                GradientPaint gp = new GradientPaint(240, y, color, 240 + barW, y, color.brighter());
                g2d.setPaint(gp);
                g2d.fillRoundRect(240, y, barW, 20, 10, 10);

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 13));
                String label = e.getKey().length() > 28 ? e.getKey().substring(0, 25) + "..." : e.getKey();
                g2d.drawString(label, 20, y + 15);

                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("%,d", e.getValue().intValue()), 250 + barW, y + 15);
                y += 42;
            }
        }
    }

    // ==========================================
    // COMPOSANT 2 : LE GINI-METER
    // ==========================================
    private class GiniMeterSection extends JPanel {
        private String title;
        private Map<String, Double> data;

        public GiniMeterSection(String title, Map<String, Double> data) {
            this.title = title; this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(700, 380));
            setMaximumSize(new Dimension(2000, 380));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.setColor(Color.GRAY);
            g2d.drawString("0.0 = Égalité parfaite de fréquentation | 1.0 = Centralisation totale sur un musée", 20, 52);

            int x = 40; int y = 105;
            int count = 0;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                if (count++ > 7) break;

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                g2d.drawString(e.getKey(), x, y - 10);

                g2d.setColor(new Color(50, 50, 50));
                g2d.fillRoundRect(x, y, 140, 12, 5, 5);

                float hue = (float) (0.3 - (e.getValue() * 0.3));
                g2d.setColor(Color.getHSBColor(hue, 0.8f, 0.9f));

                int valW = (int) (e.getValue() * 140);
                g2d.fillRoundRect(x, y, valW, 12, 5, 5);

                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("%.3f", e.getValue()), x + 148, y + 11);

                x += 240;
                if (x > getWidth() - 220) { x = 40; y += 75; }
            }
        }
    }

    // ==========================================
    // COMPOSANT 3 : DONUT CHART COMPLET ET PARFAIT A 100%
    // ==========================================
    private class DonutChartPanel extends JPanel {
        private String title;
        private Map<String, Double> data;

        // Palette étendue de 18 couleurs distinctes pour couvrir toutes les régions sans répétition directe
        private Color[] colors = {
                new Color(28, 86, 120),   new Color(106, 28, 120),  new Color(162, 108, 39),
                new Color(46, 204, 113),  new Color(231, 76, 60),   new Color(241, 196, 15),
                new Color(26, 188, 156),  new Color(155, 89, 182),  new Color(52, 152, 219),
                new Color(230, 126, 34),  new Color(52, 73, 94),    new Color(149, 165, 166),
                new Color(27, 79, 114),   new Color(110, 44, 0),    new Color(19, 141, 117),
                new Color(125, 102, 8),   new Color(90, 13, 132),   new Color(120, 120, 120)
        };

        public DonutChartPanel(String title, Map<String, Double> data) {
            this.title = title; this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(700, 430));
            setMaximumSize(new Dimension(2000, 430));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            int size = 220;
            int cx = 40; int cy = 80;

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total == 0) return;

            // --- ALGORITHME DE CORRECTION POUR UN TOTAL DE STRICTEMENT 100% ---
            Map<String, Double> percentages = new LinkedHashMap<>();
            double totalRounded = 0;
            String mainKey = null;
            double maxPct = -1;

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double pct = (entry.getValue() / total) * 100.0;
                double roundedPct = Math.round(pct * 10) / 10.0; // Arrondi à 1 décimale
                percentages.put(entry.getKey(), roundedPct);
                totalRounded += roundedPct;

                if (roundedPct > maxPct) {
                    maxPct = roundedPct;
                    mainKey = entry.getKey();
                }
            }

            // Correction de l'écart d'arrondi sur la plus grosse valeur
            double diff = 100.0 - totalRounded;
            if (Math.abs(diff) > 0.01 && mainKey != null) {
                percentages.put(mainKey, Math.round((percentages.get(mainKey) + diff) * 10) / 10.0);
            }

            // Dessin des sections du Donut
            double curAngle = 0;
            int colorIdx = 0;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                double angle = (e.getValue() / total) * 360.0;
                g2d.setColor(colors[colorIdx % colors.length]);
                g2d.fill(new Arc2D.Double(cx, cy, size, size, curAngle, angle, Arc2D.PIE));
                curAngle += angle;
                colorIdx++;
            }

            // --- LÉGENDE DE TOUTES LES RÉGIONS SUR 2 COLONNES ---
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            int lxStart = cx + size + 40;
            int lyStart = cy + 10;
            int itemIdx = 0;

            for (Map.Entry<String, Double> e : percentages.entrySet()) {
                int col = itemIdx / 9; // Max 9 lignes par colonne
                int row = itemIdx % 9;

                int lx = lxStart + (col * 210);
                int ly = lyStart + (row * 24);

                // Dessin du point rond à la place de la box
                g2d.setColor(colors[itemIdx % colors.length]);
                g2d.fillOval(lx, ly + 2, 11, 11);

                // Label textuel
                g2d.setColor(Color.WHITE);
                String label = e.getKey();
                if (label.length() > 18) label = label.substring(0, 16) + "..";
                g2d.drawString(label + " (" + String.format(Locale.US, "%.1f", e.getValue()) + "%)", lx + 20, ly + 12);

                itemIdx++;
            }

            // Centre du Donut évidé pour le look moderne
            g2d.setColor(BACKGROUND_SECONDARY_COLOR);
            g2d.fillOval(cx + size/4, cy + size/4, size/2, size/2);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("TOTAL", cx + (size - fm.stringWidth("TOTAL")) / 2, cy + (size / 2) - 3);
            g2d.drawString("100%", cx + (size - fm.stringWidth("100%")) / 2, cy + (size / 2) + 15);
        }
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX(); int y = e.getY();
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
                if (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON) {
                    anInterface.show("HOME");
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX(); int y = e.getY();
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
                BACK_BUTTON_HOVER = (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON);
                setCursor(Cursor.getPredefinedCursor(BACK_BUTTON_HOVER ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                repaint();
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
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE * 2);
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(0, BORDER_SIZE, getWidth(), BORDER_SIZE);
        g2d.drawLine(0, getHeight() - BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE);

        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
        g2d.setColor(BACK_BUTTON_HOVER ? RETURN_HOVER_COLOR : RETURN_COLOR);
        g2d.fillRoundRect(MARGIN_BACK, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("<", MARGIN_BACK + (SIZE_BACK_BUTTON - fm.stringWidth("<")) / 2, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());

        g2d.drawString("DONNÉES GLOBALES DE L'APPLICATION", MARGIN_BACK + SIZE_BACK_BUTTON + 20, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());
    }
    // --- MÉTHODE POUR RENDRE LA SCROLLBAR MODERNE ---
    private void applyModernScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setOpaque(false);
        verticalBar.setPreferredSize(new Dimension(10, 0)); // Largeur fine (10px)

        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Rendre le fond (le rail) totalement transparent
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                // Dessiner le curseur (arrondi et semi-transparent)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 100)); // Blanc avec 40% d'opacité
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton(); // Supprime la flèche du haut
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton(); // Supprime la flèche du bas
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }
}