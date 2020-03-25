package com.uni;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlayerManager extends JDialog {

    static ImageIcon xIcon;

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
        setLayout(new GridLayout(0, 2));
        for (Team team : Team.teams) {
            add(team.canvas);
            team.nameField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        setVisible(false);
                    }
                }
            });
        }
        pack();
        setLocationRelativeTo(null);

    }

    public void updateGraphics() {
        repaint();
        pack();
        Main.window.updateScoreboard();
    }

    public void reconstructCanvas() {
        Team.teams[0].reconstructCanvas();
        Team.teams[1].reconstructCanvas();
        pack();
    }
}
