package Interface.Page;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import Interface.Interface;
import auth.User;
import carte.Carte;
import carte.CartePossedee;
import carte.Rarete;
import collection.CollectionService;

public class InventoryPage extends JPanel {

    private Interface anInterface;
    private String currentRegion = "";

    // Données réelles
    private Map<String, List<CartePossedee>> groupedCards;

    // Couleurs
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

    // Composants UI
    private JTextArea detailsArea;
    private JScrollPane detailsScrollPane;
    private JPanel categoriesContainer;
    private JScrollPane scrollPane;

    public InventoryPage(Interface anInterface) {
        this.anInterface = anInterface;
        setLayout(null);
        setOpaque(false);

        // On utilise un LinkedHashMap pour garder l'ordre d'insertion
        groupedCards = new LinkedHashMap<>();

        initUI();
        setupMouseListeners();
    }

    private void initUI() {
        // --- Panneau de détails (à droite dans la zone noire) ---
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        detailsArea.setForeground(TEXT_COLOR);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setMargin(new Insets(10, 10, 10, 10));

        detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setOpaque(false);
        detailsScrollPane.getViewport().setOpaque(false);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        applyModernScrollBar(detailsScrollPane); // Applique le style aux deux barres

        // --- Conteneur des catégories (à gauche) ---
        categoriesContainer = new JPanel();
        categoriesContainer.setLayout(new BoxLayout(categoriesContainer, BoxLayout.Y_AXIS));
        categoriesContainer.setOpaque(false);

        scrollPane = new JScrollPane(categoriesContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        applyModernScrollBar(scrollPane); // Applique le style aux deux barres

        add(scrollPane);
        add(detailsScrollPane);

        // Positionnement réactif (Ratios cohérents)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                int marginX = Math.max(20, (int)(w * 0.04));
                int listWidth = (int)(w * 0.50); // 50% de la largeur pour la liste

                int rectX = (int)(w * 0.58); // Le bloc noir commence à 58%
                int rectW = w - rectX - marginX;
                int rectY = BORDER_SIZE + 10;
                int rectH = h - (BORDER_SIZE * 2) - 20;

                scrollPane.setBounds(marginX, BORDER_SIZE + 20, listWidth, h - (BORDER_SIZE * 2) - 40);
                detailsScrollPane.setBounds(rectX + 15, rectY + 15, rectW - 30, rectH - 30);
            }
        });
    }

    // --- CHARGEMENT DYNAMIQUE DES CARTES ---
    public void loadRegion(String region) {
        this.currentRegion = region;
        fetchAndFilterCards();
        resetDetailsText();
        populateCategories();
    }

    private void fetchAndFilterCards() {
        groupedCards.clear();
        User user = anInterface.getUser();
        if (user == null) return;

        CollectionService service = new CollectionService();
        List<CartePossedee> allCards = service.getCollectionUtilisateur(user.getId());

        for (CartePossedee cp : allCards) {
            Carte c = cp.getCarte();
            if (isSameRegion(currentRegion, c.getRegion()) || isSameRegion(currentRegion, c.getDepartement())) {

                String[] themes = c.getThemes();
                String category = "AUTRES";

                if (themes != null && themes.length > 0) {
                    category = normalizeCategory(themes[0]);
                }

                List<CartePossedee> list = groupedCards.computeIfAbsent(category, k -> new ArrayList<>());
                if (!list.contains(cp)) {
                    list.add(cp);
                }
            }
        }

        // --- TRI DES CARTES PAR RARETÉ ---
        for (List<CartePossedee> list : groupedCards.values()) {
            list.sort((cp1, cp2) -> Integer.compare(
                    getRareteWeight(cp1.getCarte().getRarete()),
                    getRareteWeight(cp2.getCarte().getRarete())
            ));
        }
    }

    private int getRareteWeight(Rarete r) {
        if (r == null) return 0;
        switch (r) {
            case COMMUN: return 1;
            case RARE: return 2;
            case EPIQUE: return 3;
            case LEGENDAIRE: return 4;
            default: return 0;
        }
    }

    private boolean isSameRegion(String mapRegion, String dbRegion) {
        if (mapRegion == null || dbRegion == null) return false;
        String n1 = mapRegion.toLowerCase().replaceAll("[-_ ]", "").replaceAll("[éèêë]", "e").replaceAll("[îï]", "i").replaceAll("[ôö]", "o").replaceAll("[àâä]", "a");
        String n2 = dbRegion.toLowerCase().replaceAll("[-_ ]", "").replaceAll("[éèêë]", "e").replaceAll("[îï]", "i").replaceAll("[ôö]", "o").replaceAll("[àâä]", "a");
        return n1.equals(n2) || n1.contains(n2) || n2.contains(n1);
    }

    private String normalizeCategory(String dbTheme) {
        if (dbTheme == null || dbTheme.isEmpty()) return "AUTRES";

        String themeNettoye = dbTheme.replace("\"", "");
        String premiereCategorie = themeNettoye.split(",")[0].trim();
        String t = premiereCategorie.toLowerCase();

        if (t.contains("archéologie") || t.contains("archeologie")) return "ARCHÉOLOGIE";
        if (t.contains("déco") || t.contains("deco")) return "ART DÉCORATIF";
        if (t.contains("beaux")) return "BEAUX ARTS";
        if (t.contains("technique") || t.contains("industrie")) return "TECHNIQUE";
        if (t.contains("moderne") || t.contains("contemporain")) return "ARTS MODERNE";
        if (t.contains("ethnologie")) return "ETHNOLOGIE";
        if (t.contains("histoire")) return "HISTOIRE";

        return "AUTRES";
    }

    private void resetDetailsText() {
        detailsArea.setText("• RÉGION : " + currentRegion.toUpperCase() + "\n\n" +
                "Sélectionnez une carte dans le menu de gauche pour afficher les détails du musée.\n\n");
    }

    // --- CRÉATION DE L'INTERFACE GAUCHE ---
    private void populateCategories() {
        categoriesContainer.removeAll();

        String[] ordreCategories = {"ARCHÉOLOGIE", "ART DÉCORATIF", "BEAUX ARTS", "ARTS MODERNE", "ETHNOLOGIE", "HISTOIRE", "TECHNIQUE", "SCIENCES", "AUTRES"};
        boolean aDesCartes = false;

        for (String cat : ordreCategories) {
            if (groupedCards.containsKey(cat)) {
                aDesCartes = true;
                categoriesContainer.add(createCategoryPanel(cat, groupedCards.get(cat)));
                categoriesContainer.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }

        if (!aDesCartes) {
            JLabel emptyLabel = new JLabel("Aucune carte débloquée pour cette région.");
            emptyLabel.setForeground(Color.LIGHT_GRAY);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            categoriesContainer.add(emptyLabel);
        }

        categoriesContainer.revalidate();
        categoriesContainer.repaint();
    }

    private JPanel createCategoryPanel(String categoryName, List<CartePossedee> cards) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        header.setOpaque(false);

        // CHARGEMENT IMAGE CATÉGORIE
        JLabel icon = new JLabel();
        String catFileName = "cat_" + categoryName.toLowerCase().replace(" ", "_") + ".png";

        try {
            java.net.URL imgURL = getClass().getResource("/Interface/Page/ImagesCatégories/" + catFileName);
            Image image = null;

            if (imgURL != null) {
                image = new ImageIcon(imgURL).getImage();
            } else {
                image = new ImageIcon("src/main/java/Interface/Page/ImagesCatégories/" + catFileName).getImage();
            }

            if (image != null && image.getWidth(null) != -1) {
                icon.setIcon(new ImageIcon(image.getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
            } else {
                icon.setText("🔘");
                icon.setFont(new Font("Arial", Font.PLAIN, 30));
            }
        } catch (Exception e) { icon.setText("🔘"); }

        JLabel title = new JLabel(categoryName);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton toggleBtn = new JButton("▼");
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setForeground(Color.GRAY);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 18));
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(icon);
        header.add(title);
        header.add(toggleBtn);

        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 5, getWidth() - 50, 2);
            }
        };
        line.setMaximumSize(new Dimension(800, 15));
        line.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false);

        content.add(Box.createRigidArea(new Dimension(0, 10)));
        for (CartePossedee cp : cards) {
            content.add(createCardItem(cp));
        }

        toggleBtn.addActionListener(e -> {
            boolean isVisible = content.isVisible();
            content.setVisible(!isVisible);
            toggleBtn.setText(isVisible ? "▼" : "▲");
            categoriesContainer.revalidate();
        });

        title.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { toggleBtn.doClick(); }
            public void mouseEntered(MouseEvent e) { title.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        });

        panel.add(header);
        panel.add(line);
        panel.add(content);
        return panel;
    }

    private JPanel createCardItem(CartePossedee cp) {
        Carte carte = cp.getCarte();
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        item.setOpaque(false);
        item.add(Box.createRigidArea(new Dimension(50, 0)));

        // CHARGEMENT IMAGE RARETÉ
        JLabel rarityIcon = new JLabel();

        // Traduction de la Rareté Java vers tes noms de fichiers
        String rarityFileName = "Bronze_cropped.png"; // Valeur par défaut (COMMUN)
        if (carte.getRarete() != null) {
            switch (carte.getRarete()) {
                case COMMUN: rarityFileName = "Bronze_cropped.png"; break;
                case RARE: rarityFileName = "Argent_cropped.png"; break;
                case EPIQUE: rarityFileName = "Or_cropped.png"; break;
                case LEGENDAIRE: rarityFileName = "Ultime_cropped.png"; break;
            }
        }

        try {
            java.net.URL rImgURL = getClass().getResource("/Interface/Page/ImagesRaretés/" + rarityFileName);
            Image image = null;

            if (rImgURL != null) {
                image = new ImageIcon(rImgURL).getImage();
            } else {
                image = new ImageIcon("src/main/java/Interface/Page/ImagesRaretés/" + rarityFileName).getImage();
            }

            if (image != null && image.getWidth(null) != -1) {
                rarityIcon.setIcon(new ImageIcon(image.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } else {
                rarityIcon.setText("●");
                rarityIcon.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            rarityIcon.setText("●");
            rarityIcon.setForeground(Color.WHITE);
        }

        String affichageNom = carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "Musée Inconnu";
        if(affichageNom.length() > 45) affichageNom = affichageNom.substring(0, 42) + "...";

        JButton museumBtn = new JButton(affichageNom.toUpperCase());
        museumBtn.setFont(new Font("Arial", Font.BOLD, 14));
        museumBtn.setContentAreaFilled(false);
        museumBtn.setForeground(Color.WHITE);
        museumBtn.setBorderPainted(false);
        museumBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        museumBtn.addActionListener(e -> {
            String qte = " (Possédé : " + cp.getQuantite() + ")";
            String details = "• MUSÉE : " + (carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "N/A") + qte + "\n\n" +
                    "• RARETÉ : " + (carte.getRarete() != null ? carte.getRarete().toString() : "N/A") + "\n" +
                    "• VILLE : " + (carte.getVille() != null ? carte.getVille() : "N/A") + " (" + (carte.getDepartement() != null ? carte.getDepartement() : "") + ")\n" +
                    "• ADRESSE : " + (carte.getAdresse() != null ? carte.getAdresse() : "N/A") + "\n\n" +
                    "• DOMAINE(S) : " + (carte.getDomaineThematique() != null ? carte.getDomaineThematique().replace("\"", "") : "N/A") + "\n\n" +
                    "• HISTOIRE :\n" + (carte.getHistoire() != null ? carte.getHistoire().replace("\"", "") : "Aucune information historique disponible.") + "\n\n" +
                    "• ATOUTS / INTÉRÊT :\n" + (carte.getAtout() != null ? carte.getAtout().replace("\"", "") : "") + " " +
                    (carte.getInteret() != null ? carte.getInteret().replace("\"", "") : "");

            detailsArea.setText(details);
            detailsArea.setCaretPosition(0);
        });

        item.add(rarityIcon);
        item.add(museumBtn);
        return item;
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

                if (x > MARGIN_BACK && x < MARGIN_BACK + SIZE_BACK_BUTTON && y > backBtnY && y < backBtnY + SIZE_BACK_BUTTON) {
                    anInterface.show("MAP");
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

        // Lignes de séparation haut et bas en gris foncé
        g2d.setColor(new Color(86, 86, 86));
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

        // RECTANGLE NOIR A DROITE (Panneau de détails) calculé avec les mêmes ratios
        int marginX = Math.max(20, (int)(w * 0.04));
        int rectX = (int)(w * 0.58);
        int rectW = w - rectX - marginX;

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(rectX, BORDER_SIZE + 10, rectW, h - (BORDER_SIZE * 2) - 20, 25, 25);
    }

    // --- MÉTHODE POUR RENDRE LES SCROLLBARS (VERTICALE ET HORIZONTALE) MODERNES ---
    private void applyModernScrollBar(JScrollPane scrollPane) {
        // Définition d'une classe interne locale réutilisable pour l'UI personnalisée
        class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Fond (rail) totalement transparent
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                // Curseur arrondi et semi-transparent (blanc 40%)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton(); // Supprime la flèche directionnelle de début
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton(); // Supprime la flèche directionnelle de fin
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        }

        // 1. Application à la barre verticale (Fine en largeur : 10px)
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setOpaque(false);
        verticalBar.setPreferredSize(new Dimension(10, 0));
        verticalBar.setUI(new ModernScrollBarUI());

        // 2. Application à la barre horizontale (Fine en hauteur : 10px)
        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
        horizontalBar.setOpaque(false);
        horizontalBar.setPreferredSize(new Dimension(0, 10));
        horizontalBar.setUI(new ModernScrollBarUI());
    }
}