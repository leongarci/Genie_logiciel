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
    private MuseeStatsDAO statsDAO;

    // Couleurs de base de l'application
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color LINE_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);

    // Palette Funky thématique pour les composants
    private final Color ACCENT_GOLD = new Color(162, 108, 39);
    private final Color ACCENT_BLUE = new Color(28, 86, 120);
    private final Color ACCENT_GREEN = new Color(46, 204, 113);
    private final Color ACCENT_ORANGE = new Color(230, 126, 34);
    private final Color ACCENT_PINK = new Color(235, 104, 160);
    private final Color ACCENT_PURPLE = new Color(142, 68, 173);

    private final int BORDER_SIZE = 75;
    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15;
    private boolean BACK_BUTTON_HOVER = false;

    private JPanel chartsContainer;
    private JScrollPane scrollPane;

    // Composant dynamique pour le choix de la région
    private JComboBox<String> regionSelector;
    private ModernBarChart regionalTopChart;

    public StatsPage(Interface anInterface) {
        this.anInterface = anInterface;
        this.statsDAO = new MuseeStatsDAO();
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
        // Ajout d'une marge en haut pour faire respirer le premier graphique
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        scrollPane = new JScrollPane(chartsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(28);
        applyModernScrollBar(scrollPane);

        add(scrollPane);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scrollPane.setBounds(30, BORDER_SIZE + 20, getWidth() - 60, getHeight() - (BORDER_SIZE * 2) - 40);
            }
        });
    }

    private void loadDataAndBuildCharts() {
        chartsContainer.removeAll();

        // --- 1. TOP 5 NATIONAL (Barres) ---
        List<Carte> topMusees = statsDAO.getTopMuseesByValeur(5);
        Map<String, Double> mapTop = new LinkedHashMap<>();
        for (Carte c : topMusees) mapTop.put(c.getNomOfficiel(), (double) c.getTotal());
        chartsContainer.add(new ModernBarChart("• Les Établissements les Plus Visités de France (Fréquentation Nationale)", mapTop, ACCENT_GOLD));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 2. TOP 5 RÉGIONAL INTERACTIF (Barres avec JComboBox Dynamique) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("• Filtrer le Top 5 par région :");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        filterLabel.setForeground(Color.WHITE);

        // Récupération dynamique de toutes les régions présentes en BDD !
        Set<String> regionSet = statsDAO.giniEntreesParRegion().keySet();
        List<String> regionList = new ArrayList<>(regionSet);
        Collections.sort(regionList); // Tri alphabétique

        regionSelector = new JComboBox<>(regionList.toArray(new String[0]));
        regionSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        regionSelector.addActionListener(e -> updateRegionalChart());

        filterPanel.add(filterLabel);
        filterPanel.add(regionSelector);
        chartsContainer.add(filterPanel);
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        regionalTopChart = new ModernBarChart("Fréquentation Locale Détaillée", new LinkedHashMap<>(), ACCENT_BLUE);
        chartsContainer.add(regionalTopChart);
        if(regionSelector.getItemCount() > 0) {
            updateRegionalChart(); // Premier chargement
        }
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 3. CORRÉLATION RICHESSE / GRATUITÉ (Nuage de Points) ---
        Map<String, double[]> corrRichesseGratuite = statsDAO.correlationRichesseTauxGratuite();
        chartsContainer.add(new ScatterChartPanel("• Analyse Richesse vs Gratuité (Revenu Médian vs Part d'Entrées Gratuites)", corrRichesseGratuite, "Revenu Fiscal Médian (€)", "Taux de Gratuité (0.0 à 1.0)", ACCENT_PINK));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 4. CORRÉLATION RICHESSE / ENTRÉES PAR HABITANT (Nuage de Points) ---
        Map<String, double[]> corrRichesseHabitant = statsDAO.correlationRichesseEntreesParHabitant();
        chartsContainer.add(new ScatterChartPanel("• Intensité Culturelle (Revenu Médian vs Nombre de Visites par Habitant)", corrRichesseHabitant, "Revenu Fiscal Médian (€)", "Entrées par Habitant", ACCENT_BLUE));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 5. CHAMPIONS DE LA JEUNESSE (Barres) ---
        List<Map.Entry<Carte, Double>> topJeunes = statsDAO.topMuseesAccessiblesJeunes(5);
        Map<String, Double> mapJeunes = new LinkedHashMap<>();
        for (Map.Entry<Carte, Double> e : topJeunes) mapJeunes.put(e.getKey().getNomOfficiel(), e.getValue());
        chartsContainer.add(new ModernBarChart("• Les Musées les Plus Accessibles aux Jeunes (Indice Ajusté par Richesse)", mapJeunes, ACCENT_GREEN));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 6. DIVERSITÉ CULTURELLE (Barres) ---
        Map<String, Double> mapDiv = statsDAO.diversiteCulturelleParRegion();
        chartsContainer.add(new ModernBarChart("• Indice de Diversité Thématique par Territoire (Indice de Shannon)", mapDiv, ACCENT_PURPLE));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 7. INDICE DE GINI (Jauges SANS limite) ---
        Map<String, Double> giniData = statsDAO.giniEntreesParRegion();
        chartsContainer.add(new GiniMeterSection("• Analyse des Monopoles : Inégalité de Fréquentation au Sein des Régions", giniData));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 8. EFFET DE LEVIER TOURISTIQUE (Barres) ---
        Map<String, Double> mapLevier = statsDAO.effetLevierTouristique();
        chartsContainer.add(new ModernBarChart("• Indice d'Attractivité : L'Effet de Levier Touristique Régional", mapLevier, ACCENT_ORANGE));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 9. RÉPARTITION DES ENTRÉES NATIONALES (L'Anneau Propre à 100%) ---
        chartsContainer.add(new DonutChartPanel("• Structure Globale des Publics (Entrées Payantes vs Gratuites)"));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 10. CORRÉLATION JEUNES / SCOLAIRES (Nuage de Points) ---
        Map<String, double[]> corrJeunesScolaires = statsDAO.correlationPopulationJeuneEntreesScolaires();
        chartsContainer.add(new ScatterChartPanel("• Impact Éducatif (Proportion de Population Jeune vs Taux d'Entrées Scolaires)", corrJeunesScolaires, "Part des 0-24 ans dans la population", "Taux d'Entrées Scolaires au Musée", ACCENT_GREEN));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        chartsContainer.revalidate();
        chartsContainer.repaint();
    }

    private void updateRegionalChart() {
        if(regionSelector.getSelectedItem() == null) return;
        String selectedRegion = (String) regionSelector.getSelectedItem();
        List<Carte> topRegional = statsDAO.getTopMuseesByRegion(selectedRegion, 5);
        Map<String, Double> mapReg = new LinkedHashMap<>();
        for (Carte c : topRegional) mapReg.put(c.getNomOfficiel(), (double) c.getTotal());

        regionalTopChart.setData(mapReg);
    }

    // ==========================================
    // COMPOSANT 1 : GRAPH COMPLET EN BARRES
    // ==========================================
    private class ModernBarChart extends JPanel {
        private String title;
        private Map<String, Double> data;
        private Color color;

        public ModernBarChart(String title, Map<String, Double> data, Color color) {
            this.title = title; this.data = data; this.color = color;
            setOpaque(false);
            updateHeight();
        }

        public void setData(Map<String, Double> newData) {
            this.data = newData;
            updateHeight();
            repaint();
        }

        private void updateHeight() {
            int calculatedHeight = Math.max(160, 80 + (data.size() * 42));
            setPreferredSize(new Dimension(700, calculatedHeight));
            setMinimumSize(new Dimension(700, calculatedHeight));
            setMaximumSize(new Dimension(2000, calculatedHeight));
            if (getParent() != null) getParent().revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            if (data.isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 14));
                g2d.drawString("Aucune donnée disponible pour cette sélection.", 40, 70);
                return;
            }

            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            int y = 70;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                int barW = (int) ((e.getValue() / max) * (getWidth() - 420));
                if (barW < 5) barW = 5;

                g2d.setColor(new Color(0,0,0,120));
                g2d.fillRoundRect(240, y+4, barW, 20, 8, 8);

                GradientPaint gp = new GradientPaint(240, y, color, 240 + barW, y, color.brighter());
                g2d.setPaint(gp);
                g2d.fillRoundRect(240, y, barW, 20, 8, 8);

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 13));
                String label = e.getKey().length() > 28 ? e.getKey().substring(0, 25) + "..." : e.getKey();
                g2d.drawString(label, 20, y + 15);

                g2d.setColor(Color.WHITE);
                String valStr = (e.getValue() == Math.floor(e.getValue())) ?
                        String.format("%,d", e.getValue().intValue()) : String.format(Locale.US, "%.3f", e.getValue());
                g2d.drawString(valStr, 250 + barW, y + 15);
                y += 42;
            }
        }
    }

    // ==========================================
    // COMPOSANT 2 : NUAGES DE POINTS
    // ==========================================
    private class ScatterChartPanel extends JPanel {
        private String title, xLabel, yLabel;
        private Map<String, double[]> data;
        private Color pointColor;

        public ScatterChartPanel(String title, Map<String, double[]> data, String xLabel, String yLabel, Color pointColor) {
            this.title = title; this.data = data; this.xLabel = xLabel; this.yLabel = yLabel; this.pointColor = pointColor;
            setOpaque(false);
            setPreferredSize(new Dimension(700, 380));
            setMinimumSize(new Dimension(700, 380));
            setMaximumSize(new Dimension(2000, 380));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            if (data == null || data.isEmpty()) return;

            int padLeft = 75, padRight = 40, padTop = 60, padBottom = 60;
            int graphW = getWidth() - padLeft - padRight;
            int graphH = getHeight() - padTop - padBottom;

            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

            for (double[] vals : data.values()) {
                if (vals[0] < minX) minX = vals[0]; if (vals[0] > maxX) maxX = vals[0];
                if (vals[1] < minY) minY = vals[1]; if (vals[1] > maxY) maxY = vals[1];
            }
            minX *= 0.95; maxX *= 1.05; minY *= 0.95; maxY *= 1.05;

            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.setStroke(new BasicStroke(1f));
            for (int i = 1; i <= 4; i++) {
                int yGrid = padTop + (graphH * i / 5);
                int xGrid = padLeft + (graphW * i / 5);
                g2d.drawLine(padLeft, yGrid, padLeft + graphW, yGrid);
                g2d.drawLine(xGrid, padTop, xGrid, padTop + graphH);
            }

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(padLeft, padTop + graphH, padLeft + graphW, padTop + graphH);
            g2d.drawLine(padLeft, padTop, padLeft, padTop + graphH);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(xLabel, padLeft + graphW / 2 - 50, padTop + graphH + 35);

            AffineTransform orig = g2d.getTransform();
            g2d.rotate(-Math.PI / 2);
            g2d.drawString(yLabel, -padTop - graphH / 2 - 50, padLeft - 45);
            g2d.setTransform(orig);

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (Map.Entry<String, double[]> entry : data.entrySet()) {
                double[] vals = entry.getValue();
                int x = padLeft + (int) (((vals[0] - minX) / (maxX - minX)) * graphW);
                int y = padTop + graphH - (int) (((vals[1] - minY) / (maxY - minY)) * graphH);

                g2d.setColor(new Color(pointColor.getRed(), pointColor.getGreen(), pointColor.getBlue(), 60));
                g2d.fillOval(x - 7, y - 7, 14, 14);

                g2d.setColor(pointColor);
                g2d.fillOval(x - 3, y - 3, 6, 6);

                g2d.setColor(new Color(200, 200, 200, 180));
                String label = entry.getKey().length() > 6 ? entry.getKey().substring(0, 5) + "." : entry.getKey();
                g2d.drawString(label, x + 8, y + 4);
            }
        }
    }

    // ==========================================
    // COMPOSANT 3 : LE GINI-METER (Dynamique SANS LIMITE)
    // ==========================================
    private class GiniMeterSection extends JPanel {
        private String title;
        private Map<String, Double> data;

        public GiniMeterSection(String title, Map<String, Double> data) {
            this.title = title; this.data = data;
            setOpaque(false);

            // Calcul de la hauteur : 3 jauges par ligne
            int rowsCount = (int) Math.ceil(data.size() / 3.0);
            int calculatedHeight = 130 + (rowsCount * 75);

            setPreferredSize(new Dimension(700, calculatedHeight));
            setMinimumSize(new Dimension(700, calculatedHeight));
            setMaximumSize(new Dimension(2000, calculatedHeight));
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
            g2d.drawString("0.0 = Égalité totale de fréquentation | 1.0 = Un seul établissement capte tout", 20, 52);

            int x = 40; int y = 105;
            for (Map.Entry<String, Double> e : data.entrySet()) {

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                g2d.drawString(e.getKey(), x, y - 10);

                g2d.setColor(new Color(50, 50, 50));
                g2d.fillRoundRect(x, y, 140, 12, 5, 5); // Fond gris de la jauge

                float hue = (float) (0.3 - (e.getValue() * 0.3)); // Du vert (faible gini) au rouge (fort gini)
                g2d.setColor(Color.getHSBColor(hue, 0.8f, 0.9f));

                int valW = (int) (e.getValue() * 140);
                g2d.fillRoundRect(x, y, valW, 12, 5, 5); // Remplissage dynamique

                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("%.3f", e.getValue()), x + 148, y + 11);

                x += 220; // Espacement de 220px pour placer 3 éléments par ligne proprement
                if (x > getWidth() - 150) {
                    x = 40;
                    y += 75;
                }
            }
        }
    }

    // ==========================================
    // COMPOSANT 4 : ANNEAU PROPRE
    // ==========================================
    private class DonutChartPanel extends JPanel {
        private String title;
        private Color[] donutColors = { new Color(46, 204, 113), new Color(231, 76, 60) };

        public DonutChartPanel(String title) {
            this.title = title;
            setOpaque(false);
            setPreferredSize(new Dimension(700, 260));
            setMinimumSize(new Dimension(700, 260));
            setMaximumSize(new Dimension(2000, 260));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 20, 30);

            long totalGratuit = 0, totalPayant = 0;
            for (musee.RegionStats r : statsDAO.getRegionStats()) {
                totalGratuit += r.getEntreesGratuites();
                totalPayant += r.getEntreesPayantes();
            }
            long totalGlobal = totalGratuit + totalPayant;
            if (totalGlobal == 0) return;

            double pctGratuit = (double) totalGratuit / totalGlobal * 100.0;
            double pctPayant = 100.0 - pctGratuit;

            int size = 160;
            int cx = 60; int cy = 70;

            double angleGratuit = (pctGratuit / 100.0) * 360.0;
            g2d.setColor(donutColors[0]);
            g2d.fill(new Arc2D.Double(cx, cy, size, size, 0, angleGratuit, Arc2D.PIE));

            g2d.setColor(donutColors[1]);
            g2d.fill(new Arc2D.Double(cx, cy, size, size, angleGratuit, 360.0 - angleGratuit, Arc2D.PIE));

            g2d.setFont(new Font("Arial", Font.PLAIN, 13));
            int lx = cx + size + 50;

            g2d.setColor(donutColors[0]);
            g2d.fillOval(lx, cy + 30, 12, 12);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Entrées Gratuites (" + String.format(Locale.US, "%.1f", pctGratuit) + "%)", lx + 25, cy + 42);

            g2d.setColor(donutColors[1]);
            g2d.fillOval(lx, cy + 70, 12, 12);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Entrées Payantes (" + String.format(Locale.US, "%.1f", pctPayant) + "%)", lx + 25, cy + 82);

            g2d.setColor(BACKGROUND_SECONDARY_COLOR);
            g2d.fillOval(cx + size/4, cy + size/4, size/2, size/2);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("PUBLICS", cx + (size - fm.stringWidth("PUBLICS")) / 2, cy + size/2 + 5);
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
        g2d.setColor(new Color(86, 86, 86));
        g2d.fillRoundRect(MARGIN_BACK, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        g2d.setColor(BACK_BUTTON_HOVER ? RETURN_HOVER_COLOR : RETURN_COLOR);
        g2d.fillRoundRect(MARGIN_BACK + 3, backBtnY + 3, SIZE_BACK_BUTTON - 6, SIZE_BACK_BUTTON - 6, 10, 10);

        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("<", MARGIN_BACK + (SIZE_BACK_BUTTON - fm.stringWidth("<")) / 2, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());

        g2d.drawString("DONNÉES GLOBALES DU PATRIMOINE", MARGIN_BACK + SIZE_BACK_BUTTON + 20, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());
    }

    private void applyModernScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setOpaque(false);
        verticalBar.setPreferredSize(new Dimension(10, 0));

        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
                g2.dispose();
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton(); button.setPreferredSize(new Dimension(0, 0)); return button;
            }
        });
    }
}