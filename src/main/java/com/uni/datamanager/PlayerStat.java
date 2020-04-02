package com.uni.datamanager;

import com.uni.marker.BuzzData;
import com.uni.question.Category;

import java.util.HashMap;
import java.util.Map;

public class PlayerStat {
    String formattedName;
    String teamFormattedName;
    /**
     * Category -> int[15,10,-5,points,heard,PPTUH,cdepthTOTAL]
     **/
    Map<String, float[]> tossupData = new HashMap<>();

    public PlayerStat(String formattedname, String teamFormattedName) {
        this.formattedName = formattedname;
        this.teamFormattedName = teamFormattedName;
        for (String cat : Category.names) {
            tossupData.put(cat, new float[7]);
        }
        tossupData.put("Total", new float[7]);
    }

    void incPoints(String cat, int points, float cdepth, boolean majorCat) {
        int pointIDX = BuzzData.pointMap.get(points);
        tossupData.get(cat)[pointIDX]++;
        tossupData.get(cat)[3] += points;
        if (points > 0) {
            tossupData.get(cat)[6] += cdepth;
        }
        if (majorCat) {
            tossupData.get("Total")[pointIDX]++;
            tossupData.get("Total")[3] += points;
            tossupData.get("Total")[6] += cdepth;
        }
    }

    public void incTotal(int points, float cdepth) {
        int pointIDX = BuzzData.pointMap.get(points);
        tossupData.get("Total")[pointIDX]++;
        tossupData.get("Total")[3] += points;
        if (points > 0) {
            tossupData.get("Total")[6] += cdepth;
        }
    }
}
