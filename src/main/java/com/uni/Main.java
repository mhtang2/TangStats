package com.uni;

import com.uni.datamanager.CompileStats;
import com.uni.marker.BuzzData;
import com.uni.question.Category;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Main {

    public static Window window;
    public static Image launcherIcon;

    public static void main(String[] args) throws IOException {
        launcherIcon = ImageIO.read(Main.class.getResourceAsStream("/fifteen.png"));
        Category.loadCategories("/categories");
        BuzzData.readConfig("./config.ini");
        Team.resetTeams();
        Main.window = new Window(1000, "i read qb questions");
        //test();

    }

    public static void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private static void test() throws IOException {
        new CompileStats().compile(new File[]{new File("./round2.xlsx"), new File("./round3.xlsx"), new File("./round22.xlsx")});
        PacketProcess.processFile(new File("./dogs.pdf"));
        PacketProcess.processFile(new File("./packet1.pdf"));
        for (int i = 0; i < 5; i++) {
            Team.teams[0].nameField.setText("T1 " + i);
            Team.teams[0].addPlayer();
            Team.teams[1].nameField.setText("T2 " + i);
            Team.teams[1].addPlayer();
        }
        window.setTossup(0);
    }

}

