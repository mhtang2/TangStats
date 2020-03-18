package com.uni.question;


import com.uni.marker.MarkerDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuestionWord extends JLabel {
    static Font font = new Font(Font.SERIF, Font.PLAIN, 20);
    private Tossup parentQuestion;
    private Color defaultBG = Color.white;
    private final Color hoverBG = Color.lightGray;

    public int wordID;


    public void handle(String name, int n, int teamId) {
       /* if (whoBuzzed != null) {
            if (pointValue == 15) {
                PlayerManager.playerData.get(whoBuzzed)[0] -= 1;
            } else if (pointValue == 10) {
                PlayerManager.playerData.get(whoBuzzed)[1] -= 1;
            } else {
                PlayerManager.playerData.get(whoBuzzed)[2] -= 1;
            }
        }
        if (whoBuzzed != null && whoBuzzed.equals(name) && pointValue == n) {
            whoBuzzed = null;
            pointValue = 0;
            defaultBG=Color.white;
            Main.window.updateScoreboard();
            return;
        }
        whoBuzzed = name;
        pointValue = n;
        if (n == 15) {
            PlayerManager.playerData.get(name)[0] += 1;
            defaultBG = Color.cyan;
        } else if (n == 10) {
            PlayerManager.playerData.get(name)[1] += 1;
            defaultBG = Color.green;
        } else {
            PlayerManager.playerData.get(name)[2] += 1;
            defaultBG = Color.red;
        }
        Main.window.updateScoreboard();*/
    }

    public QuestionWord(int wordID, String word, Tossup parentQuestion) {
        this.wordID = wordID;
        this.parentQuestion = parentQuestion;
        setText(word);
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

    private void handleClick() {
        new MarkerDialog(this).query();
    }

}
