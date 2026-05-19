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
    private final Color LINE_COLOR = new Color(255, 255, 255); // Lignes blanches comme la maquette
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

        // On utilise un LinkedHashMap pour garder l'ordre d'insertion (Archéo en premier, etc.)
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

        // --- Conteneur des catégories (à gauche) ---
        categoriesContainer = new JPanel();
        categoriesContainer.setLayout(new BoxLayout(categoriesContainer, BoxLayout.Y_AXIS));
        categoriesContainer.setOpaque(false);

        scrollPane = new JScrollPane(categoriesContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        add(detailsScrollPane);

        // Positionnement réactif
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                // Catégories (gauche) prennent environ 55% de l'espace
                scrollPane.setBounds(30, BORDER_SIZE + 20, (int)(w * 0.55) - 30, h - (BORDER_SIZE * 2) - 40);

                // Détails (droite dans le rectangle noir)
                int rectX = (int)(w * 0.60);
                int rectY = BORDER_SIZE + 10;
                int rectW = w - rectX - 20;
                int rectH = h - (BORDER_SIZE * 2) - 20;
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
            // On vérifie la région ET le département (utile pour les DROM de l'Outre-Mer)
            if (isSameRegion(currentRegion, c.getRegion()) || isSameRegion(currentRegion, c.getDepartement())) {

                String[] themes = c.getThemes();
                String category = "AUTRES";

                // On ne prend QUE le premier thème pour éviter les doublons
                if (themes != null && themes.length > 0) {
                    category = normalizeCategory(themes[0]);
                }

                List<CartePossedee> list = groupedCards.computeIfAbsent(category, k -> new ArrayList<>());
                if (!list.contains(cp)) {
                    list.add(cp);
                }
            }
        }

        // --- TRI DES CARTES PAR RARETÉ (Commune -> Rare -> Epique -> Legendaire) ---
        for (List<CartePossedee> list : groupedCards.values()) {
            list.sort((cp1, cp2) -> Integer.compare(
                    getRareteWeight(cp1.getCarte().getRarete()),
                    getRareteWeight(cp2.getCarte().getRarete())
            ));
        }
    }

    /**
     * Assigne un poids numérique aux raretés pour trier facilement
     */
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

        // 1. On nettoie les guillemets éventuels
        String themeNettoye = dbTheme.replace("\"", "");

        // 2. On prend uniquement ce qui est avant la première virgule
        String premiereCategorie = themeNettoye.split(",")[0].trim();

        // 3. On normalise pour faire correspondre avec tes icônes
        String t = premiereCategorie.toLowerCase();

        if (t.contains("archéologie") || t.contains("archeologie")) return "ARCHÉOLOGIE";
        if (t.contains("déco") || t.contains("deco")) return "ART DÉCORATIF";
        if (t.contains("beaux")) return "BEAUX ARTS";
        if (t.contains("technique") || t.contains("industrie")) return "TECHNIQUE";
        if (t.contains("moderne") || t.contains("contemporain")) return "ARTS MODERNE";
        if (t.contains("ethnologie")) return "ETHNOLOGIE";
        if (t.contains("histoire")) return "HISTOIRE";
        if (t.contains("nature") || t.contains("science")) return "SCIENCES";

        return "AUTRES";
    }

    private void resetDetailsText() {
        detailsArea.setText("RÉGION : " + currentRegion.toUpperCase() + "\n\n" +
                "Sélectionnez une carte dans le menu de gauche pour afficher les détails du musée.\n\n");
    }

    // --- CRÉATION DE L'INTERFACE GAUCHE ---
    private void populateCategories() {
        categoriesContainer.removeAll();

        // On crée un ordre d'affichage fixe pour l'esthétique
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

        // --- HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        header.setOpaque(false);

        // IMAGE CATÉGORIE
        JLabel icon = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/Interface/Page/Images/cat_" + categoryName.toLowerCase().replaceAll("[ éè]", "_") + ".png");
            if(imgURL != null) {
                ImageIcon ic = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));
                icon.setIcon(ic);
            } else {
                icon.setText("🔘"); // Secours
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

        // --- LIGNE BLANCHE SOUS LE TITRE ---
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 5, getWidth() - 50, 2); // 50px de marge à droite
            }
        };
        line.setMaximumSize(new Dimension(800, 15));
        line.setOpaque(false);

        // --- CONTENU DES CARTES ---
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false); // Menu accordéon fermé par défaut

        content.add(Box.createRigidArea(new Dimension(0, 10)));
        for (CartePossedee cp : cards) {
            content.add(createCardItem(cp));
        }

        // Actions du bouton
        toggleBtn.addActionListener(e -> {
            boolean isVisible = content.isVisible();
            content.setVisible(!isVisible);
            toggleBtn.setText(isVisible ? "▼" : "▲");
            categoriesContainer.revalidate();
        });

        // Clic sur le titre marche aussi !
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
        item.add(Box.createRigidArea(new Dimension(50, 0))); // Indentation par rapport au titre

        // IMAGE RARETÉ (bronze, argent, or, ultime)
        JLabel rarityIcon = new JLabel();
        try {
            String rareteStr = carte.getRarete() != null ? carte.getRarete().toString().toLowerCase() : "commun";
            java.net.URL rImgURL = getClass().getResource("/Interface/Page/Images/rarity_" + rareteStr + ".png");
            if (rImgURL != null) {
                ImageIcon rIcon = new ImageIcon(new ImageIcon(rImgURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                rarityIcon.setIcon(rIcon);
            } else {
                rarityIcon.setText("●");
                rarityIcon.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            rarityIcon.setText("●");
            rarityIcon.setForeground(Color.WHITE);
        }

        // NOM DU MUSÉE
        String affichageNom = carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "Musée Inconnu";
        if(affichageNom.length() > 45) affichageNom = affichageNom.substring(0, 42) + "...";

        JButton museumBtn = new JButton(affichageNom.toUpperCase());
        museumBtn.setFont(new Font("Arial", Font.BOLD, 14));
        museumBtn.setContentAreaFilled(false);
        museumBtn.setForeground(Color.WHITE);
        museumBtn.setBorderPainted(false);
        museumBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ACTION CLIC : Affichage des infos (SANS l'image de la carte à droite)
        museumBtn.addActionListener(e -> {
            String qte = " (Possédé : " + cp.getQuantite() + ")";
            String details = "🏛️ MUSÉE : " + (carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "N/A") + qte + "\n\n" +
                    "⭐ RARETÉ : " + (carte.getRarete() != null ? carte.getRarete().toString() : "N/A") + "\n" +
                    "📍 VILLE : " + (carte.getVille() != null ? carte.getVille() : "N/A") + " (" + (carte.getDepartement() != null ? carte.getDepartement() : "") + ")\n" +
                    "🏠 ADRESSE : " + (carte.getAdresse() != null ? carte.getAdresse() : "N/A") + "\n\n" +
                    "🎨 DOMAINE(S) : " + (carte.getDomaineThematique() != null ? carte.getDomaineThematique().replace("\"", "") : "N/A") + "\n\n" +
                    "📖 HISTOIRE :\n" + (carte.getHistoire() != null ? carte.getHistoire().replace("\"", "") : "Aucune information historique disponible.") + "\n\n" +
                    "✨ ATOUTS / INTÉRÊT :\n" + (carte.getAtout() != null ? carte.getAtout().replace("\"", "") : "") + " " +
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

        // RECTANGLE NOIR A DROITE (Panneau de détails)
        int rectX = (int)(w * 0.60);
        int rectY = BORDER_SIZE + 10;
        int rectW = w - rectX - 20;
        int rectH = h - (BORDER_SIZE * 2) - 20;

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(rectX, rectY, rectW, rectH, 25, 25);
    }
}