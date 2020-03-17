package com.uni;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Team {

    static Team[] teams = new Team[2];
    ArrayList<String> playerList = new ArrayList<>();
    Map<String, int[]> playerData = new HashMap<>();

    //Stuff for adding things in player manager
    JPanel canvas = new JPanel();
    JTextField nameField = new JTextField(20);
    String name;

    public Team(String name) {
        this.name = name;
        //Container for team 1 players
        canvas.setLayout(new GridLayout(0, 1));
        JPanel inputContainer = new JPanel();
        inputContainer.add(new JLabel("Add Player: "));
        inputContainer.add(nameField);

        nameField.addActionListener(actionEvent -> addPlayer());
        canvas.add(inputContainer);

    }

    void addPlayer() {
        String playerName = nameField.getText();
        nameField.setText("");
        playerList.add(playerName);
        playerData.put(playerName, new int[]{0, 0, 0, 0});

        JPanel playerContainer = new JPanel();
        JLabel removeButton = new JLabel();
        removeButton.setIcon(PlayerManager.xIcon);

        playerContainer.add(new JLabel(playerName));
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
                canvas.remove(playerContainer);
                Main.window.playermanager.updateGraphics();
            }
        });
    }

    static void resetScores() {
        for (Team team : teams) {
            for (String key : team.playerList) {
                team.playerData.put(key, new int[]{0, 0, 0, 0});
            }
        }
    }
}