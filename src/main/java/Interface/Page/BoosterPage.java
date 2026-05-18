package Interface.Page;

import Interface.Interface;
import carte.Booster;
import carte.Carte;
import musee.EnumRegion;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoosterPage extends JPanel implements MouseListener, MouseMotionListener {
    private Interface anInterface;

    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color LINE_COLOR = new Color(86, 86, 86);
    private final Color BOOSTER_COLOR = new Color(50, 28, 86);
    private final Color BOOSTER_HOVER_COLOR = new Color(30, 17, 51);
    private final Color INVENTORY_COLOR = new Color(162, 108, 39);
    private final Color INVENTORY_HOVER_COLOR = new Color(112, 80, 28);
    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);

    private final int LINE_STROCK = 3;
    private final int BORDER_SIZE = 75;

    private int WIDTH_BOOSTER = 200;
    private int HEIGHT_BOOSTER = 300;
    private int WIDTH_BUTTON = 200;
    private final int HEIGHT_BUTTON = 50;
    private final int SIZE_POLICE = 20;

    private final int SIZE_CAROUSEL_BUTTON = 50;
    private final int MARGIN_CAROUSEL = 30;
    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15;

    private boolean BUTTON_HOVER = false;
    private boolean LEFT_BUTTON_HOVER = false;
    private boolean RIGHT_BUTTON_HOVER = false;
    private boolean BACK_BUTTON_HOVER = false;
    private boolean NEXT_BUTTON_HOVER = false;

    private boolean isOpening = false;
    private List<Carte> pulledCards = new ArrayList<>();
    private int currentCardIndex = 0;

    private boolean arrive = false;
    private EnumRegion[] regions = {
            EnumRegion.ILE_DE_FRANCE, EnumRegion.AUVERGNE, EnumRegion.PROVENCE,
            EnumRegion.LOIRE, EnumRegion.BOURGOGNE, EnumRegion.BRETAGNE,
            EnumRegion.CENTRE_VAL_DE_LOIRE, EnumRegion.CORSE, EnumRegion.GRAND_EST,
            EnumRegion.GUADELOUPE, EnumRegion.GUYANE, EnumRegion.HAUTS_DE_FRANCE,
            EnumRegion.REUNION, EnumRegion.MARTINIQUE, EnumRegion.MAYOTTE,
            EnumRegion.NORMANDIE, EnumRegion.AQUITAINE, EnumRegion.OCCITANIE
    };

    private int nbBooster;
    private int indexRegion = 0;

    private Map<String, Image> imageCache = new HashMap<>();

    public BoosterPage(Interface anInterface) {
        super(null);
        this.anInterface = anInterface;
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
        refrechNbBooster();
    }

    private void refrechNbBooster() {
        if (anInterface.getUser() != null) {
            nbBooster = anInterface.getUser().getNbBooster();
        }
    }

    private Image getImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        try {
            Image img = ImageIO.read(new File(path));
            imageCache.put(path, img);
            return img;
        } catch (IOException e) {
            imageCache.put(path, null);
            return null;
        }
    }

    private String getThemeImageName(String theme, String rarete) {
        String baseName = "Beaux-arts";
        if (theme != null) {
            String t = theme.toLowerCase();
            if (t.contains("archéologie") || t.contains("archeologie")) baseName = "Archéologie";
            else if (t.contains("moderne")) baseName = "Art Moderne";
            else if (t.contains("déco") || t.contains("deco")) baseName = "Art-deco-arts";
            else if (t.contains("ethnologie")) baseName = "Ethnologie";
            else if (t.contains("technique")) baseName = "Technique";
        }

        String suffix = "";
        if (rarete != null) {
            switch(rarete.toUpperCase()) {
                case "RARE": suffix = " (2)"; break;
                case "EPIQUE": suffix = " (3)"; break;
                case "LEGENDAIRE": suffix = " (4)"; break;
                default: suffix = ""; break;
            }
        }
        return baseName + suffix + ".png";
    }

    // Détection ultra-robuste de l'image de région
    private String getRegionImageName(String region) {
        if (region == null || region.equals("null")) return null;
        String rLower = region.toLowerCase();

        if (rLower.contains("ile") || rLower.contains("île")) return "Île de France_cropped.png";
        if (rLower.contains("auvergne") || rLower.contains("rhône")) return "Auvergne-Rhône-Alpes_cropped.png";
        if (rLower.contains("bourgogne") || rLower.contains("franche")) return "Bourgogne-Franche-Comté_cropped.png";
        if (rLower.contains("bretagne")) return "Bretagne_cropped.png";
        if (rLower.contains("centre") || rLower.contains("val")) return "Centre-Val-De-Loire_cropped.png";
        if (rLower.contains("corse")) return "Corse_cropped.png";
        if (rLower.contains("est")) return "Grand Est_cropped.png";
        if (rLower.contains("guadeloupe")) return "Guadeloupe_cropped.png";
        if (rLower.contains("guyane")) return "Guyane_cropped.png";
        if (rLower.contains("haut")) return "Haut-De-France_cropped.png";
        if (rLower.contains("martinique")) return "Martinique_cropped.png";
        if (rLower.contains("mayotte")) return "Mayotte_cropped.png";
        if (rLower.contains("normandie")) return "Normandie_cropped.png";
        if (rLower.contains("occitanie")) return "Occitanie_cropped.png";
        if (rLower.contains("pays") || rLower.contains("loire")) return "Pays-De-La-Loire_cropped.png";
        if (rLower.contains("provence") || rLower.contains("côte") || rLower.contains("azur")) return "Provence-Alpes-Côte-d'Azur_cropped.png";
        if (rLower.contains("réunion") || rLower.contains("reunion")) return "Réunion_cropped.png";
        if (rLower.contains("aquitaine")) return "Aquitaine_cropped.png";

        return region + "_cropped.png";
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!arrive) {
            refrechNbBooster();
            arrive = !arrive;
        }

        HEIGHT_BOOSTER = getHeight() - BORDER_SIZE * 2 - LINE_STROCK * 2 - 40 * 2;
        WIDTH_BOOSTER = (int) (HEIGHT_BOOSTER / 1.6);
        WIDTH_BUTTON = WIDTH_BOOSTER;

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE * 2);
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(LINE_STROCK));
        g2d.drawLine(0, BORDER_SIZE, getWidth(), BORDER_SIZE);
        g2d.drawLine(0, getHeight() - BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE);

        if (isOpening && pulledCards != null && !pulledCards.isEmpty()) {
            drawOpenedCard(g2d);
        } else {
            drawSealedBooster(g2d);
        }
    }

    private void drawSealedBooster(Graphics2D g2d) {
        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(backBtnX, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        if (BACK_BUTTON_HOVER) g2d.setColor(RETURN_HOVER_COLOR);
        else g2d.setColor(RETURN_COLOR);

        g2d.fillRoundRect(backBtnX + LINE_STROCK, backBtnY + LINE_STROCK, SIZE_BACK_BUTTON - LINE_STROCK * 2, SIZE_BACK_BUTTON - LINE_STROCK * 2, 10, 10);
        Font policeArrow = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(policeArrow);
        FontMetrics metricsArrow = g2d.getFontMetrics(policeArrow);

        g2d.setColor(TEXT_COLOR);
        int textBackX = backBtnX + (SIZE_BACK_BUTTON - metricsArrow.stringWidth("<")) / 2;
        int textBackY = backBtnY + ((SIZE_BACK_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString("<", textBackX, textBackY);

        String textBoosterRestant = "x " + nbBooster + " Booster(s)";
        int boosterBtnX = getWidth() - MARGIN_BACK - metricsArrow.stringWidth(textBoosterRestant);
        int boosterBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;
        int textBoosterX = boosterBtnX + (SIZE_BACK_BUTTON - metricsArrow.stringWidth(textBoosterRestant)) / 2;
        int textBoosterY = boosterBtnY + ((SIZE_BACK_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.setColor(RETURN_COLOR);
        g2d.fillRoundRect(textBoosterX - 10, boosterBtnY, metricsArrow.stringWidth(textBoosterRestant) + 20, SIZE_BACK_BUTTON, 15, 15);
        g2d.setColor(LINE_COLOR);
        g2d.drawRoundRect(textBoosterX - 10, boosterBtnY, metricsArrow.stringWidth(textBoosterRestant) + 20, SIZE_BACK_BUTTON, 15, 15);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(textBoosterRestant, textBoosterX, textBoosterY);

        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;
        int y_booster = getHeight() / 2 - HEIGHT_BOOSTER / 2;
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(x_booster, y_booster, WIDTH_BOOSTER, HEIGHT_BOOSTER, 15, 15);
        Image imgBooster = getImage("src/main/java/Interface/Page/Image/Cartes2.png");
        if (imgBooster != null) g2d.drawImage(imgBooster, x_booster, y_booster, WIDTH_BOOSTER, HEIGHT_BOOSTER, this);

        Font police = new Font("Arial", Font.PLAIN, 10);
        g2d.setFont(police);
        g2d.drawString(regions[indexRegion].getNomAffichage(), (int) (x_booster + WIDTH_BOOSTER * 0.23), (int) (y_booster + HEIGHT_BOOSTER * 0.88));

        int x_btn = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;
        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(x_btn, y_btn, WIDTH_BUTTON, HEIGHT_BUTTON, 15, 15);

        if (BUTTON_HOVER) g2d.setColor(BOOSTER_HOVER_COLOR);
        else g2d.setColor(BOOSTER_COLOR);
        g2d.fillRoundRect(x_btn + LINE_STROCK, y_btn + LINE_STROCK, WIDTH_BUTTON - LINE_STROCK * 2, HEIGHT_BUTTON - LINE_STROCK * 2, 10, 10);

        String text = "Ouvrir";
        police = new Font("Arial", Font.BOLD, SIZE_POLICE);
        g2d.setFont(police);
        FontMetrics metrics = g2d.getFontMetrics(police);
        g2d.setColor(TEXT_COLOR);
        int textX = x_btn + (WIDTH_BUTTON - metrics.stringWidth(text)) / 2;
        int textY = y_btn + ((HEIGHT_BUTTON - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);

        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(leftBtnX, leftBtnY, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON, 15, 15);
        if (LEFT_BUTTON_HOVER) g2d.setColor(INVENTORY_HOVER_COLOR); else g2d.setColor(INVENTORY_COLOR);
        g2d.fillRoundRect(leftBtnX + LINE_STROCK, leftBtnY + LINE_STROCK, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, 10, 10);
        g2d.setFont(policeArrow);
        g2d.setColor(TEXT_COLOR);
        int textLeftX = leftBtnX + (SIZE_CAROUSEL_BUTTON - metricsArrow.stringWidth("<")) / 2;
        int textLeftY = leftBtnY + ((SIZE_CAROUSEL_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString("<", textLeftX, textLeftY);

        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(rightBtnX, rightBtnY, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON, 15, 15);
        if (RIGHT_BUTTON_HOVER) g2d.setColor(INVENTORY_HOVER_COLOR); else g2d.setColor(INVENTORY_COLOR);
        g2d.fillRoundRect(rightBtnX + LINE_STROCK, rightBtnY + LINE_STROCK, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, SIZE_CAROUSEL_BUTTON - LINE_STROCK * 2, 10, 10);
        g2d.setColor(TEXT_COLOR);
        int textRightX = rightBtnX + (SIZE_CAROUSEL_BUTTON - metricsArrow.stringWidth(">")) / 2;
        int textRightY = rightBtnY + ((SIZE_CAROUSEL_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString(">", textRightX, textRightY);

        if (BUTTON_HOVER || LEFT_BUTTON_HOVER || RIGHT_BUTTON_HOVER || BACK_BUTTON_HOVER) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void drawOpenedCard(Graphics2D g2d) {
        Carte currentCard = pulledCards.get(currentCardIndex);

        int maxCardH = getHeight() - 220;
        int cardH = Math.min(600, Math.max(300, maxCardH)); // Contrainte responsive
        int cardW = (int) (cardH / 1.42); // Ratio parfait de carte
        int cx = getWidth() / 2 - cardW / 2;
        int cy = (getHeight() - cardH) / 2 - 30; // On la remonte un peu

        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(cx + 8, cy + 8, cardW, cardH, 20, 20);

        String rareteStr = currentCard.getRarete() != null ? currentCard.getRarete().toString() : "COMMUN";
        String nomFichierTheme = getThemeImageName(currentCard.getDomaineThematique(), rareteStr);
        Image fondCarte = getImage("src/main/java/Interface/Page/Image_Carte/" + nomFichierTheme);

        if (fondCarte != null) {
            g2d.drawImage(fondCarte, cx, cy, cardW, cardH, this);
        } else {
            g2d.setColor(new Color(245, 245, 245));
            g2d.fillRoundRect(cx, cy, cardW, cardH, 20, 20);
            g2d.setColor(LINE_COLOR);
            g2d.drawRoundRect(cx, cy, cardW, cardH, 20, 20);
        }

        g2d.setColor(new Color(60, 35, 10)); // Marron
        int maxTextWidth = (int) (cardW * 0.70); // La largeur du cartouche
        int fontSize = Math.max(12, (int) (cardH * 0.045));
        Font titleFont = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();

        String nomMusee = currentCard.getNomOfficiel() != null ? currentCard.getNomOfficiel() : "Inconnu";
        String[] words = nomMusee.split(" ");
        List<String> lines = new ArrayList<>();
        String currentLine = "";

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (fm.stringWidth(testLine) < maxTextWidth) {
                currentLine = testLine;
            } else {
                if (!currentLine.isEmpty()) lines.add(currentLine);
                currentLine = word;
            }
        }
        if (!currentLine.isEmpty()) lines.add(currentLine);

        int boxCenterY = cy + (int)(cardH * 0.14);
        int totalHeight = lines.size() * fm.getHeight();
        int startY = boxCenterY - (totalHeight / 2) + fm.getAscent();

        for (int i = 0; i < Math.min(lines.size(), 2); i++) { // Affichage sur 2 lignes max
            String line = lines.get(i);
            if (i == 1 && lines.size() > 2) line += "...";
            int lx = cx + (cardW - fm.stringWidth(line)) / 2;
            g2d.drawString(line, lx, startY + (i * fm.getHeight()));
        }

        String regionStr = currentCard.getRegion();
        if (regionStr == null || regionStr.isEmpty() || regionStr.equals("null")) {
            regionStr = regions[indexRegion].getNomAffichage();
        }
        Image imgRegion = getImage("src/main/java/Interface/Page/Images/" + getRegionImageName(regionStr));

        if (imgRegion != null) {
            int regW = (int) (cardW * 0.38);
            int regH = (int) (cardH * 0.12);
            int regX = cx + cardW - regW - (int) (cardW * 0.05);
            int regY = cy + cardH - regH - (int) (cardH * 0.045);
            g2d.drawImage(imgRegion, regX, regY, regW, regH, this);
        }

        int btnW = 200;
        int btnH = 50;
        int btnX = getWidth() / 2 - btnW / 2;
        int btnY = cy + cardH + 40; // On force le bouton à être 40px en-dessous de la carte

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(btnX, btnY, btnW, btnH, 15, 15);

        if (NEXT_BUTTON_HOVER) {
            g2d.setColor(BOOSTER_HOVER_COLOR);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            g2d.setColor(BOOSTER_COLOR);
            setCursor(Cursor.getDefaultCursor());
        }
        g2d.fillRoundRect(btnX + LINE_STROCK, btnY + LINE_STROCK, btnW - LINE_STROCK * 2, btnH - LINE_STROCK * 2, 10, 10);

        String textBtn = (currentCardIndex == pulledCards.size() - 1) ? "Terminer" : "Suivante >";
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metricsBtn = g2d.getFontMetrics();
        int textBtnX = btnX + (btnW - metricsBtn.stringWidth(textBtn)) / 2;
        int textBtnY = btnY + ((btnH - metricsBtn.getHeight()) / 2) + metricsBtn.getAscent();
        g2d.drawString(textBtn, textBtnX, textBtnY);

        // Le compteur se place juste au-dessus du bouton (et donc sous la carte)
        g2d.setColor(TEXT_COLOR);
        String compteur = (currentCardIndex + 1) + " / " + pulledCards.size();
        g2d.drawString(compteur, getWidth() / 2 - metricsBtn.stringWidth(compteur) / 2, btnY - 15);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();

        if (isOpening) {
            int btnW = 200, btnH = 50;
            int btnX = getWidth() / 2 - btnW / 2;

            // On calcule la même position Y pour le clic que pour l'affichage
            int maxCardH = getHeight() - 220;
            int cardH = Math.min(600, Math.max(300, maxCardH));
            int cy = (getHeight() - cardH) / 2 - 30;
            int btnY = cy + cardH + 40;

            if (isOnArea(btnX, btnY, x_mouse, y_mouse, btnW, btnH)) {
                if (currentCardIndex < pulledCards.size() - 1) {
                    currentCardIndex++;
                } else {
                    isOpening = false;
                    pulledCards.clear();
                }
                repaint();
            }
            return;
        }

        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;
        int y_booster = getHeight() / 2 - HEIGHT_BOOSTER / 2;
        int x_btn_main = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn_main = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;
        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        if (isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON)) {
            anInterface.show("HOME");
        }

        if (isOnArea(x_booster, y_booster, x_mouse, y_mouse, WIDTH_BOOSTER, HEIGHT_BOOSTER) ||
                isOnArea(x_btn_main, y_btn_main, x_mouse, y_mouse, WIDTH_BUTTON, HEIGHT_BUTTON)) {
            ouvrirBooster();
        }

        if (isOnArea(leftBtnX, leftBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON)) {
            moveCarousel(-1);
        }

        if (isOnArea(rightBtnX, rightBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON)) {
            moveCarousel(+1);
        }
    }

    private void ouvrirBooster() {
        if (nbBooster <= 0) {
            JOptionPane.showMessageDialog(this, "Vous n'avez plus de boosters !", "Stock vide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booster booster = new Booster(regions[indexRegion]);
        List<Carte> cartesTirees = booster.ouvrirBooster(anInterface.getUser(), regions[indexRegion]);

        if (cartesTirees != null && !cartesTirees.isEmpty()) {
            this.pulledCards = cartesTirees;
            this.isOpening = true;
            this.currentCardIndex = 0;
            refrechNbBooster();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Impossible d'ouvrir ce booster. (Limite atteinte ou erreur serveur).", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void moveCarousel(int n) {
        int tmpIndex = indexRegion + n;
        if (tmpIndex >= 0 && tmpIndex < regions.length) {
            indexRegion = tmpIndex;
        } else if (tmpIndex == -1) {
            indexRegion = regions.length - 1;
        } else {
            indexRegion = 0;
        }
        repaint();
    }

    private boolean isOnArea(int x_init, int y_init, int x_mouse, int y_mouse, int width, int height) {
        return (x_mouse > x_init && x_mouse < x_init + width && y_mouse > y_init && y_mouse < y_init + height);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();
        boolean repaintNeeded = false;

        if (isOpening) {
            int btnW = 200, btnH = 50;
            int btnX = getWidth() / 2 - btnW / 2;

            // Calculer la même zone de survol
            int maxCardH = getHeight() - 220;
            int cardH = Math.min(600, Math.max(300, maxCardH));
            int cy = (getHeight() - cardH) / 2 - 30;
            int btnY = cy + cardH + 40;

            boolean isNextHovered = isOnArea(btnX, btnY, x_mouse, y_mouse, btnW, btnH);
            if (NEXT_BUTTON_HOVER != isNextHovered) {
                NEXT_BUTTON_HOVER = isNextHovered;
                repaint();
            }
            return;
        }

        int x_booster = getWidth() / 2 - WIDTH_BOOSTER / 2;
        int x_btn_main = getWidth() / 2 - WIDTH_BUTTON / 2;
        int y_btn_main = getHeight() - BORDER_SIZE - HEIGHT_BUTTON / 2;
        int leftBtnX = x_booster - SIZE_CAROUSEL_BUTTON - MARGIN_CAROUSEL;
        int leftBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        int rightBtnX = x_booster + WIDTH_BOOSTER + MARGIN_CAROUSEL;
        int rightBtnY = getHeight() / 2 - SIZE_CAROUSEL_BUTTON / 2;
        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        boolean isBackHovered = isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON);
        if (BACK_BUTTON_HOVER != isBackHovered) { BACK_BUTTON_HOVER = isBackHovered; repaintNeeded = true; }

        boolean isMainHovered = isOnArea(x_btn_main, y_btn_main, x_mouse, y_mouse, WIDTH_BUTTON, HEIGHT_BUTTON);
        if (BUTTON_HOVER != isMainHovered) { BUTTON_HOVER = isMainHovered; repaintNeeded = true; }

        boolean isLeftHovered = isOnArea(leftBtnX, leftBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON);
        if (LEFT_BUTTON_HOVER != isLeftHovered) { LEFT_BUTTON_HOVER = isLeftHovered; repaintNeeded = true; }

        boolean isRightHovered = isOnArea(rightBtnX, rightBtnY, x_mouse, y_mouse, SIZE_CAROUSEL_BUTTON, SIZE_CAROUSEL_BUTTON);
        if (RIGHT_BUTTON_HOVER != isRightHovered) { RIGHT_BUTTON_HOVER = isRightHovered; repaintNeeded = true; }

        if (repaintNeeded) repaint();
    }
}