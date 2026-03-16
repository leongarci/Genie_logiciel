package org.example.Interface;

import org.example.Interface.Page.Home;
import org.example.Interface.Page.Login;
import org.example.User;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.HashMap;

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

            Login login = new Login(this, Paths.get("src/main/java/org/example/Interface/Page/Background/login.png").toAbsolutePath().normalize());
            mainContentPanel.add(login, "LOGIN");
            Home home = new Home(this, Paths.get("src/main/java/org/example/Interface/Page/Background/home.png").toAbsolutePath().normalize());
            mainContentPanel.add(home, "HOME");

            FlexibleModernWindow mainWindow = new FlexibleModernWindow("Mon Appli", mainContentPanel, theme, 850, 600, null, false);
            if (user != null){
                cardLayout.show(mainContentPanel, "HOME");
            }else {
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
