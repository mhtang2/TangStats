package com.uni;

import com.uni.datamanager.CompileWindow;
import com.uni.datamanager.ExportRound;
import com.uni.gui.UIButton;
import com.uni.gui.UILabel;
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
    public static final Font defFont = new Font("Open Sans", Font.PLAIN, 15);
    public static final Font boldFont = new Font("Open Sans", Font.BOLD, 15);
    final static Color buttonColor = new Color(0xe3e3e3);

    private boolean tossupMode = true;
    public int roundNumber = 0;

    public PlayerManager playermanager = new PlayerManager();
    private UILabel fileStatus = new UILabel("No file selected");
    private UIButton toggleBonusButton;
    private UILabel questionStatus = new UILabel("Tossup 0 of 0");
    private UILabel teamControlLabel = new UILabel("No Bonus Control");
    private JCheckBox deadToggle = new JCheckBox(": Dead");
    private JPanel questionContainer = new JPanel();
    private JFileChooser filechoose = new JFileChooser(new File("./"));
    private JPanel scoreBoard = new JPanel();
    private JPanel bottomContainer = new JPanel();
    private UILabel scoreLabelT1 = new UILabel("Team 0");
    private UILabel scoreLabelT2 = new UILabel("Team 1");
    private JComboBox<Category> categorySelect = new JComboBox<>(Category.categories.toArray(new Category[0]));
    private JComboBox<String> subcategorySelect = new JComboBox<>(new String[]{null});
    private JComboBox<String> roundSelect;
    private JFrame scoreboard = new JFrame("Scoreboard");

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
        selectContainer.setLayout(new GridLayout(1, 3));
        UIButton playersButton = new UIButton("Manage Players", (e) -> playermanager.setVisible(true));
        UIButton selectButton = new UIButton("Select packet", (e) -> openSet());
        Eval.eval();
        fileStatus.setFont(defFont);
        fileStatus.setVerticalAlignment(SwingConstants.CENTER);

        selectContainer.add(playersButton);
        selectContainer.add(selectButton);

        //Container for controller
        JPanel controlContainer = new JPanel();
        controlContainer.setBackground(Color.lightGray);
        questionStatus.setFont(defFont);
        questionStatus.setVerticalAlignment(SwingConstants.CENTER);
        controlContainer.add(questionStatus);

        //Container for category selection
        controlContainer.add(fileStatus);
        JPanel categoryContainer = new JPanel();
        categoryContainer.add(new UILabel("Category: "));
        categoryContainer.add(categorySelect);
        categoryContainer.add(new UILabel("Subcategory: "));
        categoryContainer.add(subcategorySelect);
        categorySelect.setFont(defFont);
        subcategorySelect.setFont(defFont);
        categorySelect.addItemListener(e -> {
            Category selected = ((Category) categorySelect.getSelectedItem());
            if (e.getStateChange() == ItemEvent.SELECTED || selected == null) {
                if (tossupMode) {
                    Tossup.current().category = selected;
                } else {
                    Bonus.questionSet[Bonus.setidx].category = selected;
                }
                if (selected != null) {
                    subcategorySelect.setModel(new DefaultComboBoxModel<>(selected.subcategories.toArray(new String[0])));
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

        GridBagConstraints gbc;
        //Container for toggling bonus
        JPanel toggleContainer = new JPanel();
        toggleContainer.setLayout(new GridLayout(1, 2));
        teamControlLabel.setFont(defFont);
        deadToggle.setSelected(true);
        deadToggle.setFont(defFont);
        toggleContainer.add(deadToggle);
        toggleContainer.add(teamControlLabel);

        deadToggle.addActionListener(e -> {
            Tossup.current().dead = deadToggle.isSelected();
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
        gbc = new GridBagConstraints();
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

        //Scoreboard && bottom container
        scoreBoard.setLayout(new GridLayout(0, 4));
        scoreBoard.add(new UILabel("Player"));
        scoreBoard.add(new UILabel("+15"));
        scoreBoard.add(new UILabel("+10"));
        scoreBoard.add(new UILabel("-5"));
        scoreboard.add(scoreBoard);
        bottomContainer.setLayout(new GridBagLayout());
        scoreLabelT1.setForeground(Team.teamColors[0]);
        scoreLabelT2.setForeground(Team.teamColors[1]);
        scoreLabelT1.setFont(new Font("Open Sans", Font.PLAIN, 30));
        scoreLabelT2.setFont(new Font("Open Sans", Font.PLAIN, 30));

        JPanel lrcontainer = new JPanel();
        UIButton showButton = new UIButton("Scoreboard");
        showButton.addButtonListener(e -> {
            scoreboard.setVisible(!scoreboard.isVisible());
            scoreboard.pack();
        });
        toggleBonusButton = new UIButton("Show bonus");
        toggleBonusButton.addButtonListener(e -> {
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
        UIButton leftButton = new UIButton("<");
        leftButton.addButtonListener((e) -> setTossup(Tossup.setidx - 1));
        UIButton rightButton = new UIButton(">");
        rightButton.addButtonListener((e) -> setTossup(Tossup.setidx + 1));
        UIButton saveButton = new UIButton("Export Round Stats");
        saveButton.addButtonListener(e -> ExportRound.saveRoundData(filechoose));
        String[] roundstrings = new String[20];
        Arrays.setAll(roundstrings, String::valueOf);
        roundSelect = new JComboBox<>(roundstrings);
        roundSelect.addActionListener(e -> {
            roundNumber = roundSelect.getSelectedIndex();
        });
        lrcontainer.setLayout(new GridLayout(1, 4));
        lrcontainer.add(showButton);
        lrcontainer.add(toggleBonusButton);
        lrcontainer.add(leftButton);
        lrcontainer.add(rightButton);
        JPanel saveContainer = new JPanel();
        UIButton compileButton = new UIButton("Total Summary Tool");
        compileButton.addButtonListener(e -> new CompileWindow());
        saveContainer.setLayout(new BoxLayout(saveContainer, BoxLayout.X_AXIS));
        saveContainer.add(new UILabel("Round: "), gbc);
        saveContainer.add(roundSelect, gbc);
        saveContainer.add(saveButton, gbc);
        saveContainer.add(compileButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        bottomContainer.add(scoreLabelT1, gbc);
        gbc.gridy = 1;
        bottomContainer.add(scoreLabelT2, gbc);
        gbc.weightx = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 0;
        bottomContainer.add(lrcontainer, gbc);
        gbc.gridy = 1;
        bottomContainer.add(saveContainer, gbc);

        add(topContainer, BorderLayout.PAGE_START);
        add(questionContainer);
        add(bottomContainer, BorderLayout.PAGE_END);
        questionContainer.grabFocus();
        revalidate();

    }

    public void onCategoryChange() {
        categorySelect.setModel(new DefaultComboBoxModel<>(Category.categories.toArray(new Category[0])));
        Tossup t0 = Tossup.questionSet[0];
        subcategorySelect.setModel(new DefaultComboBoxModel<>(t0 == null || t0.category == null ? new String[]{null} : t0.category.subcategories.toArray(new String[0])));
    }

    private void openSet() {
        filechoose.setFileFilter(new FileNameExtensionFilter("pdf", "PDF"));
        int r = filechoose.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                PacketProcess.processFile(filechoose.getSelectedFile());
                fileStatus.setText(filechoose.getSelectedFile().getName());
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
        toggleBonusButton.setText("Show Tossup");
        Bonus.setidx = idx;
        Bonus bonus = Bonus.questionSet[idx];
        bonus.controllingTeam = Tossup.current().controllingTeam;
        categorySelect.setSelectedItem(bonus.category);
        subcategorySelect.setSelectedItem(bonus.subcategory);
        questionContainer.removeAll();
        questionContainer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JTextArea leadIn = new JTextArea(bonus.leadin);
        leadIn.setEditable(false);
        leadIn.setFont(Bonus.font);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;
        questionContainer.add(leadIn, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int c = 0; c < 3; c++) {
            int i = c;
            JTextArea q = new JTextArea(bonus.q[i].trim() + "\n" + bonus.a[i].trim());
            q.setFont(Bonus.font);
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
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            questionContainer.add(q, gbc);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy++;
            questionContainer.add(choose, gbc);
        }
        repaint();
        validate();
    }

    public void setTossup(int idx) {
        if (idx < 0 || idx >= Tossup.questionSet.length) {
            return;
        }
        toggleBonusButton.setText("Show Bonus");
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
        JTextArea answerT = new JTextArea("\n" + q.answer.replaceAll("\n", ""));
        answerT.setEditable(false);
        answerT.setFont(Bonus.font);
        answerT.setLineWrap(true);
        answerT.setPreferredSize(new Dimension(getWidth() - 50, 100));
        questionContainer.add(answerT);
        questionContainer.validate();
        questionStatus.setText("Tossup " + q.id + " of " + Tossup.questionSet.length + " in");
        updateScoreboard();
        repaint();
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
        UILabel teamLabel = team.teamId == 0 ? scoreLabelT1 : scoreLabelT2;
        teamLabel.setText(team.teamStats[3] + " - " + team.name);
    }

    public void updateScoreboard() {
        scoreBoard.removeAll();
        scoreBoard.add(new UILabel("Player"));
        scoreBoard.add(new UILabel("+15"));
        scoreBoard.add(new UILabel("+10"));
        scoreBoard.add(new UILabel("-5"));
        for (Team team : Team.teams) {
            //Team stats
            updateScore(team);
            scoreBoard.add(new UILabel(team.name));
            for (int j = 0; j < 3; j++) {
                scoreBoard.add(new UILabel(String.valueOf(team.teamStats[j])));
            }
            UILabel points = new UILabel(String.valueOf(team.teamStats[3]));
            points.setForeground(Team.teamColors[team.teamId]);
            //Individual stats
            //Get active player
            Tossup currentQ = Tossup.current();
            if (currentQ == null) continue;
            for (String name : currentQ.getActive(team.teamId)) {
                team.playerData.get(name)[3] = 0;
                scoreBoard.add(new UILabel(name));
                for (int j = 0; j < 3; j++) {
                    team.playerData.get(name)[3] += team.playerData.get(name)[j] * BuzzData.pointVals[j];
                    scoreBoard.add(new UILabel(String.valueOf(team.playerData.get(name)[j])));
                }
            }
        }
        scoreboard.validate();
        scoreboard.setLocationRelativeTo(null);
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

