package com.uni.question;

public class Bonus {
    public String leadin;
    public String[] q = new String[3];
    public String[] a = new String[3];
    public static Bonus[] questionSet;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(leadin);
        for (int i = 0; i < 3; i++) {
            s.append(q[i] + a[i]);
        }
        return s.toString();
    }
}
