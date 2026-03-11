package org.example.Interface;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class FlexibleModernWindow extends JFrame {

    private boolean isMaximized = false;
    private boolean isSnapped = false;
    private Rectangle normalBounds;
    private WindowTheme theme;

    private JPanel mainContainer;
    private JPanel contentWrapper;
    private ModernTitleBar titleBar;
    private SnapHandler snapHandler;

    private static final int RESIZE_MARGIN = 10;

    // ==========================================
    // 1. ARCHITECTURE : THÈMES MODULAIRES
    // ==========================================
    public static abstract class WindowTheme {
        public String fontFamily = "Segoe UI";
        public Font titleFont = new Font(fontFamily, Font.BOLD, 13);
        public Font menuFont = new Font(fontFamily, Font.PLAIN, 13);
        public Font textFont = new Font(fontFamily, Font.PLAIN, 14);

        public Color windowBackground;
        public Color contentBackground;
        public Color textAreaBackground;
        public Color titleColor;

        public int windowCornerRadius = 15;
        public int contentCornerRadius = 15;
        public int outerPadding = 8;
        public int innerPadding = 0;
        public int titleBarHeight = 40;

        public Color btnCloseNormal = new Color(255, 95, 87);
        public Color btnCloseHover = new Color(222, 82, 75);
        public Color btnMinNormal = new Color(255, 189, 46);
        public Color btnMinHover = new Color(230, 169, 41);
        public Color btnMaxNormal = new Color(40, 200, 64);
        public Color btnMaxHover = new Color(36, 178, 57);

        public Color menuForeground;
        public Color menuPillHoverBg;
        public Color popupBackground;
        public Color popupSelectionBg = new Color(70, 130, 250);
        public Color popupBorder;

        public Color snapPreviewFill = new Color(100, 150, 255, 50);
        public Color snapPreviewBorder = new Color(100, 150, 255, 150);
    }

    public static class DarkTheme extends WindowTheme {
        public DarkTheme() {
            windowBackground = new Color(18, 18, 18);
            contentBackground = new Color(35, 35, 35);
            textAreaBackground = new Color(45, 45, 45);
            titleColor = new Color(220, 220, 220);
            menuForeground = new Color(220, 220, 220);
            menuPillHoverBg = new Color(255, 255, 255, 35);
            popupBackground = new Color(45, 45, 45);
            popupBorder = new Color(80, 80, 80);
        }
    }

    public static class LightTheme extends WindowTheme {
        public LightTheme() {
            windowBackground = new Color(240, 240, 240);
            contentBackground = new Color(255, 255, 255);
            textAreaBackground = new Color(250, 250, 250);
            titleColor = new Color(30, 30, 30);
            menuForeground = new Color(30, 30, 30);
            menuPillHoverBg = new Color(0, 0, 0, 15);
            popupBackground = new Color(255, 255, 255);
            popupBorder = new Color(200, 200, 200);
        }
    }

    // ==========================================
    // INITIALISATION FENÊTRE
    // ==========================================
    public FlexibleModernWindow(String title, JPanel contentPanel, WindowTheme theme, int width, int height, Point location, boolean startMaximized) {
        this.theme = (theme != null) ? theme : new DarkTheme();
        initWindow(title, contentPanel);
        setSize(width, height);
        applyLocation(location);
        normalBounds = getBounds();
        if (startMaximized) toggleMaximize();
    }

    private void applyLocation(Point location) {
        if (location == null) setLocationRelativeTo(null);
        else setLocation(location);
    }

    private void initWindow(String title, JPanel contentPanel) {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        snapHandler = new SnapHandler(this);

        mainContainer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int radius = isSnapped ? 0 : theme.windowCornerRadius;
                g2.setColor(theme.windowBackground);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                g2.setColor(theme.popupBorder);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(0, theme.outerPadding, theme.outerPadding, theme.outerPadding));

        titleBar = new ModernTitleBar(this, title);
        mainContainer.add(titleBar, BorderLayout.NORTH);

        contentWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int radius = isSnapped ? 0 : theme.contentCornerRadius;
                g2.setColor(theme.contentBackground);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose();
            }
        };
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(theme.innerPadding, theme.innerPadding, theme.innerPadding, theme.innerPadding));

        if (contentPanel != null) {
            contentPanel.setOpaque(false);
            contentWrapper.add(contentPanel, BorderLayout.CENTER);
        }

        mainContainer.add(contentWrapper, BorderLayout.CENTER);
        setContentPane(mainContainer);

        setupAccessibilityAndShortcuts();

        ResizeListener resizeListener = new ResizeListener();
        this.addMouseListener(resizeListener);
        this.addMouseMotionListener(resizeListener);
    }

    // ==========================================
    // 4. ACCESSIBILITÉ & RACCOURCIS
    // ==========================================
    private void setupAccessibilityAndShortcuts() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        // Ctrl + W / Alt + F4 pour fermer
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "close");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK), "close");
        actionMap.put("close", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { System.exit(0); }
        });

        // Ctrl + F pour Focus la recherche
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusSearch");
        actionMap.put("focusSearch", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { titleBar.focusSearch(); }
        });

        // La navigation Tab est gérée nativement par Swing si les composants sont focusables.
        // Nous nous assurons que le JTextField de recherche le soit lors de sa création.
    }

    // ==========================================
    // MÉTHODES PUBLIQUES (API)
    // ==========================================
    public WindowTheme getTheme() { return theme; }
    public boolean isSnapped() { return isSnapped; }
    public Rectangle getNormalBounds() { return normalBounds; }
    public void setNormalBounds(Rectangle bounds) { this.normalBounds = bounds; }

    public void setTheme(WindowTheme newTheme) {
        this.theme = newTheme;
        setupMenuDesign(theme);
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    public void updateWindowState(boolean snapped, boolean maximized) {
        this.isSnapped = snapped;
        this.isMaximized = maximized;
        if (snapped) mainContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        else mainContainer.setBorder(new EmptyBorder(0, theme.outerPadding, theme.outerPadding, theme.outerPadding));
        revalidate();
        repaint();
    }

    public void toggleMaximize() {
        if (isMaximized) {
            setBounds(normalBounds);
            updateWindowState(false, false);
        } else {
            normalBounds = getBounds();
            Rectangle b = snapHandler.getPredictedSnapBounds(new Point(normalBounds.x, normalBounds.y));
            if (b != null) setBounds(b);
            else setExtendedState(JFrame.MAXIMIZED_BOTH); // Fallback
            updateWindowState(true, true);
        }
    }

    public void setAppLogo(ImageIcon icon) { titleBar.setLogo(icon); }
    public JTextField addTitleSearchBar(String placeholder) { return titleBar.addSearchBar(placeholder); }
    public void setModernMenuBar(JMenuBar menuBar) { titleBar.setMenuBar(menuBar); }

    // ==========================================
    // 1. ARCHITECTURE : BARRE DE TITRE SÉPARÉE
    // ==========================================
    private class ModernTitleBar extends JPanel {
        private final FlexibleModernWindow window;
        private JLabel iconLabel;
        private JPanel menuContainer;
        private JPanel searchContainer;
        private JTextField searchField;
        private int mouseX, mouseY;
        private boolean isResizingTop = false;

        public ModernTitleBar(FlexibleModernWindow window, String title) {
            this.window = window;
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(100, window.theme.titleBarHeight));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

            JPanel leftZone = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, (window.theme.titleBarHeight - 28) / 2));
            leftZone.setOpaque(false);

            iconLabel = new JLabel();
            iconLabel.setVisible(false);
            leftZone.add(iconLabel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            buttonPanel.setOpaque(false);
            buttonPanel.add(createControlButton(window.theme.btnCloseNormal, window.theme.btnCloseHover, () -> System.exit(0)));
            buttonPanel.add(createControlButton(window.theme.btnMinNormal, window.theme.btnMinHover, () -> window.setExtendedState(JFrame.ICONIFIED)));
            buttonPanel.add(createControlButton(window.theme.btnMaxNormal, window.theme.btnMaxHover, window::toggleMaximize));
            leftZone.add(buttonPanel);

            menuContainer = new JPanel(new BorderLayout());
            menuContainer.setOpaque(false);
            leftZone.add(menuContainer);

            add(leftZone, BorderLayout.WEST);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(window.theme.titleFont);
            titleLabel.setForeground(window.theme.titleColor);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(titleLabel, BorderLayout.CENTER);

            searchContainer = new JPanel(new GridBagLayout());
            searchContainer.setOpaque(false);
            searchContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            add(searchContainer, BorderLayout.EAST);

            setupMouseListeners();
        }

        private void setupMouseListeners() {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (!window.isSnapped() && e.getY() <= RESIZE_MARGIN) {
                        isResizingTop = true;
                        window.setNormalBounds(window.getBounds());
                    } else {
                        isResizingTop = false;
                        mouseX = e.getX(); mouseY = e.getY();
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) window.toggleMaximize();
                }
                public void mouseReleased(MouseEvent e) {
                    isResizingTop = false;
                    window.snapHandler.applySnapPreview();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor((!window.isSnapped() && e.getY() <= RESIZE_MARGIN) ? Cursor.N_RESIZE_CURSOR : Cursor.DEFAULT_CURSOR));
                }
                public void mouseDragged(MouseEvent e) {
                    if (isResizingTop) {
                        Point currentPos = e.getLocationOnScreen();
                        Rectangle normalBounds = window.getNormalBounds();
                        int dy = currentPos.y - normalBounds.y;
                        int newH = normalBounds.height - dy;
                        if (newH >= window.getMinimumSize().height) {
                            window.setBounds(normalBounds.x, normalBounds.y + dy, normalBounds.width, newH);
                        }
                        return;
                    }

                    if (window.isSnapped()) {
                        Rectangle normalBounds = window.getNormalBounds();
                        int restoreW = normalBounds.width;
                        int restoreH = normalBounds.height;

                        GraphicsConfiguration gc = window.getGraphicsConfiguration();
                        if (restoreW >= gc.getBounds().width - 20) { restoreW = 800; restoreH = 600; }

                        double widthRatio = (double) mouseX / getWidth();
                        window.updateWindowState(false, false);

                        mouseX = (int) (restoreW * widthRatio);
                        window.setBounds(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY, restoreW, restoreH);
                        window.setNormalBounds(window.getBounds());
                        return;
                    }

                    window.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                    window.snapHandler.updatePreview(e.getLocationOnScreen());
                }
            });
        }

        public void setLogo(ImageIcon icon) {
            if (icon == null) iconLabel.setVisible(false);
            else {
                int size = window.theme.titleBarHeight - 16;
                iconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
                iconLabel.setVisible(true);
                window.setIconImage(icon.getImage());
            }
        }

        public JTextField addSearchBar(String placeholderText) {
            searchField = new JTextField(15);
            Dimension size = new Dimension(180, 28);
            searchField.setPreferredSize(size);
            searchField.setMinimumSize(size);
            searchField.setMaximumSize(size);
            searchField.setFont(window.theme.textFont);
            searchField.setOpaque(false);
            searchField.setText(placeholderText);
            searchField.setForeground(Color.GRAY);
            searchField.setFocusable(true); // Requis pour la navigation Tab
            searchField.setCaretColor(Color.WHITE);
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedPopupBorder(window.theme.popupBorder, 10),
                    BorderFactory.createEmptyBorder(2, 10, 2, 10)
            ));

            searchField.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (searchField.getText().equals(placeholderText)) {
                        searchField.setText(""); searchField.setForeground(window.theme.titleColor);
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (searchField.getText().isEmpty()) {
                        searchField.setForeground(Color.GRAY); searchField.setText(placeholderText);
                    }
                }
            });

            searchContainer.removeAll();
            searchContainer.add(searchField);
            searchContainer.revalidate(); searchContainer.repaint();
            return searchField;
        }

        public void focusSearch() {
            if (searchField != null) searchField.requestFocusInWindow();
        }

        public void setMenuBar(JMenuBar menuBar) {
            menuBar.setOpaque(false); menuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            menuContainer.removeAll(); menuContainer.add(menuBar, BorderLayout.CENTER);
            menuContainer.revalidate(); menuContainer.repaint();
        }

        private JButton createControlButton(Color normal, Color hover, Runnable action) {
            JButton btn = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? hover : normal);
                    g2.fill(new Ellipse2D.Double(0, 0, 12, 12));
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(13, 13)); btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> action.run());
            return btn;
        }
    }

    // ==========================================
    // 1 & 2. GESTIONNAIRE D'ANCRAGE & MULTI-ÉCRANS
    // ==========================================
    private class SnapHandler {
        private final FlexibleModernWindow window;
        private final JWindow snapPreview;

        public SnapHandler(FlexibleModernWindow window) {
            this.window = window;
            snapPreview = new JWindow();
            snapPreview.setAlwaysOnTop(true);
            snapPreview.setBackground(new Color(0, 0, 0, 0));

            JPanel previewPanel = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(window.theme.snapPreviewFill);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), window.theme.windowCornerRadius, window.theme.windowCornerRadius);
                    g2.setColor(window.theme.snapPreviewBorder);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, window.theme.windowCornerRadius, window.theme.windowCornerRadius);
                    g2.dispose();
                }
            };
            previewPanel.setOpaque(false);
            snapPreview.setContentPane(previewPanel);
        }

        public void updatePreview(Point screenLocation) {
            Rectangle snapBounds = getPredictedSnapBounds(screenLocation);
            if (snapBounds != null) {
                snapPreview.setBounds(snapBounds);
                if (!snapPreview.isVisible()) snapPreview.setVisible(true);
            } else {
                if (snapPreview.isVisible()) snapPreview.setVisible(false);
            }
        }

        public void applySnapPreview() {
            if (snapPreview != null && snapPreview.isVisible()) {
                Rectangle boundsToApply = snapPreview.getBounds();
                snapPreview.setVisible(false);

                // Multi-écran : Trouver le bon écran pour savoir si c'est "maximized"
                GraphicsConfiguration gc = getGraphicsConfigurationForPoint(boundsToApply.getLocation());
                Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                int screenWidth = gc.getBounds().width - insets.left - insets.right;

                if (boundsToApply.width >= screenWidth - 10) {
                    window.setNormalBounds(window.getBounds());
                    window.setBounds(boundsToApply);
                    window.updateWindowState(true, true);
                } else {
                    window.setBounds(boundsToApply);
                    window.setNormalBounds(window.getBounds());
                    window.updateWindowState(true, false);
                }
            }
        }

        public Rectangle getPredictedSnapBounds(Point screenLocation) {
            // MULTI-ÉCRANS : On cherche l'écran sur lequel se trouve la souris
            GraphicsConfiguration gc = getGraphicsConfigurationForPoint(screenLocation);
            Rectangle bounds = gc.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

            int x = bounds.x + insets.left; int y = bounds.y + insets.top;
            int w = bounds.width - insets.left - insets.right; int h = bounds.height - insets.top - insets.bottom;
            int snapTolerance = 15;

            boolean top = screenLocation.y <= y + snapTolerance;
            boolean bottom = screenLocation.y >= y + h - snapTolerance;
            boolean left = screenLocation.x <= x + snapTolerance;
            boolean right = screenLocation.x >= x + w - snapTolerance;

            if (top && left) return new Rectangle(x, y, w / 2, h / 2);
            else if (top && right) return new Rectangle(x + w / 2, y, w / 2, h / 2);
            else if (bottom && left) return new Rectangle(x, y + h / 2, w / 2, h / 2);
            else if (bottom && right) return new Rectangle(x + w / 2, y + h / 2, w / 2, h / 2);
            else if (top) return new Rectangle(x, y, w, h);
            else if (left) return new Rectangle(x, y, w / 2, h);
            else if (right) return new Rectangle(x + w / 2, y, w / 2, h);

            return null;
        }

        private GraphicsConfiguration getGraphicsConfigurationForPoint(Point p) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (GraphicsDevice gd : ge.getScreenDevices()) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                if (gc.getBounds().contains(p)) return gc;
            }
            return window.getGraphicsConfiguration(); // Fallback
        }
    }

    // ==========================================
    // 3. PERFORMANCE DU REDIMENSIONNEMENT
    // ==========================================
    private class ResizeListener extends MouseAdapter {
        private int cursorType = Cursor.DEFAULT_CURSOR;
        private Point startPos = null;
        private Rectangle startBounds = null;
        private long lastUpdate = 0;
        private final int THROTTLE_MS = 15; // Évite l'engorgement du thread UI (~60 FPS)

        @Override public void mouseMoved(MouseEvent e) {
            if (isSnapped) { setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); return; }
            Point p = e.getPoint();
            int w = getWidth(); int h = getHeight();

            boolean left = p.x <= RESIZE_MARGIN; boolean right = p.x >= w - RESIZE_MARGIN;
            boolean top = p.y <= RESIZE_MARGIN; boolean bottom = p.y >= h - RESIZE_MARGIN;

            if (top && left) cursorType = Cursor.NW_RESIZE_CURSOR;
            else if (top && right) cursorType = Cursor.NE_RESIZE_CURSOR;
            else if (bottom && left) cursorType = Cursor.SW_RESIZE_CURSOR;
            else if (bottom && right) cursorType = Cursor.SE_RESIZE_CURSOR;
            else if (bottom) cursorType = Cursor.S_RESIZE_CURSOR;
            else if (left) cursorType = Cursor.W_RESIZE_CURSOR;
            else if (right) cursorType = Cursor.E_RESIZE_CURSOR;
            else cursorType = Cursor.DEFAULT_CURSOR;

            setCursor(Cursor.getPredefinedCursor(cursorType));
        }

        @Override public void mousePressed(MouseEvent e) {
            if (cursorType != Cursor.DEFAULT_CURSOR) {
                startPos = e.getLocationOnScreen(); startBounds = getBounds();
            }
        }

        @Override public void mouseDragged(MouseEvent e) {
            if (startPos == null || startBounds == null || cursorType == Cursor.DEFAULT_CURSOR || isSnapped) return;

            // THROTTLE : On ne redessine pas à chaque milliseconde pour éviter les lags avec des JTextArea
            long now = System.currentTimeMillis();
            if (now - lastUpdate < THROTTLE_MS) return;
            lastUpdate = now;

            Point currentPos = e.getLocationOnScreen();
            int dx = currentPos.x - startPos.x; int dy = currentPos.y - startPos.y;
            int x = startBounds.x; int y = startBounds.y; int w = startBounds.width; int h = startBounds.height;
            Dimension minSize = getMinimumSize();

            if (cursorType == Cursor.E_RESIZE_CURSOR || cursorType == Cursor.NE_RESIZE_CURSOR || cursorType == Cursor.SE_RESIZE_CURSOR) w += dx;
            if (cursorType == Cursor.S_RESIZE_CURSOR || cursorType == Cursor.SW_RESIZE_CURSOR || cursorType == Cursor.SE_RESIZE_CURSOR) h += dy;
            if (cursorType == Cursor.W_RESIZE_CURSOR || cursorType == Cursor.NW_RESIZE_CURSOR || cursorType == Cursor.SW_RESIZE_CURSOR) { w -= dx; x += dx; }
            if (cursorType == Cursor.NW_RESIZE_CURSOR || cursorType == Cursor.NE_RESIZE_CURSOR) { h -= dy; y += dy; }

            if (w >= minSize.width && h >= minSize.height) {
                setBounds(x, y, w, h);
                normalBounds = getBounds();
                validate(); // Assure une mise à jour propre du layout interne sans repaints excessifs
            }
        }
        @Override public void mouseReleased(MouseEvent e) { startPos = null; startBounds = null; }
    }

    // ==========================================
    // MÉTHODES DE DESIGN STATIQUES CONSERVÉES
    // ==========================================
    public static void setupMenuDesign(WindowTheme theme) {
        UIManager.put("MenuBar.background", new Color(0, 0, 0, 0));
        UIManager.put("MenuBar.border", BorderFactory.createEmptyBorder());
        UIManager.put("MenuBar.opaque", false);
        UIManager.put("Menu.selectionBackground", new Color(0, 0, 0, 0));
        UIManager.put("Menu.selectionForeground", theme.titleColor);
        UIManager.put("Menu.opaque", false);
        UIManager.put("Menu.font", theme.menuFont);
        UIManager.put("MenuItem.font", theme.menuFont);
        UIManager.put("PopupMenu.background", theme.popupBackground);
        UIManager.put("PopupMenu.opaque", true);
        UIManager.put("PopupMenu.border", new RoundedPopupBorder(theme.popupBorder, 8));
        UIManager.put("MenuItem.foreground", theme.menuForeground);
        UIManager.put("MenuItem.background", theme.popupBackground);
        UIManager.put("MenuItem.selectionBackground", theme.popupSelectionBg);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.borderPainted", false);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("MenuItem.border", BorderFactory.createEmptyBorder(6, 20, 6, 20));
    }

    public static class RoundedMenu extends JMenu {
        private final WindowTheme theme;
        public RoundedMenu(String title, WindowTheme theme) {
            super(title);
            this.theme = theme;
            setOpaque(false); setForeground(theme.menuForeground); setFont(theme.menuFont);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }
        @Override protected void paintComponent(Graphics g) {
            if (getModel().isRollover() || getModel().isSelected()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(theme.menuPillHoverBg);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    public static class RoundedPopupBorder implements Border {
        private final Color color; private final int radius;
        public RoundedPopupBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
        @Override public boolean isBorderOpaque() { return false; }
    }

}