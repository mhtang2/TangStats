package com.uni;

import com.uni.datamanager.CompileStats;
import com.uni.question.Category;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class SetFormatException extends Exception {
    public SetFormatException(String error) {
        super(error);
    }
}

public class Main {

    public static Window window;
    public static Image launcherIcon;

    public static void main(String[] args) throws IOException, SetFormatException {
        launcherIcon = ImageIO.read(Main.class.getResourceAsStream("/fifteen.png"));
        Category.loadCategories("/categories");
        Team.resetTeams();
        new CompileStats().compile(new File[]{new File("./round2.xlsx"),new File("./round3.xlsx"),new File("./round22.xlsx")}, "");
        window = new Window(1000, "i read qb questions");
//        processFile(new File("./dogs.pdf"));
        PacketProcess.processFile(new File("./packet1.pdf"));
        window.setTossup(0);
        for (int i = 0; i < 5; i++) {
            Team.teams[0].nameField.setText("T1 " + i);
            Team.teams[0].addPlayer();
            Team.teams[1].nameField.setText("T2 " + i);
            Team.teams[1].addPlayer();
        }
    }


}

