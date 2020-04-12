package com.uni.datamanager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CompileWindow extends JDialog {
    JFileChooser jfc = new JFileChooser();
    JTextArea textArea = new JTextArea();
    ArrayList<File> files = new ArrayList<>();

    public CompileWindow() {
        JPanel canvas = new JPanel();
        JButton openButton = new JButton("Select Round Files");
        JButton saveButton = new JButton("Generate Total Summary");
        openButton.addActionListener(e -> selectFiles());
        saveButton.addActionListener(e -> saveFile());
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        canvas.add(openButton);
        canvas.add(saveButton);
        canvas.add(textArea);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setModal(true);
        add(canvas);

        setVisible(true);
    }

    private void saveFile() {
        if (files == null || files.size() < 1) {
            JOptionPane.showMessageDialog(null, "Pick some round files first!", "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new CompileStats().compile(files.toArray(new File[0]));
    }

    private void selectFiles() {
        jfc.setFileFilter(new FileNameExtensionFilter(".xlsx", "xlsx"));
        jfc.setCurrentDirectory(new File("./"));
        jfc.setMultiSelectionEnabled(true);
        int r = jfc.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            files.addAll(Arrays.asList(jfc.getSelectedFiles()));
            StringBuilder sb = new StringBuilder();
            for (File f : jfc.getSelectedFiles()) {
                sb.append(f.getName());
                sb.append("\n");
            }
            textArea.setText(textArea.getText() + sb.toString());
        }
    }
}
