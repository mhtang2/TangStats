package com.uni.datamanager;

import com.uni.question.Category;

import java.util.HashMap;
import java.util.Map;

public class TeamStat {
    String formattedName;
    /**
     * Category-> int[points, heard,ppb]
     **/
    Map<String, float[]> bonusData = new HashMap<>();
    /**
     * playername -> PlayerStat
     **/
    Map<String, PlayerStat> players = new HashMap<>();

    public TeamStat(String formattedname) {
        this.formattedName = formattedname;
        for(String cat:Category.names){
                bonusData.put(cat, new float[3]);
        }
        bonusData.put("Total", new float[3]);

    }

}
