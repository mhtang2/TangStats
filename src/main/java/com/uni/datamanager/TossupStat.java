package com.uni.datamanager;

public class TossupStat {
    String cat;
    String subcat;

    /**
     * int[powers,tossups,negs,heard,dead]
     **/
    int[] stats = new int[3];

    TossupStat(String cat, String subcat) {
        this.cat = cat;
        this.subcat = subcat;
    }
}
