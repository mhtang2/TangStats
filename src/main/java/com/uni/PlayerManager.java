package com.uni;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager extends JDialog {

    public static ArrayList<String> playerList = new ArrayList<>();
    public static Map<String, int[]> playerData = new HashMap<>();
    JPanel canvas = new JPanel();
    JTextField nameField = new JTextField(20);
    ImageIcon xIcon;

    public PlayerManager() {
        //Set icon image for X
        try {
            BufferedImage inIm = ImageIO.read(getClass().getResourceAsStream("/x.png"));
            xIcon = new ImageIcon(inIm.getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIconImage(Main.launcherIcon);
        setTitle("Manage Players");
        setSize(300, 300);
        setModal(true);
        canvas.setLayout(new GridLayout(0, 1));
        JPanel inputContainer = new JPanel();
        inputContainer.add(new JLabel("Add Player: "));
        inputContainer.add(nameField);

        nameField.addActionListener(actionEvent -> addPlayer());

        canvas.add(inputContainer);
        add(canvas);
        pack();
        //add escape to close
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    setVisible(false);
            }
        });
    }

    private void addPlayer() {
        String name = nameField.getText();
        nameField.setText("");
        addPlayer(name);
    }

    public void addPlayer(String name) {
        playerList.add(name);
        playerData.put(name, new int[]{0, 0, 0, 0});

        JPanel container = new JPanel();
        JLabel removeButton = new JLabel();
        removeButton.setIcon(xIcon);

        container.add(new JLabel(name));
        container.add(removeButton);
        canvas.add(container);
        repaint();
        pack();
        Main.window.updateScoreboard();
        //Remove self from canvas and list of players
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                playerList.remove(name);
                playerData.remove(name);
                canvas.remove(container);
                repaint();
                pack();
                Main.window.updateScoreboard();
            }
        });
    }
}
