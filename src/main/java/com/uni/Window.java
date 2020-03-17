package com.uni;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame {
    final static double SCREEN_RES = 1.61;
    final static Font defFont = new Font("Open Sans", 0, 15);

    public PlayerManager playermanager = new PlayerManager();
    private JLabel fileStatus = new JLabel("No file selected");
    private JLabel questionStatus = new JLabel("Tossup 0 of 0");
    private JPanel questionContainer = new JPanel();
    private JFileChooser filechoose = new JFileChooser(new File("./"));
    private JPanel scoreBoard = new JPanel();

    public Window(int base, String title) {
        //Close operation confirm
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Reader", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        //Setup frame
        setIconImage(Main.launcherIcon);
        setTitle(title);
        setSize(base, (int) (base / SCREEN_RES));
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        //Container for file open
        JPanel selectContainer = new JPanel();
        JButton playersButton = new JButton("Manage Players");
        JButton selectButton = new JButton("Select packet");
        playersButton.addActionListener(e -> playermanager.setVisible(true));
        selectButton.addActionListener(e -> openSet());
        fileStatus.setFont(defFont);
        fileStatus.setVerticalAlignment(0);

        selectContainer.add(playersButton);
        selectContainer.add(selectButton);
        selectContainer.add(fileStatus);

        //Container for controller
        JButton saveButton = new JButton("Save data");
        saveButton.addActionListener(e -> Main.saveData(filechoose));
        JPanel controlContainer = new JPanel();
        controlContainer.setBackground(Color.lightGray);
        questionStatus.setFont(defFont);
        questionStatus.setVerticalAlignment(0);
        controlContainer.add(questionStatus);
        controlContainer.add(saveButton);

        //Container for question
        questionContainer.setBackground(Color.white);
        questionContainer.setFocusable(true);
        questionContainer.requestFocus();
        questionContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                questionContainer.grabFocus();
            }
        });
        questionContainer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());

            }
        });

        //Main containers
        JPanel topContainer = new JPanel(new GridLayout(1, 2));
        topContainer.add(selectContainer);
        topContainer.add(controlContainer);

        //Scoreboard
        scoreBoard.setLayout(new GridLayout(0, 5));
        scoreBoard.add(new JLabel("Player"));
        scoreBoard.add(new JLabel("+15"));
        scoreBoard.add(new JLabel("+10"));
        scoreBoard.add(new JLabel("-5"));
        add(topContainer, BorderLayout.PAGE_START);
        add(questionContainer);
        add(scoreBoard, BorderLayout.PAGE_END);
        questionContainer.grabFocus();
        revalidate();

    }

    public void openSet() {
        filechoose.setFileFilter(new FileNameExtensionFilter("pdf", "PDF"));
        int r = filechoose.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                Main.processFile(filechoose.getSelectedFile());
                fileStatus.setText("Read " + Tossup.questionSet.length + " questions from " + filechoose.getSelectedFile().getName());
                //Reset player counts
                Team.resetScores();
                Main.window.updateScoreboard();
                setQuestion(0);
            } catch (IOException | SetFormatException e1) {
                fileStatus.setText("Error reading");
                e1.printStackTrace();
            }
        }
    }

    public void setQuestion(int idx) {
        if (idx < 0 || idx >= Tossup.questionSet.length) {
            return;
        }
        Tossup.setidx = idx;
        Tossup q = Tossup.questionSet[idx];
        questionContainer.removeAll();
        for (QuestionWord qw : q.words) {
            questionContainer.add(qw);
        }
        JButton b = new JButton("Show answer");
        b.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, q.answer, "Answer", JOptionPane.INFORMATION_MESSAGE);
        });
        questionContainer.add(b);
        questionContainer.validate();
        questionStatus.setText("Tossup " + q.id + " of " + Tossup.questionSet.length);
        repaint();
    }

    private void handleKey(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            setQuestion(Tossup.setidx - 1);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            setQuestion(Tossup.setidx + 1);
        }
    }

    public void updateScoreboard() {
        scoreBoard.removeAll();
        scoreBoard.add(new JLabel("Player"));
        scoreBoard.add(new JLabel("+15"));
        scoreBoard.add(new JLabel("+10"));
        scoreBoard.add(new JLabel("-5"));
        scoreBoard.add(new JLabel("Point total"));
        for (Team team : Team.teams) {
            //Team stats
            team.calculateStats();
            scoreBoard.add(new JLabel(team.name));
            for (int j = 0; j < 3; j++) {
                scoreBoard.add(new JLabel(String.valueOf(team.teamStats[j])));
            }
            scoreBoard.add(new JLabel(String.valueOf(team.teamStats[3])));
            //Individual stats
            for (int i = 0; i < team.playerList.size(); i++) {
                String name = team.playerList.get(i);
                team.playerData.get(name)[3] = 0;
                scoreBoard.add(new JLabel(name));
                for (int j = 0; j < 3; j++) {
                    team.playerData.get(name)[3] += team.playerData.get(name)[j] * Tossup.pointVals[j];
                    scoreBoard.add(new JLabel(String.valueOf(team.playerData.get(name)[j])));
                }
                scoreBoard.add(new JLabel(String.valueOf(team.playerData.get(name)[3])));
            }
        }
        validate();
    }
}

