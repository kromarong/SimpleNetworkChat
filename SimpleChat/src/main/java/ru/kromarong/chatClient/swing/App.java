package ru.kromarong.chatClient.swing;

import javax.swing.*;

public class App {

    private static MainWindow mainWindow;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> mainWindow = new MainWindow());
    }
}
