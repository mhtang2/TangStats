package com.uni.marker;

public class BuzzData {
    //Array of point values
    public static final int[] pointVals = new int[]{15, 10, -5};
    public int point;
    public String name;
    public int teamId;

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
