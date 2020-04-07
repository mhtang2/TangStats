package com.uni.marker;


import com.uni.Main;
import com.uni.PlayerManager;
import com.uni.Team;
import com.uni.marker.BuzzData;
import com.uni.marker.MarkerDialog;
import com.uni.question.Tossup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuestionWord extends JLabel {
    public static Font font = new Font(Font.SERIF, Font.PLAIN, 20);
    public Tossup parentQuestion;
    private Color defaultBG = Color.white;
    private final Color hoverBG = Color.lightGray;

    public int wordID;

    public BuzzData buzzData = new BuzzData(-1, null, -1);

    private void handleClick() {
        new MarkerDialog(this).query();
    }

    void handle(BuzzData newData) {
        if (buzzData.name != null) {
            Team.teams[buzzData.teamId].playerData.get(buzzData.name)[buzzData.point] -= 1;
        }
        if (newData.sameData(buzzData)) {
            buzzData = new BuzzData(-1, null, -1);
            if (newData.point == 0 || newData.point == 1) parentQuestion.setControllingTeam(-1);
            defaultBG = Color.white;
            setBackground(defaultBG);
            Main.window.updateScoreboard();
            return;
        }

        //Handle setting question control
        if (newData.point == 2 && buzzData != null) {
            //neg
            buzzData.point = 2;
            parentQuestion.setControllingTeam(-1);
        } else {
            if ((buzzData.name == null || buzzData.point == 2) && parentQuestion.controllingTeam > -1) {
                JOptionPane.showMessageDialog(null, "Warning: You just marked multiple correct buzzes", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            parentQuestion.setControllingTeam(newData.teamId);
        }

        //Process buzz
        buzzData = newData;
        if (newData.point == 0) {
            defaultBG = Color.cyan;
        } else if (newData.point == 1) {
            defaultBG = Color.green;
        } else if (newData.point == 2) {
            defaultBG = Color.red;
        }
        setBackground(defaultBG);

        Team.teams[buzzData.teamId].playerData.get(buzzData.name)[buzzData.point] += 1;
        Main.window.updateScoreboard();
    }

    public QuestionWord(int wordID, String word, Tossup parentQuestion) {
        this.wordID = wordID;
        this.parentQuestion = parentQuestion;
        setText("<html>"+word.trim()+"</html>");
        setFont(font);
        setOpaque(true);
        setBackground(defaultBG);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setBackground(hoverBG);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setBackground(defaultBG);
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                handleClick();
            }
        });
    }


}
