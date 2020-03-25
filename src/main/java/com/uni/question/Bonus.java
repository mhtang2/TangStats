package com.uni.question;

public class Bonus {
    public static Bonus[] questionSet;
    public static int setidx = 0;
    public String leadin;
    public String[] q = new String[3];
    public String[] a = new String[3];
    public int[] score = new int[]{-1, -1, -1};
    public Category category = null;
    public String subcategory = null;
    public int controllingTeam = -1;
    public int id = 0;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(leadin);
        for (int i = 0; i < 3; i++) {
            s.append(q[i]);
            s.append(a[i]);
        }
        return s.toString();
    }
}
