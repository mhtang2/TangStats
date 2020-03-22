package com.uni;

import com.uni.marker.BuzzData;
import com.uni.marker.QuestionWord;
import com.uni.question.Bonus;
import com.uni.question.Category;
import com.uni.question.Tossup;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Window extends JFrame {
    final static double SCREEN_RES = 1.61;
    final static Font defFont = new Font("Open Sans", 0, 15);
    final static Color buttonColor = new Color(0xe3e3e3);

    private boolean tossupMode = true;

    public PlayerManager playermanager = new PlayerManager();
    private JLabel fileStatus = new JLabel("No file selected");
    private JLabel questionStatus = new JLabel("Tossup 0 of 0");
    private JLabel teamControlLabel = new JLabel("No Bonus Control");
    private JCheckBox deadToggle = new JCheckBox("Dead: ");
    private JPanel questionContainer = new JPanel();
    private JFileChooser filechoose = new JFileChooser(new File("./"));
    private JPanel scoreBoard = new JPanel();

    private JComboBox<Category> categorySelect = new JComboBox<>(Category.categories);
    private JComboBox<String> subcategorySelect = new JComboBox<>(new String[]{null});

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
        playersButton.setBackground(buttonColor);
        selectButton.setBackground(buttonColor);
        playersButton.addActionListener(e -> playermanager.setVisible(true));
        selectButton.addActionListener(e -> openSet());
        fileStatus.setFont(defFont);
        fileStatus.setVerticalAlignment(SwingConstants.CENTER);

        selectContainer.add(playersButton);
        selectContainer.add(selectButton);
        selectContainer.add(fileStatus);

        //Container for controller
        JButton saveButton = new JButton("Save data");
        saveButton.setBackground(buttonColor);
        saveButton.addActionListener(e -> ExportData.saveData(filechoose));
        JPanel controlContainer = new JPanel();
        controlContainer.setBackground(Color.lightGray);
        questionStatus.setFont(defFont);
        questionStatus.setVerticalAlignment(SwingConstants.CENTER);
        controlContainer.add(questionStatus);
        controlContainer.add(saveButton);

        //Container for category selection
        JPanel categoryContainer = new JPanel();
        categoryContainer.add(new JLabel("Category: "));
        categoryContainer.add(categorySelect);
        categoryContainer.add(new JLabel("Subcategory: "));
        categoryContainer.add(subcategorySelect);
        categorySelect.addActionListener(e -> {
            Category selected = ((Category) categorySelect.getSelectedItem());
            if (tossupMode) {
                Tossup.current().category = selected;
            } else {
                Bonus.questionSet[Bonus.setidx].category = selected;
            }
            if (selected != null) {
                subcategorySelect.setModel(new DefaultComboBoxModel<>(selected.subcategories));
            } else {
                subcategorySelect.setModel(new DefaultComboBoxModel<>(new String[]{null}));
            }
            questionContainer.grabFocus();
        });
        subcategorySelect.addActionListener(e -> {
            if (tossupMode) {
                Tossup.current().subcategory = (String) subcategorySelect.getSelectedItem();
            } else {
                Bonus.questionSet[Bonus.setidx].subcategory = (String) subcategorySelect.getSelectedItem();
            }
            questionContainer.grabFocus();
        });

        //Container for toggling bonus
        JPanel toggleContainer = new JPanel();
        JButton toggleButton = new JButton("Show/Hide Bonus");
        toggleButton.setBackground(buttonColor);
        teamControlLabel.setFont(defFont);
        deadToggle.setSelected(true);
        toggleContainer.add(deadToggle);
        toggleContainer.add(teamControlLabel);
        toggleContainer.add(toggleButton);

        deadToggle.addActionListener(e -> {
            Tossup.current().dead = deadToggle.isSelected();
        });
        toggleButton.addActionListener(e -> {
            if (tossupMode) {
                if (deadToggle.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Can't show bonus for dead tossup", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                setBonus(Tossup.getCorrespondingBonus());
            } else {
                setTossup(Tossup.setidx);
            }
            tossupMode = !tossupMode;
        });


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

        //Top Container
        JPanel topContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        topContainer.add(selectContainer, gbc);
        gbc.gridx = 1;
        topContainer.add(controlContainer, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topContainer.add(categoryContainer, gbc);
        gbc.gridx = 1;
        topContainer.add(toggleContainer, gbc);

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
                setTossup(0);
            } catch (IOException | SetFormatException e1) {
                fileStatus.setText("Error reading");
                e1.printStackTrace();
            }
        }
    }

    private void setBonus(int idx) {
        if (idx < 0 || idx >= Bonus.questionSet.length) {
            return;
        }
        Bonus.setidx = idx;
        Bonus bonus = Bonus.questionSet[idx];
        categorySelect.setSelectedItem(bonus.category);
        subcategorySelect.setSelectedItem(bonus.subcategory);
        questionContainer.removeAll();
        questionContainer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JTextArea leadIn = new JTextArea(bonus.leadin);
        leadIn.setFont(defFont);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        questionContainer.add(leadIn, gbc);
        for (int c = 0; c < 3; c++) {
            int i = c;
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            JTextArea q = new JTextArea(bonus.q[i] + bonus.a[i]);
            q.setFont(defFont);
            q.setEditable(false);

            JComboBox<String> choose = new JComboBox<>(new String[]{"Dead", Team.teams[0].name, Team.teams[1].name});
            choose.addActionListener(e -> {
                @SuppressWarnings("unchecked")
                int s = ((JComboBox<String>) e.getSource()).getSelectedIndex() - 1;
                Bonus.questionSet[Bonus.setidx].score[i] = s;
            });
            questionContainer.add(q, gbc);
            gbc.gridx = 1;
            questionContainer.add(choose, gbc);
        }
        repaint();
    }

    public boolean setTossup(int idx) {
        if (idx < 0 || idx >= Tossup.questionSet.length) {
            return false;
        }

        Tossup.setidx = idx;
        Tossup q = Tossup.questionSet[idx];
        //Set selected values for tossup
        categorySelect.setSelectedItem(q.category);
        subcategorySelect.setSelectedItem(q.subcategory);
        setControllingTeam(q.controllingTeam);
        deadToggle.setSelected(q.dead);

        questionContainer.removeAll();
        questionContainer.setLayout(new FlowLayout());
        for (QuestionWord qw : q.words) {
            questionContainer.add(qw);
        }
        JButton b = new JButton("Show answer");
        b.setBackground(buttonColor);
        b.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, q.answer, "Answer", JOptionPane.INFORMATION_MESSAGE);
        });
        questionContainer.add(b);
        questionContainer.validate();
        questionStatus.setText("Tossup " + q.id + " of " + Tossup.questionSet.length);
        repaint();
        return true;
    }

    private void handleKey(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            setTossup(Tossup.setidx - 1);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            setTossup(Tossup.setidx + 1);
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
            //Get active player
            Tossup currentQ = Tossup.current();
            if (currentQ == null) continue;
            String[] activePlayers = team.teamId == 0 ? currentQ.t1Active : currentQ.t2Active;
            for (String name : activePlayers) {
                team.playerData.get(name)[3] = 0;
                scoreBoard.add(new JLabel(name));
                for (int j = 0; j < 3; j++) {
                    team.playerData.get(name)[3] += team.playerData.get(name)[j] * BuzzData.pointVals[j];
                    scoreBoard.add(new JLabel(String.valueOf(team.playerData.get(name)[j])));
                }
                scoreBoard.add(new JLabel(String.valueOf(team.playerData.get(name)[3])));
            }
        }
        validate();
    }

    public void setControllingTeam(int control) {
        deadToggle.setSelected(control < 0);
        if (control < 0) {
            teamControlLabel.setText("No Bonus Control");
            teamControlLabel.setForeground(Color.black);
            return;
        }
        String name = Team.teams[control].name;
        teamControlLabel.setText("Control: " + name);
        teamControlLabel.setForeground(Team.teamColors[control]);
        repaint();
    }
}

