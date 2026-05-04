package Interface;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Interface.Page.BoosterPage;
import Interface.Page.HomePage;
import Interface.Page.InventoryPage;
import Interface.Page.LoginPage;
import auth.User;

public class Interface {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private User user;

    public Interface() {
        SwingUtilities.invokeLater(() -> {
            FlexibleModernWindow.WindowTheme theme = new FlexibleModernWindow.DarkTheme(); // Essaie LightTheme() pour tester la modularité !
            FlexibleModernWindow.setupMenuDesign(theme);

            cardLayout = new CardLayout();
            mainContentPanel = new JPanel(cardLayout);
            mainContentPanel.setOpaque(false);

            LoginPage login = new LoginPage(this);
            mainContentPanel.add(login, "LOGIN");
            HomePage home = new HomePage(this);
            mainContentPanel.add(home, "HOME");
            InventoryPage inventoryPage = new InventoryPage(this);
            mainContentPanel.add(inventoryPage, "INVENTORY");
            BoosterPage boosterPage = new BoosterPage(this);
            mainContentPanel.add(boosterPage, "BOOSTER");

            FlexibleModernWindow mainWindow = new FlexibleModernWindow("Mon Appli", mainContentPanel, theme, 850, 600, null, false);
            if (user != null) {
                cardLayout.show(mainContentPanel, "HOME");
            } else {
                cardLayout.show(mainContentPanel, "LOGIN");
            }
            mainWindow.setVisible(true);
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void show(String page) {
        cardLayout.show(mainContentPanel, page);
    }
}
