package com.uni.datamanager;

import org.apache.poi.ss.usermodel.Cell;

public class BonusStat {
    String cat;
    String subcat;

    /**
     * int[30,20,10,0,heard]
     **/
    int[] stats = new int[5];

    BonusStat(Cell catcell, Cell subcatcell) {
        this.cat = catcell == null ? null : catcell.getStringCellValue();
        this.subcat = subcatcell == null ? null : subcatcell.getStringCellValue();
    }
}
