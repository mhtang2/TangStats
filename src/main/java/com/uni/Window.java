package com.uni;

import com.uni.datamanager.CompileWindow;
import com.uni.datamanager.ExportRound;
import com.uni.marker.BuzzData;
import com.uni.marker.Eval;
import com.uni.marker.QuestionWord;
import com.uni.question.Bonus;
import com.uni.question.Category;
import com.uni.question.Tossup;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Window extends JFrame {
    final static double SCREEN_RES = 1.61;
    final static Font defFont = new Font("Open Sans", 0, 15);
    final static Color buttonColor = new Color(0xe3e3e3);

    private boolean tossupMode = true;
    public int roundNumber = 0;

    public PlayerManager playermanager = new PlayerManager();
    private JLabel fileStatus = new JLabel("No file selected");
    private JLabel questionStatus = new JLabel("Tossup 0 of 0");
    private JLabel teamControlLabel = new JLabel("No Bonus Control");
    private JCheckBox deadToggle = new JCheckBox("Dead: ");
    private JPanel questionContainer = new JPanel();
    private JFileChooser filechoose = new JFileChooser(new File("./"));
    private JPanel scoreBoard = new JPanel();
    private JPanel scoreBoardContainer = new JPanel();
    private JLabel scoreLabelT1 = new JLabel("Team 0");
    private JLabel scoreLabelT2 = new JLabel("Team 1");
    private JComboBox<Category> categorySelect = new JComboBox<>(Category.categories);
    private JComboBox<String> subcategorySelect = new JComboBox<>(new String[]{null});
    private JComboBox<String> roundSelect;

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
        Eval.eval();
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
        saveButton.addActionListener(e -> ExportRound.saveRoundData(filechoose));
        JPanel controlContainer = new JPanel();
        controlContainer.setBackground(Color.lightGray);
        questionStatus.setFont(defFont);
        questionStatus.setVerticalAlignment(SwingConstants.CENTER);
        String[] roundstrings = new String[20];
        Arrays.setAll(roundstrings, String::valueOf);
        roundSelect = new JComboBox<>(roundstrings);
        roundSelect.addActionListener(e -> {
            roundNumber = roundSelect.getSelectedIndex();
        });
        controlContainer.add(questionStatus);

        //Container for category selection
        JPanel categoryContainer = new JPanel();
        categoryContainer.add(new JLabel("Category: "));
        categoryContainer.add(categorySelect);
        categoryContainer.add(new JLabel("Subcategory: "));
        categoryContainer.add(subcategorySelect);
        categorySelect.addItemListener(e -> {
            Category selected = ((Category) categorySelect.getSelectedItem());
            if (e.getStateChange() == ItemEvent.SELECTED || selected == null) {
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
            }
        });
        subcategorySelect.addItemListener(e -> {
            String selected = (String) subcategorySelect.getSelectedItem();
            if (e.getStateChange() == ItemEvent.SELECTED || selected == null) {
                if (tossupMode) {
                    Tossup.current().subcategory = selected;
                } else {
                    Bonus.questionSet[Bonus.setidx].subcategory = selected;
                }
                questionContainer.grabFocus();
            }
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

        //Scoreboard && scoreboard container
        scoreBoard.setLayout(new GridLayout(0, 4));
        scoreBoard.add(new JLabel("Player"));
        scoreBoard.add(new JLabel("+15"));
        scoreBoard.add(new JLabel("+10"));
        scoreBoard.add(new JLabel("-5"));
        scoreBoardContainer.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        scoreBoardContainer.add(scoreBoard, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        scoreLabelT1.setForeground(Team.teamColors[0]);
        scoreLabelT2.setForeground(Team.teamColors[1]);
        scoreLabelT1.setFont(new Font("Open Sans", Font.BOLD, 30));
        scoreLabelT2.setFont(new Font("Open Sans", Font.BOLD, 30));
        scoreBoardContainer.add(scoreLabelT1, gbc);
        gbc.gridy = 1;
        scoreBoardContainer.add(scoreLabelT2, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton b = new JButton("Show answer");
        b.setBackground(buttonColor);
        b.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, Tossup.current().answer, "Answer", JOptionPane.INFORMATION_MESSAGE);
        });
        scoreBoardContainer.add(b, gbc);
        JPanel saveContainer = new JPanel();
        JButton compileButton = new JButton("Compile Total Summary");
        compileButton.setBackground(buttonColor);
        compileButton.addActionListener(e -> new CompileWindow());
        saveContainer.add(new JLabel("Round: "));
        saveContainer.add(roundSelect);
        saveContainer.add(saveButton);
        saveContainer.add(compileButton);
        gbc.gridy = 1;
        scoreBoardContainer.add(saveContainer, gbc);

        add(topContainer, BorderLayout.PAGE_START);
        add(questionContainer);
        add(scoreBoardContainer, BorderLayout.PAGE_END);
        questionContainer.grabFocus();
        revalidate();

    }

    public void openSet() {
        filechoose.setFileFilter(new FileNameExtensionFilter("pdf", "PDF"));
        int r = filechoose.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                PacketProcess.processFile(filechoose.getSelectedFile());
                fileStatus.setText("Read " + Tossup.questionSet.length + " questions from " + filechoose.getSelectedFile().getName());
                //Reset player counts
                Team.resetTeams();
                Team.resetScores();
                updateScoreboard();
                playermanager = new PlayerManager();
                setTossup(0);
            } catch (IOException e1) {
                fileStatus.setText("Error reading");
                e1.printStackTrace();
            }
        }
    }

    private void setBonus(int idx) {
        if (idx < 0 || idx >= Bonus.questionSet.length) {
            return;
        }
        tossupMode = false;
        Bonus.setidx = idx;
        Bonus bonus = Bonus.questionSet[idx];
        bonus.controllingTeam = Tossup.current().controllingTeam;
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
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        questionContainer.add(leadIn, gbc);
        for (int c = 0; c < 3; c++) {
            int i = c;
            gbc.gridwidth = 1;
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            JTextArea q = new JTextArea(bonus.q[i] + bonus.a[i]);
            q.setFont(defFont);
            q.setEditable(false);

            JComboBox<String> choose = new JComboBox<>(new String[]{"Dead", Team.teams[0].name, Team.teams[1].name});
            choose.setSelectedIndex(bonus.score[i] + 1);
            choose.addActionListener(e -> {
                @SuppressWarnings("unchecked")
                int s = ((JComboBox<String>) e.getSource()).getSelectedIndex() - 1;
                Bonus.questionSet[Bonus.setidx].score[i] = s;
                updateScore(Team.teams[0]);
                updateScore(Team.teams[1]);
            });
            questionContainer.add(q, gbc);
            gbc.gridx = 1;
            questionContainer.add(choose, gbc);
        }
        repaint();
        validate();
    }

    public void setTossup(int idx) {
        if (idx < 0 || idx >= Tossup.questionSet.length) {
            return;
        }
        tossupMode = true;
        Tossup.setidx = idx;
        Tossup q = Tossup.questionSet[idx];
        for (int i = 0; i < 2; i++) {
            if (q.getActive(i).isEmpty() && idx > 0) {
                q.getActive(i).addAll(Tossup.questionSet[idx - 1].getActive(i));
            }
        }
        playermanager.reconstructCanvas();
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
        questionContainer.validate();
        questionStatus.setText("Tossup " + q.id + " of " + Tossup.questionSet.length);
        updateScoreboard();
        repaint();
        return;
    }

    private void handleKey(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            setTossup(Tossup.setidx - 1);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            setTossup(Tossup.setidx + 1);
        }
    }

    public void updateScore(Team team) {
        team.calculateStats();
        JLabel teamLabel = team.teamId == 0 ? scoreLabelT1 : scoreLabelT2;
        teamLabel.setText(team.teamStats[3] + " - " + team.name);
    }

    public void updateScoreboard() {
        scoreBoard.removeAll();
        scoreBoard.add(new JLabel("Player"));
        scoreBoard.add(new JLabel("+15"));
        scoreBoard.add(new JLabel("+10"));
        scoreBoard.add(new JLabel("-5"));
        for (Team team : Team.teams) {
            //Team stats
            updateScore(team);
            scoreBoard.add(new JLabel(team.name));
            for (int j = 0; j < 3; j++) {
                scoreBoard.add(new JLabel(String.valueOf(team.teamStats[j])));
            }
            JLabel points = new JLabel(String.valueOf(team.teamStats[3]));
            points.setForeground(Team.teamColors[team.teamId]);
            //Individual stats
            //Get active player
            Tossup currentQ = Tossup.current();
            if (currentQ == null) continue;
            for (String name : currentQ.getActive(team.teamId)) {
                team.playerData.get(name)[3] = 0;
                scoreBoard.add(new JLabel(name));
                for (int j = 0; j < 3; j++) {
                    team.playerData.get(name)[3] += team.playerData.get(name)[j] * BuzzData.pointVals[j];
                    scoreBoard.add(new JLabel(String.valueOf(team.playerData.get(name)[j])));
                }
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

