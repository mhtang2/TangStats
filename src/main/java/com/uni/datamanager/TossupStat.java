package com.uni.datamanager;

import org.apache.poi.ss.usermodel.Cell;

public class TossupStat {
    String cat;
    String subcat;

    /**
     * int[powers,tossups,negs,heard,dead]
     **/
    int[] stats = new int[5];

    TossupStat(Cell catcell, Cell subcatcell) {
        this.cat = catcell == null ? null : catcell.getStringCellValue();
        this.subcat = subcatcell == null ? null : subcatcell.getStringCellValue();
    }
}
