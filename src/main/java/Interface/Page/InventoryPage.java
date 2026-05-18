package Interface.Page;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Interface.Interface;

public class InventoryPage extends JPanel {

    private Interface anInterface;
    private String currentRegion = "";

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
    private JPanel categoriesContainer;
    private JScrollPane scrollPane;

    public InventoryPage(Interface anInterface) {
        this.anInterface = anInterface;
        setLayout(null);
        setOpaque(false);

        initUI();
        setupMouseListeners();
    }

    private void initUI() {
        // --- Panneau de détails (à droite dans la zone noire) ---
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false); // Transparent pour voir le rectangle noir dessiné
        detailsArea.setForeground(TEXT_COLOR);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setMargin(new Insets(10, 10, 10, 10));

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
        add(detailsArea);

        // Positionnement réactif
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                // Le JScrollPane (Categories) prend environ 60% de la largeur
                scrollPane.setBounds(20, BORDER_SIZE + 20, (int)(w * 0.6) - 20, h - (BORDER_SIZE * 2) - 40);

                // Le JTextArea (Détails) prend l'intérieur du rectangle noir
                int rectX = (int)(w * 0.65);
                int rectY = BORDER_SIZE + 20;
                int rectW = w - rectX - 20;
                int rectH = h - (BORDER_SIZE * 2) - 40;
                detailsArea.setBounds(rectX + 15, rectY + 15, rectW - 30, rectH - 30);
            }
        });
    }

    // Appelée par l'Interface quand on clique sur une région
    public void loadRegion(String region) {
        this.currentRegion = region;
        resetDetailsText();
        populateCategories();
    }

    private void resetDetailsText() {
        detailsArea.setText("RÉGION : " + currentRegion.toUpperCase() + "\n\n" +
                "INSÉRER TOUTES LES INFOS DE LA CARTE\n\n" +
                "Sélectionnez une carte dans le menu de gauche pour afficher les détails du musée.");
    }

    // Remplit la liste déroulante des catégories
    private void populateCategories() {
        categoriesContainer.removeAll();

        String[] categories = {"ARCHÉOLOGIE", "ART DÉCORATIF", "BEAUX ARTS", "TECHNIQUE", "ARTS MODERNE", "ETHNOLOGIE", "HISTOIRE"};

        for (String cat : categories) {
            categoriesContainer.add(createCategoryPanel(cat));
            categoriesContainer.add(Box.createRigidArea(new Dimension(0, 15))); // Espacement
        }

        categoriesContainer.revalidate();
        categoriesContainer.repaint();
    }

    // Crée un bloc accordéon pour une catégorie
    private JPanel createCategoryPanel(String categoryName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // HEADER
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        // Image de la catégorie (à remplacer par ton image)
        JLabel icon = new JLabel();
        try {
            ImageIcon ic = new ImageIcon(new ImageIcon("resources/images/cat_" + categoryName.toLowerCase().replace(" ", "_") + ".png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            icon.setIcon(ic);
        } catch (Exception e) {
            // Placeholder si l'image n'est pas trouvée
            icon.setText("🎨");
            icon.setFont(new Font("Arial", Font.PLAIN, 24));
        }

        JLabel title = new JLabel(categoryName);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        // Bouton déroulant
        JButton toggleBtn = new JButton("▼");
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setForeground(Color.GRAY);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(icon);
        header.add(title);
        header.add(toggleBtn);

        // CONTENU (Les cartes du musée)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false); // Caché par défaut

        // Ligne de séparation sous la catégorie
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), 2);
            }
        };
        line.setMaximumSize(new Dimension(800, 2));
        line.setOpaque(false);

        // Exemples de musées pour cette catégorie (données mockées)
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(createCardItem("Musée de l'Archéologie de Toulouse", "or"));
        content.add(createCardItem("Musée Ethnographique", "bronze"));
        content.add(createCardItem("Galerie d'Art Moderne", "ultime"));

        // Action d'ouverture/fermeture de l'accordéon
        toggleBtn.addActionListener(e -> {
            boolean isVisible = content.isVisible();
            content.setVisible(!isVisible);
            toggleBtn.setText(isVisible ? "▼" : "▲");
            categoriesContainer.revalidate();
        });

        // Clique sur le titre ouvre aussi
        title.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                toggleBtn.doClick();
            }
        });

        panel.add(header);
        panel.add(line);
        panel.add(content);
        return panel;
    }

    // Crée une ligne pour une carte de l'inventaire
    private JPanel createCardItem(String museumName, String rarity) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        item.setOpaque(false);

        // Indentation
        item.add(Box.createRigidArea(new Dimension(30, 0)));

        // Icone de rareté (bronze, argent, or, ultime)
        JLabel rarityIcon = new JLabel();
        try {
            ImageIcon rIcon = new ImageIcon(new ImageIcon("resources/images/rarity_" + rarity + ".png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            rarityIcon.setIcon(rIcon);
        } catch (Exception e) {
            rarityIcon.setText("●"); // Point si l'image n'est pas là
            rarityIcon.setForeground(Color.WHITE);
        }

        // Nom cliquable
        JButton museumBtn = new JButton(museumName.toUpperCase());
        museumBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        museumBtn.setContentAreaFilled(false);
        museumBtn.setForeground(Color.WHITE);
        museumBtn.setBorderPainted(false);
        museumBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Evénement au clic : mettre à jour le panneau de détails
        museumBtn.addActionListener(e -> {
            detailsArea.setText("MUSÉE : " + museumName.toUpperCase() + "\n\n" +
                    "RARETÉ : " + rarity.toUpperCase() + "\n\n" +
                    "Région : " + currentRegion + "\n\n" +
                    "Insérer ici toutes les informations détaillées récupérées de la base de données ou de l'API pour cette carte."
            );
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
                    anInterface.show("MAP"); // Retourne à la carte
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

                if (isBackHovered) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
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

        // RECTANGLE NOIR A DROITE (Panneau de détails)
        int rectX = (int)(w * 0.65);
        int rectY = BORDER_SIZE + 20;
        int rectW = w - rectX - 20;
        int rectH = h - (BORDER_SIZE * 2) - 40;

        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(rectX, rectY, rectW, rectH, 25, 25);
    }
}