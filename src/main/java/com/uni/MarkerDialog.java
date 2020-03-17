package com.uni;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MarkerDialog extends JDialog {
    private QuestionWord qword;
    private JPanel container = new JPanel();

    public MarkerDialog(QuestionWord qword) {
        this.qword = qword;
        setIconImage(Main.launcherIcon);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setLocationRelativeTo(qword);
        container.setLayout(new GridLayout(0, 1));
        for (String name : Team.teams[0].playerList) {
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
            p10.addActionListener(e -> {
                qword.handle(name, 10);
                update();
            });
            p15.addActionListener(e -> {
                qword.handle(name, 15);
                update();
            });
            p5.addActionListener(e -> {
                qword.handle(name, -5);
                update();
            });
            KeyAdapter ka = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        setVisible(false);
                        dispose();
                    }
                }
            };
            p15.addKeyListener(ka);
            p10.addKeyListener(ka);
            p5.addKeyListener(ka);
            player.add(p15);
            player.add(p10);
            player.add(p5);
            container.add(player);
        }
        update();
        if (Team.teams[0].playerList.isEmpty() && Team.teams[1].playerList.isEmpty()) {
            container.add(new JLabel("Add more players dawg"));
        }
        add(container);
        pack();
    }

    public void update() {
        for (int i = 0; i < Team.teams[0].playerList.size(); i++) {
            String name = Team.teams[1].playerList.get(i);
            JButton p15 = (JButton) ((JPanel) container.getComponent(i)).getComponent(1);
            JButton p10 = (JButton) ((JPanel) container.getComponent(i)).getComponent(2);
            JButton p5 = (JButton) ((JPanel) container.getComponent(i)).getComponent(3);
            p15.setBackground(Color.white);
            p10.setBackground(Color.white);
            p5.setBackground(Color.white);
            if (name == qword.whoBuzzed) {
                if (qword.pointValue == 15) {
                    p15.setBackground(Color.gray);
                } else if (qword.pointValue == 10) {
                    p10.setBackground(Color.gray);
                } else if (qword.pointValue == -5) {
                    p5.setBackground(Color.gray);
                }
            }
        }
    }

    public int query() {
        setVisible(true);
        revalidate();
        pack();
        return 0;
    }
}
