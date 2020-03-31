package com.uni.marker;

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorMessage("Can't find config.ini");
            return;
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            for (int i = 0; i < 3; i++) {
                int val = Integer.parseInt(br.readLine().split("=", 2)[1].trim());
                pointVals[i] = val;
                pointMap.put(val, i);
            }
            String bar = br.readLine().split("=", 2)[1];
            if (bar.length() < 1) {
                System.out.println("HI");
                Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                while (n.hasMoreElements()) {
                    StringBuilder sb = new StringBuilder();
                    NetworkInterface nif = n.nextElement();
                    byte[] mac = nif.getHardwareAddress();
                        System.out.println(nif.getName());
                    if (nif.getHardwareAddress() != null) {
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X", mac[i]));
                        }
                        System.out.println(sb);
                    }
                }
            }
        } catch (IOException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            errorMessage("Bad formatting for config.ini");
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

    private static void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
