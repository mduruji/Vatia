package vatia;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import vatia.WebCrawler;

public class GUI {

    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField urlField;
    private WebCrawler webCrawler;
    private String seedUrl;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Web Crawler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createPageA(), "PageA");
        cardPanel.add(createPageB(), "PageB");

        cardLayout.show(cardPanel, "PageA");

        frame.add(cardPanel);
        frame.setVisible(true);
    }

    private JPanel createPageA() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        urlField = new JTextField(20);
        inputPanel.add(new JLabel("Enter URL:"));
        inputPanel.add(urlField);
        panel.add(inputPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            seedUrl = urlField.getText();
            if (!seedUrl.isEmpty()) {
                webCrawler = new WebCrawler(10);
                cardLayout.show(cardPanel, "PageB");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a valid URL.");
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(submitButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPageB() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        startButton.addActionListener(e -> {
            if (webCrawler != null) {
                webCrawler.crawl(seedUrl);
            }
        });

        pauseButton.addActionListener(e -> {
            if (webCrawler != null) {
                webCrawler.pause();
            }
        });

        resumeButton.addActionListener(e -> {
            if (webCrawler != null) {
                webCrawler.resume();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

}
