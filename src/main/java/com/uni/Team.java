package com.uni;

import com.uni.marker.BuzzData;
import com.uni.question.Tossup;

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
    public ArrayList<String> activePlayers = new ArrayList<>();
    public Map<String, int[]> playerData = new HashMap<>();

    //Stuff for adding things in player manager
    JPanel canvas = new JPanel();
    JTextField nameField = new JTextField(20);
    public String name;
    int[] teamStats = new int[]{0, 0, 0, 0};
    //0 or 1 value
    public int teamId;

    Team(String name, int teamId) {
        this.name = name;
        this.teamId = teamId;
        //Container for team 1 players
        canvas.setLayout(new GridLayout(0, 1));
        JPanel inputContainer = new JPanel();
        inputContainer.add(new JLabel("Add Player: "));
        inputContainer.add(nameField);

        JPanel teamNameContainer = new JPanel();
        JLabel teamNameLabel = new JLabel(name);
        teamNameLabel.setForeground(teamColors[teamId]);
        JButton changeName = new JButton("Set Team Name");

        teamNameContainer.add(teamNameLabel);
        teamNameContainer.add(changeName);
        nameField.addActionListener(actionEvent -> addPlayer());
        canvas.add(teamNameContainer);
        canvas.add(inputContainer);

        changeName.addActionListener((actionEvent) -> {
            String newName = JOptionPane.showInputDialog("New team name: ");
            if (newName != null && newName.length() > 0) {
                this.name = newName;
                teamNameLabel.setText(newName);
                Main.window.playermanager.updateGraphics();
            }
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
        activePlayers.add(playerName);
        if (Tossup.current() != null) Tossup.current().updateActive(teamId);
        playerData.put(playerName, new int[]{0, 0, 0, 0});

        JPanel playerContainer = new JPanel();
        JLabel removeButton = new JLabel();
        removeButton.setIcon(PlayerManager.xIcon);
        JToggleButton togglePlayer = new JToggleButton("Active", false);

        playerContainer.add(new JLabel(playerName));
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
                activePlayers.remove(playerName);
                canvas.remove(playerContainer);
                if (Tossup.current() != null) Tossup.current().updateActive(teamId);
                Main.window.playermanager.updateGraphics();
            }
        });
        togglePlayer.addItemListener((itemEvent) -> {
            if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                if (!activePlayers.contains(playerName)) {
                    activePlayers.add(playerName);
                }
                togglePlayer.setText("Active");
            } else {
                activePlayers.remove(playerName);
                togglePlayer.setText("Inactive");
            }
            if (Tossup.current() != null) Tossup.current().updateActive(teamId);
            Main.window.playermanager.updateGraphics();
        });
    }

    void calculateStats() {
        teamStats = new int[]{0, 0, 0, 0};
        for (String key : playerList) {
            int[] playerstats = playerData.get(key);
            for (int i = 0; i < 3; i++) {
                teamStats[i] += playerstats[i];
            }
            //Calculate total
            teamStats[3] = 0;
            for (int j = 0; j < 3; j++) {
                teamStats[3] += teamStats[j] * BuzzData.pointVals[j];
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