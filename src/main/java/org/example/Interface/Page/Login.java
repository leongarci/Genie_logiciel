package org.example.Interface.Page;

import org.example.AuthService;
import org.example.Interface.FlexibleModernWindow;
import org.example.Interface.Interface;
import org.example.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class Login extends JPanel {
    private Interface anInterface;
    private BufferedImage back;
    private JLabel err = null;
    private boolean login = true;
    public Login(Interface anInterface, Path pathBackground) {
        super(null);
        this.anInterface = anInterface;

        if (pathBackground != null) {
            try {
                this.back = ImageIO.read(pathBackground.toFile());
            } catch (IOException e) {
                System.err.println("Error loading background: " + e.getMessage());
            }
        }

        JTextField user = textBox(50, 50, 200, 30, "username");
        JTextField pass = textBox(50, 100, 200, 30, "password");
        JButton button = button(50, 150, 200, 30, "Se connecter");
        JButton sw = button (50, 200, 200, 30, "S’inscrire");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthService authService = new AuthService();
                int res = -1;
                if (login) {
                    res = authService.login(user.getText(), pass.getText());
                }else {
                    res = authService.register(user.getText(), pass.getText());
                }
                if (res != -1) {
                    anInterface.setUser(new User(res, user.getText()));
                    anInterface.show("HOME");
                }else {
                    err = texte(50, 250, 220, 30, "Username ou password incorrect", err);
                    revalidate();
                    repaint();
                }
            }
        });

        sw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (login) {
                    sw.setText("Se connecter");
                    button.setText("Créé un compte");
                    login = !login;
                }else {
                    sw.setText("S’inscrire");
                    button.setText("Se connecter");
                    login = !login;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (back != null) {
            g.drawImage(back, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public JTextField textBox(int x, int y, int w, int h, String placeHolder) {
        JTextField textField;
        textField = new JTextField(placeHolder);
        textField.setBounds(x, y, w, h);
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new FlexibleModernWindow.RoundedPopupBorder(Color.BLACK, 10),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        textField.setForeground(Color.DARK_GRAY);
        textField.setFocusable(true);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeHolder)){
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.DARK_GRAY); textField.setText(placeHolder);
                }
            }
        });
        this.add(textField);
        return textField;
    }

    public JButton button(int x, int y, int w, int h, String placeHolder) {
        JButton button;
        button = new JButton(placeHolder);
        button.setBounds(x, y, w, h);
        button.setOpaque(false);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                new FlexibleModernWindow.RoundedPopupBorder(Color.BLACK, 10),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        button.setForeground(Color.BLACK);
        button.setFocusable(true);
        this.add(button);
        return button;
    }

    public JLabel texte(int x, int y, int w, int h, String placeHolder, JLabel texte) {
        JLabel newtext;
        if (texte != null) {
            newtext = texte;
        }else {
            newtext = new JLabel(placeHolder);
            newtext.setBackground(new Color(.8f, 0, 0, .5f));
            newtext.setOpaque(true);
            newtext.setBorder(BorderFactory.createCompoundBorder(
                    new FlexibleModernWindow.RoundedPopupBorder(Color.RED, 10),
                    BorderFactory.createEmptyBorder(2, 10, 2, 10)
            ));
            newtext.setForeground(Color.WHITE);
            this.add(newtext);
        }
        newtext.setText(placeHolder);
        newtext.setBounds(x, y, w, h);
        repaint();
        return newtext;
    }
}