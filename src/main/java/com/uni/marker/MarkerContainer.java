package com.uni.marker;

import com.uni.Team;
import com.uni.gui.UIButton;
import com.uni.gui.UILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MarkerContainer extends JPanel {
    private QuestionWord qword;
    private int teamId;

    MarkerContainer(QuestionWord qword, int teamId, MarkerDialog dialog) {
        this.qword = qword;
        this.teamId = teamId;
        setLayout(new GridLayout(0, 1));
        UILabel teamName = new UILabel(Team.teams[teamId].name,true);
        teamName.setForeground(Team.teamColors[teamId]);
        add(teamName);
        for (String name : qword.parentQuestion.getActive(teamId)) {
            JPanel player = new JPanel();
            player.setLayout(new GridLayout(1,4));
            player.add(new UILabel(name+": ",true));
            UIButton p10 = new UIButton("+10");
            UIButton p15 = new UIButton("+15");
            UIButton p5 = new UIButton("-5");
            p15.setOpaque(true);
            p10.setOpaque(true);
            p5.setOpaque(true);
            p15.setNorm(UIButton.defaultNorm);
            p10.setNorm(UIButton.defaultNorm);
            p5.setNorm(UIButton.defaultNorm);
            p15.addButtonListener(e -> {
                qword.handle(new BuzzData(0, name, teamId));
                dialog.update();
            });
            p10.addButtonListener(e -> {
                qword.handle(new BuzzData(1, name, teamId));
                dialog.update();
            });
            p5.addButtonListener(e -> {
                qword.handle(new BuzzData(2, name, teamId));
                dialog.update();
            });
            KeyAdapter ka = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        setVisible(false);
                        dialog.dispose();
                    }
                }
            };
            p15.addKeyListener(ka);
            p10.addKeyListener(ka);
            p5.addKeyListener(ka);
            player.add(p15);
            player.add(p10);
            player.add(p5);
            add(player);
        }
        if (qword.parentQuestion.getActive(teamId).isEmpty()) {
            add(new UILabel("Add more players dawg"));
        }
    }

    void update() {
        for (int i = 0; i < qword.parentQuestion.getActive(teamId).size(); i++) {
            String name = qword.parentQuestion.getActive(teamId).get(i);
            UIButton p15 = (UIButton) ((JPanel) getComponent(i + 1)).getComponent(1);
            UIButton p10 = (UIButton) ((JPanel) getComponent(i + 1)).getComponent(2);
            UIButton p5 = (UIButton) ((JPanel) getComponent(i + 1)).getComponent(3);
            p15.setNorm(UIButton.defaultNorm);
            p10.setNorm(UIButton.defaultNorm);
            p5.setNorm(UIButton.defaultNorm);
            if (qword.buzzData.samePerson(name, teamId)) {
                if (qword.buzzData.point == 0) {
                    p15.setNorm(Color.gray);
                } else if (qword.buzzData.point == 1) {
                    p10.setNorm(Color.gray);
                } else if (qword.buzzData.point == 2) {
                    p5.setNorm(Color.gray);
                }
            }
        }
    }
}

