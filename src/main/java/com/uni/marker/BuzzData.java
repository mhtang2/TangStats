package com.uni.marker;

import com.uni.Main;
import com.uni.Team;
import com.uni.Window;

import javax.swing.*;
import java.io.*;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;

public class BuzzData {
    //Array of point values
    public static final int[] pointVals = new int[]{15, 10, -5};
    public static final HashMap<Integer, Integer> pointMap = new HashMap<Integer, Integer>() {{
        put(15, 0);
        put(10, 1);
        put(-5, 2);
    }};
    public int point;
    public String name;
    public int teamId;

    public static void readConfig(String path) {
        FileReader fr;
        try {
            fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            try {
                for (int i = 0; i < 3; i++) {
                    int val = Integer.parseInt(br.readLine().split("=", 2)[1].trim());
                    pointVals[i] = val;
                    pointMap.put(val, i);
                }
                br.close();
            } catch (IOException | NullPointerException | NumberFormatException e) {
                e.printStackTrace();
                Main.errorMessage("Bad formatting for config.ini");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.errorMessage("Can't find config.ini, using defaults of +15 +10 -5");
            return;
        }
    }

    BuzzData(int point, String name, int teamId) {
        this.point = point;
        this.name = name;
        this.teamId = teamId;
    }

    boolean sameData(BuzzData other) {
        return name != null && name.equals(other.name) && other.teamId == teamId && point == other.point;
    }

    boolean samePerson(BuzzData other) {
        return name != null && name.equals(other.name) && other.teamId == teamId;
    }

    boolean samePerson(String otherName, int otherTeamId) {
        return name != null && name.equals(otherName) && teamId == otherTeamId;
    }
}
