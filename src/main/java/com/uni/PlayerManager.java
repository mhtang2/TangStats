package com.uni;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
        Team.teams[0] = new Team("team 1");
        Team.teams[1] = new Team("team 2");

        setIconImage(Main.launcherIcon);
        setTitle("Manage Players");
        setSize(300, 300);
        setModal(true);
        setLayout(new GridLayout(0, 2));
        add(Team.teams[0].canvas);
        add(Team.teams[1].canvas);
        pack();

    }

    public void updateGraphics() {
        repaint();
        pack();
        Main.window.updateScoreboard();
    }

}
