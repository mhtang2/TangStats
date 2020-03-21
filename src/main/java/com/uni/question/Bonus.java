package com.uni.question;

public class Bonus {
    public static Bonus[] questionSet;
    public static int setIdx = -1;
    public String leadin;
    public String[] q = new String[3];
    public String[] a = new String[3];
    public Category category = null;
    public String subcategory = null;

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
