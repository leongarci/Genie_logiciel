package Interface.Page;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import Interface.Interface;

public class InventoryPage extends JPanel implements MouseListener, MouseMotionListener {

    private Interface anInterface;

    private final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private final Color BACKGROUND_SECONDARY_COLOR = new Color(0, 52, 21);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color LINE_COLOR = new Color(86, 86, 86);

    private final Color RETURN_COLOR = new Color(32, 32, 57);
    private final Color RETURN_HOVER_COLOR = new Color(1, 1, 35);


    // Constantes pour le bouton de retour
    private final int SIZE_BACK_BUTTON = 50;
    private final int MARGIN_BACK = 15;

    private final int LINE_STROCK = 3;
    private final int BORDER_SIZE = 75;


    private boolean BACK_BUTTON_HOVER = false;

    public InventoryPage(Interface anInterface) {
        super(null);
        this.anInterface = anInterface;
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond et lignes
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.setColor(BACKGROUND_SECONDARY_COLOR);
        g2d.fillRect(0, BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE * 2);

        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(0, BORDER_SIZE, getWidth(), BORDER_SIZE);
        g2d.drawLine(0, getHeight() - BORDER_SIZE, getWidth(), getHeight() - BORDER_SIZE);


        // Bouton RETOUR
        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        g2d.setColor(LINE_COLOR);
        g2d.fillRoundRect(backBtnX, backBtnY, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON, 15, 15);
        if (BACK_BUTTON_HOVER) {
            g2d.setColor(RETURN_HOVER_COLOR);
        } else {
            g2d.setColor(RETURN_COLOR);
        }
        g2d.fillRoundRect(backBtnX + LINE_STROCK, backBtnY + LINE_STROCK, SIZE_BACK_BUTTON - LINE_STROCK * 2, SIZE_BACK_BUTTON - LINE_STROCK * 2, 10, 10);

        Font policeArrow = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(policeArrow);
        FontMetrics metricsArrow = g2d.getFontMetrics(policeArrow);

        String textBack = "<";
        g2d.setColor(TEXT_COLOR);
        int textBackX = backBtnX + (SIZE_BACK_BUTTON - metricsArrow.stringWidth(textBack)) / 2;
        int textBackY = backBtnY + ((SIZE_BACK_BUTTON - metricsArrow.getHeight()) / 2) + metricsArrow.getAscent();
        g2d.drawString(textBack, textBackX, textBackY);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();

        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        // Clic sur Bouton Retour
        if (isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON)) {
            System.out.println("Bouton Retour <");
            anInterface.show("HOME");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x_mouse = e.getX();
        int y_mouse = e.getY();

        boolean repaintNeeded = false;

        int backBtnX = MARGIN_BACK;
        int backBtnY = (BORDER_SIZE - SIZE_BACK_BUTTON) / 2;

        // Vérification survol Bouton Retour
        boolean isBackHovered = isOnArea(backBtnX, backBtnY, x_mouse, y_mouse, SIZE_BACK_BUTTON, SIZE_BACK_BUTTON);
        if (BACK_BUTTON_HOVER != isBackHovered) {
            BACK_BUTTON_HOVER = isBackHovered;
            repaintNeeded = true;
        }
        if (repaintNeeded) {
            repaint();
        }
    }
    private boolean isOnArea(int x_init, int y_init, int x_mouse, int y_mouse, int width, int height) {
        return (x_mouse > x_init && x_mouse < x_init + width && y_mouse > y_init && y_mouse < y_init + height);
    }
}
