package org.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class QRCodeGeneratorGUI {

    private JFrame frame;
    private JTextField textField;
    private JTextField descriptionField;
    private JLabel qrLabel;

    public QRCodeGeneratorGUI() {
        frame = new JFrame("QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        textField = new JTextField(20);
        descriptionField = new JTextField(20);
        JButton generateButton = new JButton("Generate QR Code");
        JButton exportButton = new JButton("Export as PNG");

        inputPanel.add(new JLabel("Text/URL:"));
        inputPanel.add(textField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(generateButton);
        inputPanel.add(exportButton);

        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(JLabel.CENTER);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(qrLabel, BorderLayout.CENTER);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateQRCode();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportQRCodeAsPNG();
            }
        });

        frame.setVisible(true);
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
            int labelHeight = 30; // Reduced height for the description
            BufferedImage descriptionImage = new BufferedImage(labelWidth, labelHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D gDescription = descriptionImage.createGraphics();
            gDescription.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gDescription.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gDescription.setFont(new Font("Bahnschrift", Font.BOLD, 20));
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
            int labelHeight = 30; // Reduced height for the description
            BufferedImage descriptionImage = new BufferedImage(labelWidth, labelHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D gDescription = descriptionImage.createGraphics();
            gDescription.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gDescription.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gDescription.setFont(new Font("Serif", Font.BOLD, 20));
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

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save QR Code as PNG");
            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".png")) {
                    fileToSave = new File(filePath + ".png");
                }
                ImageIO.write(combinedImage, "png", fileToSave);
                JOptionPane.showMessageDialog(frame, "QR Code exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error exporting QR Code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
