package Interface.Page;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import Interface.Interface;
import carte.Carte;
import musee.MuseeStatsDAO;

public class StatsPage extends JPanel {

    private Interface anInterface;

    // Couleurs globales
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color LINE_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);

    // Constantes dimensions
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(scrollPane);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Le scrollPane prend la zone centrale verte
                scrollPane.setBounds(30, BORDER_SIZE + 20, getWidth() - 60, getHeight() - (BORDER_SIZE * 2) - 40);
            }
        });
    }

    private void loadDataAndBuildCharts() {
        MuseeStatsDAO statsDAO = new MuseeStatsDAO();
        chartsContainer.removeAll();

        // 1. Top 5 Musées (Entrées)
        List<Carte> topMusees = statsDAO.getTopMuseesByValeur(5);
        Map<String, Double> mapTop = new LinkedHashMap<>();
        for (Carte c : topMusees) {
            mapTop.put(c.getNomOfficiel(), (double) c.getTotal());
        }
        chartsContainer.add(new BarChartPanel("Top 5 Musées en France (Entrées Totales)", mapTop, new Color(162, 108, 39)));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Diversité Culturelle par Région
        Map<String, Double> mapDiv = statsDAO.diversiteCulturelleParRegion();
        chartsContainer.add(new BarChartPanel("Indice de Diversité Culturelle par Région", mapDiv, new Color(28, 86, 120)));
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // 3. Effet de Levier Touristique
        Map<String, Double> mapLevier = statsDAO.effetLevierTouristique();
        chartsContainer.add(new BarChartPanel("Effet de Levier Touristique Régional", mapLevier, new Color(200, 70, 70)));

        chartsContainer.revalidate();
        chartsContainer.repaint();
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

                if (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON) {
                    anInterface.show("HOME");
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

                boolean isBackHovered = (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON);
                if (BACK_BUTTON_HOVER != isBackHovered) {
                    BACK_BUTTON_HOVER = isBackHovered;
                    repaint();
                }
                setCursor(Cursor.getPredefinedCursor(isBackHovered ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
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

        int w = getWidth();
        int h = getHeight();

        // Fond global
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, w, h, 15, 15);

        // Zone centrale verte
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, w, h - BORDER_SIZE * 2);

        // Lignes
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(0, BORDER_SIZE, w, BORDER_SIZE);
        g2d.drawLine(0, h - BORDER_SIZE, w, h - BORDER_SIZE);

        // Bouton Retour "<"
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
        g2d.fillRoundRect(MARGIN_BACK, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        g2d.setColor(BACK_BUTTON_HOVER ? RETURN_HOVER_COLOR : RETURN_COLOR);
        g2d.fillRoundRect(MARGIN_BACK + 3, backBtnY + 3, SIZE_BACK_BUTTON - 6, SIZE_BACK_BUTTON - 6, 10, 10);
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("<", MARGIN_BACK + (SIZE_BACK_BUTTON - fm.stringWidth("<")) / 2, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());

        // Titre de la page
        g2d.drawString("DONNÉES GLOBALES", MARGIN_BACK + SIZE_BACK_BUTTON + 20, backBtnY + ((SIZE_BACK_BUTTON - fm.getHeight()) / 2) + fm.getAscent());
    }

    // ==========================================
    // COMPOSANT GRAPHIQUE SUR-MESURE (BarChart)
    // ==========================================
    private class BarChartPanel extends JPanel {
        private String title;
        private Map<String, Double> data;
        private Color barColor;

        public BarChartPanel(String title, Map<String, Double> data, Color barColor) {
            this.title = title;
            this.data = data;
            this.barColor = barColor;
            setOpaque(false);

            // Calcule la hauteur totale dynamiquement en fonction du nombre de barres
            int totalHeight = 80 + (data.size() * 40);
            setPreferredSize(new Dimension(700, totalHeight));
            setMinimumSize(new Dimension(700, totalHeight));
            setMaximumSize(new Dimension(2000, totalHeight)); // Permet de s'étirer en largeur
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Titre du graphique
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(title, 10, 30);

            if (data == null || data.isEmpty()) return;

            // Trouver la valeur maximale pour l'échelle
            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

            int y = 60;
            int barHeight = 22;
            int labelWidth = 250; // Place pour le texte à gauche

            g2d.setFont(new Font("Arial", Font.PLAIN, 13));

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String label = entry.getKey();
                if (label.length() > 32) label = label.substring(0, 29) + "...";
                double val = entry.getValue();

                // Dessin du Label (Texte gauche)
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(label, 10, y + 16);

                // Calcul de la largeur de la barre
                int maxBarWidth = getWidth() - labelWidth - 80; // 80 de marge à droite
                int barWidth = (int) ((val / max) * maxBarWidth);
                if(barWidth < 5) barWidth = 5; // Minimum visible

                // Dessin de la barre
                g2d.setColor(barColor);
                g2d.fillRoundRect(labelWidth, y, barWidth, barHeight, 8, 8);

                // Valeur au bout de la barre
                g2d.setColor(Color.WHITE);
                String valStr = (val == (long) val) ? String.format("%,d", (long)val) : String.format("%.3f", val);
                g2d.drawString(valStr, labelWidth + barWidth + 10, y + 16);

                y += barHeight + 18; // Espace entre les barres
            }
        }
    }
}