package Interface.Page;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Interface.Interface;
import auth.AuthService;
import auth.User;

public class LoginPage extends JPanel {

    private Interface anInterface;
    private BufferedImage back;
    private JLabel err = null;
    private boolean login = true;

    // Composants
    private JTextField userField;
    private JTextField passField;
    private JButton mainButton;
    private JButton switchButton;

    // CONSTANTES DE STYLE
    private final Color BACKGROUND_COLOR = new Color(0, 52, 21);
    private final Color BACKGROUND_MODAL_COLOR = new Color(86, 86, 86);
    private final Color MODAL_COLOR = new Color(0, 0, 0);
    private final Color TEXT_MODAL_COLOR = new Color(255, 255, 255);

    private final int WIDTH_MODAL = 400;
    private final int HEIGHT_MODAL = 400; // Augmenté pour laisser de la place
    private final int PADDING_MODAL = 3;

    public LoginPage(Interface anInterface) {
        super(null); // Layout null pour positionnement manuel dynamique
        this.anInterface = anInterface;
        setOpaque(false);
        // Initialisation des composants
        userField = textBox("username");
        passField = textBox("password");
        mainButton = button("Se connecter");
        switchButton = button("S’inscrire");

        // Action du bouton principal (Login/Register)
        mainButton.addActionListener(e -> {
            AuthService authService = new AuthService();
            User us = login ? authService.login(userField.getText(), passField.getText())
                    : authService.inscrireUtilisateur(userField.getText(), passField.getText());

            if (us != null) {
                anInterface.setUser(us);
                anInterface.show("HOME");
            } else {
                err = texte("Identifiants incorrects", err);
                repositionnerComposants(); // Replacer l'erreur au bon endroit
            }
        });

        // Action pour switcher entre Connexion et Inscription
        switchButton.addActionListener(e -> {
            login = !login;
            if (!login) {
                switchButton.setText("Se connecter");
                mainButton.setText("Créer un compte");
            } else {
                switchButton.setText("S’inscrire");
                mainButton.setText("Se connecter");
            }
            repaint(); // Pour mettre à jour le titre "Connexion" dessiné
        });

        // Rendre l'interface responsive
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionnerComposants();
            }
        });
    }

    /**
     * Calcule la position de chaque élément en fonction de la taille actuelle
     * de la fenêtre
     */
    private void repositionnerComposants() {
        int centerX = getWidth() / 2;
        int modalY = (getHeight() - HEIGHT_MODAL) / 2;

        int fieldWidth = 250;
        int fieldHeight = 35;
        int startY = modalY + 110; // Commence après le titre "Connexion"

        userField.setBounds(centerX - (fieldWidth / 2), startY, fieldWidth, fieldHeight);
        passField.setBounds(centerX - (fieldWidth / 2), startY + 50, fieldWidth, fieldHeight);
        mainButton.setBounds(centerX - (fieldWidth / 2), startY + 110, fieldWidth, fieldHeight);
        switchButton.setBounds(centerX - (fieldWidth / 2), startY + 160, fieldWidth, fieldHeight);

        if (err != null) {
            err.setBounds(centerX - (fieldWidth / 2), startY + 210, fieldWidth, fieldHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // Dessin de la modal
        int x = (getWidth() - WIDTH_MODAL) / 2;
        int y = (getHeight() - HEIGHT_MODAL) / 2;

        g2d.setColor(BACKGROUND_MODAL_COLOR);
        g2d.fillRoundRect(x, y, WIDTH_MODAL, HEIGHT_MODAL, 30, 30);

        g2d.setColor(MODAL_COLOR);
        g2d.fillRoundRect(x + PADDING_MODAL, y + PADDING_MODAL,
                WIDTH_MODAL - PADDING_MODAL * 2, HEIGHT_MODAL - PADDING_MODAL * 2, 25, 25);

        // Titre dynamique
        g2d.setColor(TEXT_MODAL_COLOR);
        Font police = new Font("Arial", Font.BOLD, 26);
        g2d.setFont(police);
        String titre = login ? "Connexion" : "Inscription";
        FontMetrics metrics = g2d.getFontMetrics(police);
        g2d.drawString(titre, x + ((WIDTH_MODAL - metrics.stringWidth(titre)) / 2), y + 50);

        // Lignes de décoration
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(new Color(255, 255, 255, 50)); // Blanc semi-transparent
        g2d.drawLine(x + 50, y + 70, x + WIDTH_MODAL - 50, y + 70);
    }

    // --- Méthodes utilitaires pour créer les composants ---
    public JTextField textBox(String placeHolder) {
        JTextField textField = new JTextField(placeHolder);
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        textField.setForeground(Color.GRAY);
        textField.setCaretColor(Color.WHITE);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeHolder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeHolder);
                }
            }
        });
        this.add(textField);
        return textField;
    }

    public JButton button(String text) {
        JButton button = new JButton(text);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBackground(new Color(40, 40, 40));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        this.add(button);
        return button;
    }

    public JLabel texte(String content, JLabel labelInstance) {
        if (labelInstance == null) {
            labelInstance = new JLabel(content, SwingConstants.CENTER);
            labelInstance.setBackground(new Color(150, 0, 0, 200));
            labelInstance.setOpaque(true);
            labelInstance.setForeground(Color.WHITE);
            this.add(labelInstance);
        }
        labelInstance.setText(content);
        return labelInstance;
    }
}
