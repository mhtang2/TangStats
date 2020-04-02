package com.uni;

import com.uni.gui.UIButton;
import com.uni.gui.UILabel;
import com.uni.marker.BuzzData;
import com.uni.question.Bonus;
import com.uni.question.Tossup;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.FormChoiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Team {
    public static final Color[] teamColors = new Color[]{new Color(0xFFA500), Color.blue};
    public static Team[] teams = new Team[2];

    public ArrayList<String> playerList = new ArrayList<>();
    public Map<String, int[]> playerData = new HashMap<>();

    //Stuff for adding things in player manager
    JPanel canvas = new JPanel();
    JTextField nameField = new JTextField(20);
    public String name;
    public int[] teamStats = new int[]{0, 0, 0, 0};
    //0 or 1 value
    public int teamId;

    Team(String name, int teamId) {
        this.name = name;
        this.teamId = teamId;
        //Container for team 1 players
        canvas.setLayout(new GridLayout(0, 1));
        JPanel inputContainer = new JPanel();
        inputContainer.add(new UILabel("Add Player: "));
        inputContainer.add(nameField);

        JPanel teamNameContainer = new JPanel();
        teamNameContainer.setLayout(new GridLayout(1, 2));
        UILabel teamNameLabel = new UILabel(name, true);
        teamNameLabel.setForeground(teamColors[teamId]);
        UIButton changeName = new UIButton("Set Team Name");

        teamNameContainer.add(teamNameLabel);
        teamNameContainer.add(changeName);
        nameField.addActionListener(actionEvent -> addPlayer());
        canvas.add(teamNameContainer);
        canvas.add(inputContainer);

        changeName.addButtonListener((actionEvent) -> {
            String newName = JOptionPane.showInputDialog("New team name: ");
            if (newName != null && newName.length() > 0) {
                this.name = newName;
                teamNameLabel.setText(newName);
                Main.window.playermanager.updateGraphics();
            }
        });
    }

    public static void resetTeams() {
        Team.teams[0] = new Team("team 0", 0);
        Team.teams[1] = new Team("team 1", 1);
    }

    public void reconstructCanvas() {
        while (canvas.getComponentCount() > 2) {
            canvas.remove(2);
        }
        ArrayList<String> activelist = Tossup.current().getActive(teamId);
        for (String player : playerList) {
            addPlayerContainer(player, activelist.contains(player));
        }
    }

    private void addPlayerContainer(String playerName, boolean active) {
        JPanel playerContainer = new JPanel();
        UILabel removeButton = new UILabel();
        removeButton.setIcon(PlayerManager.xIcon);
        JToggleButton togglePlayer = new JToggleButton(active ? "Active" : "Inactive", !active);
        togglePlayer.setBackground(Color.white);
        playerContainer.add(new UILabel(playerName));
        playerContainer.add(togglePlayer);
        playerContainer.add(removeButton);
        canvas.add(playerContainer);
        Main.window.playermanager.updateGraphics();
        //Remove self from canvas and list of players
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                playerList.remove(playerName);
                playerData.remove(playerName);
                Tossup.current().getActive(teamId).remove(playerName);
                canvas.remove(playerContainer);
                Main.window.playermanager.updateGraphics();
            }
        });
        togglePlayer.addItemListener((itemEvent) -> {
            if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                Tossup.current().getActive(teamId).add(playerName);
                togglePlayer.setText("Active");
            } else {
                Tossup.current().getActive(teamId).remove(playerName);
                togglePlayer.setText("Inactive");
            }
            Main.window.playermanager.updateGraphics();
        });
    }

    void addPlayer() {
        String playerName = nameField.getText();
        if (playerName.length() < 1 || playerList.contains(playerName)) {
            JOptionPane.showMessageDialog(null, "Invalid or duplicate player name", "bad player", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        nameField.setText("");
        playerList.add(playerName);
        Tossup.current().getActive(teamId).add(playerName);
        playerData.put(playerName, new int[]{0, 0, 0, 0, 0});
        addPlayerContainer(playerName, true);
    }

    void calculateStats() {
        teamStats = new int[]{0, 0, 0, 0};
        for (String key : playerList) {
            int[] playerstats = playerData.get(key);
            for (int i = 0; i < 3; i++) {
                teamStats[i] += playerstats[i];
            }
        }
        //Calculate total
        teamStats[3] = 0;
        for (int j = 0; j < 3; j++) {
            teamStats[3] += teamStats[j] * BuzzData.pointVals[j];
        }
        for (Bonus b : Bonus.questionSet) {
            if (b == null) return;
            for (int score : b.score) {
                if (score == teamId) teamStats[3] += 10;
            }
        }
    }

    static void resetScores() {
        for (Team team : teams) {
            for (String key : team.playerList) {
                team.playerData.put(key, new int[]{0, 0, 0, 0});
            }
            team.calculateStats();
        }

    }
}