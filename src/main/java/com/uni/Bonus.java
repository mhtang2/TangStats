package com.uni;

public class Bonus {
    String leadin;
    String[] q = new String[3];
    String[] a = new String[3];
    static Bonus[] questionSet;

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
