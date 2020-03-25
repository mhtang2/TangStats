package com.uni.marker;

import com.uni.Team;

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
        JLabel teamName = new JLabel(Team.teams[teamId].name);
        teamName.setForeground(Team.teamColors[teamId]);
        add(teamName);
        for (String name : qword.parentQuestion.getActive(teamId)) {
            JPanel player = new JPanel();
            player.add(new JLabel(name));
            JButton p10 = new JButton("+10");
            JButton p15 = new JButton("+15");
            JButton p5 = new JButton("-5");
            p15.setOpaque(true);
            p10.setOpaque(true);
            p5.setOpaque(true);
            p15.setBackground(Color.white);
            p10.setBackground(Color.white);
            p5.setBackground(Color.white);
            p15.addActionListener(e -> {
                qword.handle(new BuzzData(0, name, teamId));
                dialog.update();
            });
            p10.addActionListener(e -> {
                qword.handle(new BuzzData(1, name, teamId));
                dialog.update();
            });
            p5.addActionListener(e -> {
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
            add(new JLabel("Add more players dawg"));
        }
    }

    void update() {
        for (int i = 0; i < qword.parentQuestion.getActive(teamId).size(); i++) {
            String name = qword.parentQuestion.getActive(teamId).get(i);
            JButton p15 = (JButton) ((JPanel) getComponent(i + 1)).getComponent(1);
            JButton p10 = (JButton) ((JPanel) getComponent(i + 1)).getComponent(2);
            JButton p5 = (JButton) ((JPanel) getComponent(i + 1)).getComponent(3);
            p15.setBackground(Color.white);
            p10.setBackground(Color.white);
            p5.setBackground(Color.white);
            if (qword.buzzData.samePerson(name, teamId)) {
                if (qword.buzzData.point == 0) {
                    p15.setBackground(Color.gray);
                } else if (qword.buzzData.point == 1) {
                    p10.setBackground(Color.gray);
                } else if (qword.buzzData.point == 2) {
                    p5.setBackground(Color.gray);
                }
            }
        }
    }
}

