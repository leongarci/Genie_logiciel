package org.example.Interface;

import org.example.Interface.Page.Login;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

public class Interface {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    public Interface() {
        SwingUtilities.invokeLater(() -> {
            FlexibleModernWindow.WindowTheme theme = new FlexibleModernWindow.DarkTheme(); // Essaie LightTheme() pour tester la modularité !
            FlexibleModernWindow.setupMenuDesign(theme);

            cardLayout = new CardLayout();
            mainContentPanel = new JPanel(cardLayout);
            mainContentPanel.setOpaque(false);

            Login login = new Login(this, Paths.get("src/main/java/org/example/Interface/Page/login.jpg").toAbsolutePath().normalize());
            mainContentPanel.add(login, "LOGIN");


            FlexibleModernWindow mainWindow = new FlexibleModernWindow("Mon Appli", mainContentPanel, theme, 850, 600, null, false);
            cardLayout.show(mainContentPanel, "LOGIN");
            mainWindow.setVisible(true);
        });
    }
}
