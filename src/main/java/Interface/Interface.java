package Interface;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Interface.Page.BoosterPage;
import Interface.Page.HomePage;
import Interface.Page.InventoryPage;
import Interface.Page.LoginPage;
import Interface.Page.MapPage; // N'oublie pas l'import de MapPage
import Interface.Page.StatsPage;
import auth.User;

public class Interface {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private User user;

    private InventoryPage inventoryPage;

    public Interface() {
        SwingUtilities.invokeLater(() -> {
            FlexibleModernWindow.WindowTheme theme = new FlexibleModernWindow.DarkTheme();
            FlexibleModernWindow.setupMenuDesign(theme);

            cardLayout = new CardLayout();
            mainContentPanel = new JPanel(cardLayout);
            mainContentPanel.setOpaque(false);

            LoginPage login = new LoginPage(this);
            mainContentPanel.add(login, "LOGIN");

            HomePage home = new HomePage(this);
            mainContentPanel.add(home, "HOME");

            MapPage mapPage = new MapPage(this);
            mainContentPanel.add(mapPage, "MAP");

            // 2. On instancie la variable (sans remettre le type devant)
            inventoryPage = new InventoryPage(this);
            mainContentPanel.add(inventoryPage, "INVENTORY");

            BoosterPage boosterPage = new BoosterPage(this);
            mainContentPanel.add(boosterPage, "BOOSTER");

            StatsPage statsPage = new StatsPage(this);
            mainContentPanel.add(statsPage, "STATS");

            FlexibleModernWindow mainWindow = new FlexibleModernWindow("Kulturo", mainContentPanel, theme, 850, 600, null, false);
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

    public void showInventoryForRegion(String region) {
        if (inventoryPage != null) {
            inventoryPage.loadRegion(region);
            show("INVENTORY");
        }
    }
}