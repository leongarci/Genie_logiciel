package Interface.Page;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import Interface.Interface;
import auth.User;
import carte.Carte;
import carte.CartePossedee;
import collection.CollectionService;

public class InventoryPage extends JPanel {

    private Interface anInterface;
    private String currentRegion = "";

    // Données réelles
    private Map<String, List<CartePossedee>> groupedCards;

    // Couleurs
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color LINE_COLOR = new Color(86, 86, 86);
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
    private JScrollPane detailsScrollPane; // Ajouté pour faire défiler le long texte
    private JPanel categoriesContainer;
    private JScrollPane scrollPane;

    public InventoryPage(Interface anInterface) {
        this.anInterface = anInterface;
        setLayout(null);
        setOpaque(false);

        groupedCards = new HashMap<>();

        initUI();
        setupMouseListeners();
    }

    private void initUI() {
        // --- Panneau de détails (à droite dans la zone noire) ---
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false); // Transparent pour voir le rectangle noir
        detailsArea.setForeground(TEXT_COLOR);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setMargin(new Insets(10, 10, 10, 10));

        // On enveloppe la zone de texte dans un ScrollPane (L'histoire peut être longue)
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

                // Catégories (gauche)
                scrollPane.setBounds(20, BORDER_SIZE + 20, (int)(w * 0.55) - 20, h - (BORDER_SIZE * 2) - 40);

                // Détails (droite dans le rectangle noir)
                int rectX = (int)(w * 0.60);
                int rectY = BORDER_SIZE + 20;
                int rectW = w - rectX - 20;
                int rectH = h - (BORDER_SIZE * 2) - 40;
                detailsScrollPane.setBounds(rectX + 15, rectY + 15, rectW - 30, rectH - 30);
            }
        });
    }

    // Appelée par l'Interface quand on clique sur une région sur la Map
    public void loadRegion(String region) {
        this.currentRegion = region;
        fetchAndFilterCards(); // Récupère les vraies cartes
        resetDetailsText();
        populateCategories();
    }

    // --- LOGIQUE DE RÉCUPÉRATION DES CARTES ---
    private void fetchAndFilterCards() {
        groupedCards.clear();
        User user = anInterface.getUser();
        if (user == null) return;

        CollectionService service = new CollectionService();
        List<CartePossedee> allCards = service.getCollectionUtilisateur(user.getId());

        for (CartePossedee cp : allCards) {
            Carte c = cp.getCarte();
            // On vérifie si la carte correspond à la région cliquée
            if (isSameRegion(currentRegion, c.getRegion())) {
                String category = normalizeCategory(c.getDomaineThematique());
                groupedCards.computeIfAbsent(category, k -> new ArrayList<>()).add(cp);
            }
        }
    }

    // Normalise le nom de la région pour éviter les bugs (espaces, tirets, accents)
    private boolean isSameRegion(String mapRegion, String dbRegion) {
        if (mapRegion == null || dbRegion == null) return false;
        String n1 = mapRegion.toLowerCase().replaceAll("[-_ ]", "").replaceAll("[éèêë]", "e").replaceAll("[îï]", "i").replaceAll("[ôö]", "o").replaceAll("[àâä]", "a");
        String n2 = dbRegion.toLowerCase().replaceAll("[-_ ]", "").replaceAll("[éèêë]", "e").replaceAll("[îï]", "i").replaceAll("[ôö]", "o").replaceAll("[àâä]", "a");
        return n1.equals(n2) || n1.contains(n2) || n2.contains(n1);
    }

    // Assigne une grande catégorie selon les mots clés du domaine
    private String normalizeCategory(String dbTheme) {
        if (dbTheme == null) return "AUTRES";
        String t = dbTheme.toLowerCase();
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
        detailsArea.setText("RÉGION : " + currentRegion.toUpperCase() + "\n\n" +
                "Sélectionnez une carte dans le menu de gauche pour afficher les détails du musée.\n\n" +
                "Cartes débloquées ici : " + countTotalCardsInRegion());
    }

    private int countTotalCardsInRegion() {
        int count = 0;
        for (List<CartePossedee> list : groupedCards.values()) {
            count += list.size();
        }
        return count;
    }

    // --- CRÉATION DE L'INTERFACE DE LISTE ---
    private void populateCategories() {
        categoriesContainer.removeAll();

        // Afficher seulement les catégories pour lesquelles on a des cartes
        for (Map.Entry<String, List<CartePossedee>> entry : groupedCards.entrySet()) {
            categoriesContainer.add(createCategoryPanel(entry.getKey(), entry.getValue()));
            categoriesContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        if (groupedCards.isEmpty()) {
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

        // HEADER
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        // IMAGE CATÉGORIE (EMPLACEMENT)
        JLabel icon = new JLabel();
        try {
            // Remplace ce chemin par ton vrai chemin d'images pour les catégories
            java.net.URL imgURL = getClass().getResource("/Interface/Page/Images/cat_" + categoryName.toLowerCase().replace(" ", "_") + ".png");
            if(imgURL != null) {
                ImageIcon ic = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                icon.setIcon(ic);
            } else {
                icon.setText("🎨"); // Placeholder
                icon.setFont(new Font("Arial", Font.PLAIN, 24));
            }
        } catch (Exception e) {
            icon.setText("🎨");
        }

        JLabel title = new JLabel(categoryName + " (" + cards.size() + ")");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JButton toggleBtn = new JButton("▼");
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setForeground(Color.GRAY);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(icon);
        header.add(title);
        header.add(toggleBtn);

        // CONTENU
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false);

        // Ligne de séparation
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), 2);
            }
        };
        line.setMaximumSize(new Dimension(800, 2));
        line.setOpaque(false);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Ajout des vraies cartes
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

        item.add(Box.createRigidArea(new Dimension(30, 0))); // Indentation

        // IMAGE RARETÉ (EMPLACEMENT)
        JLabel rarityIcon = new JLabel();
        try {
            String rareteStr = carte.getRarete() != null ? carte.getRarete().toString().toLowerCase() : "commun";
            // Remplace ce chemin par tes icônes de rareté
            java.net.URL rImgURL = getClass().getResource("/Interface/Page/Images/rarity_" + rareteStr + ".png");
            if (rImgURL != null) {
                ImageIcon rIcon = new ImageIcon(new ImageIcon(rImgURL).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                rarityIcon.setIcon(rIcon);
            } else {
                // Placeholder textuel basé sur la rareté
                String symbol = rareteStr.equals("legendaire") ? "⭐" : rareteStr.equals("epique") ? "🟣" : rareteStr.equals("rare") ? "🔵" : "⚪";
                rarityIcon.setText(symbol);
                rarityIcon.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            rarityIcon.setText("●");
            rarityIcon.setForeground(Color.WHITE);
        }

        // Bouton Musée
        String affichageNom = carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "Musée Inconnu";
        // On tronque le nom s'il est trop long pour la liste de gauche
        if(affichageNom.length() > 40) affichageNom = affichageNom.substring(0, 37) + "...";

        JButton museumBtn = new JButton(affichageNom.toUpperCase());
        museumBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        museumBtn.setContentAreaFilled(false);
        museumBtn.setForeground(Color.WHITE);
        museumBtn.setBorderPainted(false);
        museumBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ACTION CLIC : Affichage des détails réels de la BDD
        museumBtn.addActionListener(e -> {
            String details = "🏛️ MUSÉE : " + (carte.getNomOfficiel() != null ? carte.getNomOfficiel() : "N/A") + "\n\n" +
                    "⭐ RARETÉ : " + (carte.getRarete() != null ? carte.getRarete().toString() : "N/A") + "\n" +
                    "🗃️ QUANTITÉ POSSÉDÉE : " + cp.getQuantite() + "\n" +
                    "📍 VILLE : " + (carte.getVille() != null ? carte.getVille() : "N/A") + "\n" +
                    "🏠 ADRESSE : " + (carte.getAdresse() != null ? carte.getAdresse() : "N/A") + "\n\n" +
                    "🎨 DOMAINE : " + (carte.getDomaineThematique() != null ? carte.getDomaineThematique() : "N/A") + "\n\n" +
                    "📖 HISTOIRE :\n" + (carte.getHistoire() != null ? carte.getHistoire() : "Aucune information historique disponible.") + "\n\n" +
                    "✨ ATOUTS / INTÉRÊT :\n" + (carte.getAtout() != null ? carte.getAtout() : "") + " " +
                    (carte.getInteret() != null ? carte.getInteret() : "");

            detailsArea.setText(details);
            detailsArea.setCaretPosition(0); // Remonte le scroll tout en haut
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

        // Zone centrale
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, w, h - BORDER_SIZE * 2);

        // Lignes de séparation
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

        // RECTANGLE NOIR A DROITE
        int rectX = (int)(w * 0.60);
        int rectY = BORDER_SIZE + 20;
        int rectW = w - rectX - 20;
        int rectH = h - (BORDER_SIZE * 2) - 40;

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(rectX, rectY, rectW, rectH, 25, 25);
    }
}