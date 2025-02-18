package org.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class QRCodeGeneratorGUI {

    private JFrame frame;
    private JTextField textField;
    private JTextField descriptionField;
    private JLabel qrLabel;
    private File lastSaveDirectory;

    public QRCodeGeneratorGUI() {
        frame = new JFrame("QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 650);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        textField = new JTextField(20);
        descriptionField = new JTextField(20);
        JButton generateButton = new JButton("Generate QR Code");
        JButton exportButton = new JButton("Export to PNG");
        JButton setSaveLocationButton = new JButton("Set Save Location");

        styleButton(generateButton);
        styleButton(exportButton);
        styleButton(setSaveLocationButton);

        inputPanel.add(new JLabel("Text/URL:"));
        inputPanel.add(textField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(generateButton);
        inputPanel.add(setSaveLocationButton);
        inputPanel.add(exportButton);
        inputPanel.add(new JLabel()); // Empty label for spacing

        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(JLabel.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(qrLabel, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateQRCode());
        exportButton.addActionListener(e -> exportQRCodeAsPNG());
        setSaveLocationButton.addActionListener(e -> setSaveLocation());

        frame.setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void generateQRCode() {
        String text = textField.getText();
        String description = descriptionField.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a URL or text.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int size = 300;
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

            int labelWidth = qrImage.getWidth();
            int labelHeight = 30;
            BufferedImage descriptionImage = new BufferedImage(labelWidth, labelHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D gDescription = descriptionImage.createGraphics();
            gDescription.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gDescription.setFont(new Font("Arial", Font.BOLD, 20));
            gDescription.setColor(Color.WHITE);
            gDescription.fillRect(0, 0, labelWidth, labelHeight);
            gDescription.setColor(Color.BLACK);
            FontMetrics metrics = gDescription.getFontMetrics();
            int x = (labelWidth - metrics.stringWidth(description)) / 2;
            int y = ((labelHeight - metrics.getHeight()) / 2) + metrics.getAscent();
            gDescription.drawString(description, x, y);
            gDescription.dispose();

            int totalHeight = qrImage.getHeight() + descriptionImage.getHeight();
            BufferedImage combinedImage = new BufferedImage(labelWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gCombined = combinedImage.createGraphics();
            gCombined.drawImage(qrImage, 0, 0, null);
            gCombined.drawImage(descriptionImage, 0, qrImage.getHeight(), null);
            gCombined.dispose();

            qrLabel.setIcon(new ImageIcon(combinedImage));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating QR Code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void exportQRCodeAsPNG() {
        if (lastSaveDirectory == null) {
            JOptionPane.showMessageDialog(frame, "Please set a save location first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String text = textField.getText();
        String description = descriptionField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a URL or text.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (description.isEmpty()) {
            description = "QRCode";
        }

        File fileToSave = new File(lastSaveDirectory, description + ".png");
        try {
            int size = 300;
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
            ImageIO.write(qrImage, "png", fileToSave);
            JOptionPane.showMessageDialog(frame, "QR Code exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear the text field after successful export
            textField.setText("");
            descriptionField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error exporting QR Code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setSaveLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Save Location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Set the last selected directory if available
        if (lastSaveDirectory != null) {
            fileChooser.setCurrentDirectory(lastSaveDirectory);
        }

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            lastSaveDirectory = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(frame, "Save location set successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRCodeGeneratorGUI::new);
    }
}
