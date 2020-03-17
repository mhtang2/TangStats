package com.uni;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuestionWord extends JLabel {
    static Font font = new Font(Font.SERIF, 0, 20);
    private Color defaultBG = Color.white;
    private final Color hoverBG = Color.lightGray;

    public int wordID;
    private String word;

    //Mark who buzzed on this word
    public String whoBuzzed = null;
    public int pointValue = 0;

    public void handle(String name, int n) {
//        if (whoBuzzed != null) {
//            if (pointValue == 15) {
//                PlayerManager.playerData.get(whoBuzzed)[0] -= 1;
//            } else if (pointValue == 10) {
//                PlayerManager.playerData.get(whoBuzzed)[1] -= 1;
//            } else {
//                PlayerManager.playerData.get(whoBuzzed)[2] -= 1;
//            }
//        }
//        if (whoBuzzed != null && whoBuzzed.equals(name) && pointValue == n) {
//            whoBuzzed = null;
//            pointValue = 0;
//            defaultBG=Color.white;
//            Main.window.updateScoreboard();
//            return;
//        }
//        whoBuzzed = name;
//        pointValue = n;
//        if (n == 15) {
//            PlayerManager.playerData.get(name)[0] += 1;
//            defaultBG = Color.cyan;
//        } else if (n == 10) {
//            PlayerManager.playerData.get(name)[1] += 1;
//            defaultBG = Color.green;
//        } else {
//            PlayerManager.playerData.get(name)[2] += 1;
//            defaultBG = Color.red;
//        }
//        Main.window.updateScoreboard();
    }

    public QuestionWord(int wordID, String word) {
        this.wordID = wordID;
        this.word = word;
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
