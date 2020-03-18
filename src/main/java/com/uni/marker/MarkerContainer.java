package com.uni.marker;

import com.uni.Team;
import com.uni.question.QuestionWord;

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
        for (String name : Team.teams[teamId].activePlayers) {
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
                qword.handle(name, 0, teamId);
                update();
            });
            p10.addActionListener(e -> {
                qword.handle(name, 1, teamId);
                update();
            });
            p5.addActionListener(e -> {
                qword.handle(name, 2, teamId);
                update();
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
        update();
        if (Team.teams[teamId].activePlayers.isEmpty()) {
            add(new JLabel("Add more players dawg"));
        }
    }

    private void update() {
        for (int i = 0; i < Team.teams[teamId].activePlayers.size(); i++) {
            String name = Team.teams[teamId].activePlayers.get(i);
            JButton p15 = (JButton) ((JPanel) getComponent(i)).getComponent(1);
            JButton p10 = (JButton) ((JPanel) getComponent(i)).getComponent(2);
            JButton p5 = (JButton) ((JPanel) getComponent(i)).getComponent(3);
            p15.setBackground(Color.white);
            p10.setBackground(Color.white);
            p5.setBackground(Color.white);
           /* if (name.equals(qword.whoBuzzed)) {
                if (qword.pointValue == 15) {
                    p15.setBackground(Color.gray);
                } else if (qword.pointValue == 10) {
                    p10.setBackground(Color.gray);
                } else if (qword.pointValue == -5) {
                    p5.setBackground(Color.gray);
                }
            }*/
        }
    }
}

