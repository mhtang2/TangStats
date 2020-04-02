package com.uni.datamanager;

import com.uni.Team;
import com.uni.question.Bonus;
import com.uni.question.Category;
import com.uni.question.Tossup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CompileStats {
    private Map<String, TeamStat> teams = new HashMap<>();
    private Map<Integer, ArrayList<TossupStat>> tossupMap = new HashMap<>();
    private Map<Integer, ArrayList<BonusStat>> bonusMap = new HashMap<>();
    private static int off2 = ExportRound.off2;
    private static int off3 = ExportRound.off3;
    private static int off4 = ExportRound.off4;
    private static int off_bonus = ExportRound.off_bonus;
    private static int headerRow = ExportRound.headerRow;
    private static int roundSheetHeader = ExportRound.roundSheetHeader;
    private XSSFWorkbook tossupBook = new XSSFWorkbook();
    private Sheet tossupSheet;
    XSSFCellStyle tossupnumberStyle;
    int tossupSheetRowN = 0;
    int correctlyGenerated = 0;
    int expectedGenerated = 0;

    public void compile(File[] files) {
        tossupSheetSetup();
        buildData(files);
        rankTeams();
        conversionData();
        //write out tossup sheet
        for (int i = 0; i < 8; i++) tossupSheet.autoSizeColumn(i);
        writeExport("./exportdata/every_buzz.xlsx", tossupBook);
        JOptionPane.showMessageDialog(null, "Generated " + correctlyGenerated + "/" + expectedGenerated + " files");
    }

    private void conversionData() {
        //Tossups
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Tossups");
        XSSFCellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);
        String headers[] = {"Round#", "Tossup#", "Rooms Heard", "15's", "10's", " -5's", "Dead", "Category", "Subcategory"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
        ArrayList<Integer> rounds = new ArrayList<>(tossupMap.keySet());
        Collections.sort(rounds);
        for (int round : rounds) {
            ArrayList<TossupStat> tossups = tossupMap.get(round);
            /*for (TossupStat ts : tossups) {
                System.out.println(Arrays.toString(ts.stats));
            }*/
            for (int q = 0; q < tossups.size(); q++) {
                int[] dat = tossups.get(q).stats;
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(round);
                row.createCell(1).setCellValue(q + 1);
                row.createCell(2).setCellValue(dat[3]);
                for (int i = 0; i < 3; i++) row.createCell(3 + i).setCellValue(dat[i]);
                row.createCell(6).setCellValue(dat[4]);
                row.createCell(7).setCellValue(tossups.get(q).cat);
                row.createCell(8).setCellValue(tossups.get(q).subcat);
            }
        }
        for (int i = 0; i <= 8; i++) {
            sheet.autoSizeColumn(i);
        }

        sheet = wb.createSheet("Bonuses");
        //Bonuses
        rowNum = 0;
        row = sheet.createRow(rowNum++);
        headers = new String[]{"Round#", "Bonus#", "Teams Heard", "30's", "20's", "10's", "0's", "Category", "Subcategory"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
        rounds = new ArrayList<>(bonusMap.keySet());
        Collections.sort(rounds);
        for (int round : rounds) {
            ArrayList<BonusStat> bonuses = bonusMap.get(round);
            /*for (BonusStat ts : bonuses) {
                System.out.println(Arrays.toString(ts.stats));
            }*/
            for (int q = 0; q < bonuses.size(); q++) {
                int[] dat = bonuses.get(q).stats;
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(round);
                row.createCell(1).setCellValue(q + 1);
                row.createCell(2).setCellValue(dat[4]);
                for (int i = 0; i < 4; i++) row.createCell(3 + i).setCellValue(dat[i]);
                row.createCell(7).setCellValue(bonuses.get(q).cat);
                row.createCell(8).setCellValue(bonuses.get(q).subcat);
            }
        }
        for (int i = 0; i <= 8; i++) {
            sheet.autoSizeColumn(i);
        }
        writeExport("./exportdata/conversion.xlsx", wb);
    }

    private void tossupSheetSetup() {
        //Setup sheet
        tossupSheet = tossupBook.createSheet();
        tossupnumberStyle = tossupBook.createCellStyle();
        tossupnumberStyle.setDataFormat(tossupBook.createDataFormat().getFormat("0.000"));
        Row tossupSheetRow = tossupSheet.createRow(tossupSheetRowN++);
        XSSFCellStyle style = tossupBook.createCellStyle();
        Font font = tossupBook.createFont();
        font.setBold(true);
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        String headers[] = {"Round", "Team", "Tossup#", "Player", "Points", "Category", "Subcategory", "Answer", "cDepth"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = tossupSheetRow.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
    }

    private void rankTeams() {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        XSSFCellStyle numberStyle = wb.createCellStyle();
        numberStyle.setDataFormat(wb.createDataFormat().getFormat("0.000"));
        //list of categories + "Total"
        List<String> catsAndTotal = new ArrayList<>();
        catsAndTotal.add("Total");
        catsAndTotal.addAll(Category.names);

        //Generate list of teams;
        List<TeamStat> teamRanked = new ArrayList<>(teams.values());
        XSSFSheet sheet = wb.createSheet("TeamRank_PPB");
        int column = 0;
        int rownum;
        Row row;
        for (String cat : catsAndTotal) {
            teamRanked.sort((b, a) -> ((int) a.bonusData.get(cat)[2] - (int) b.bonusData.get(cat)[2]));
            rownum = 0;
            row = getRow(sheet, rownum++);
            row.createCell(column).setCellValue(cat);
            row.getCell(column).setCellStyle(style);
            for (TeamStat ts : teamRanked) {
                row = getRow(sheet, rownum++);
                row.createCell(column).setCellValue(ts.formattedName);
                Cell cell = row.createCell(column + 1);
                cell.setCellValue(ts.bonusData.get(cat)[2]);
                cell.setCellStyle(numberStyle);
            }
            column += 3;
        }
        for (int i = 0; i < catsAndTotal.size(); i++) {
            sheet.autoSizeColumn(3 * i);
            sheet.autoSizeColumn(3 * i + 1);
        }

        //Generate list of players
        List<PlayerStat> playerRanked = new ArrayList<>();
        for (TeamStat ts : teamRanked) playerRanked.addAll(ts.players.values());
        sheet = wb.createSheet("PlayerRank_PPTUH");
        column = 0;
        String[] headers = {"Team:Player", "15", "10", "-5", "Total", "Heard", "cDepth", "PPTUH"};
        for (String cat : catsAndTotal) {
            playerRanked.sort((b, a) -> ((int) a.tossupData.get(cat)[5] - (int) b.tossupData.get(cat)[5]));
            rownum = 0;
            row = getRow(sheet, rownum++);
            row.createCell(column).setCellValue(cat);
            row.getCell(column).setCellStyle(style);
            row = getRow(sheet, rownum++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(column + i);
                cell.setCellStyle(style);
                cell.setCellValue(headers[i]);
            }
            for (PlayerStat ps : playerRanked) {
                float[] dat = ps.tossupData.get(cat);
                row = getRow(sheet, rownum++);
                row.createCell(column).setCellValue(ps.teamFormattedName + ": " + ps.formattedName);
                for (int i = 0; i < 5; i++) {
                    row.createCell(column + i + 1).setCellValue(dat[i]);
                }
                //Cdepth
                float correct = dat[0] + dat[1];
                Cell cell = row.createCell(column + 6);
                cell.setCellValue((correct == 0f || dat[6] == 0f) ? 1 : dat[6] / correct);
                cell.setCellStyle(numberStyle);
                //PPTUH
                cell = row.createCell(column + 7);
                cell.setCellValue(dat[5]);
                cell.setCellStyle(numberStyle);
            }
            column += headers.length;
        }
        for (int i = 0; i < catsAndTotal.size() * headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        writeExport("./exportdata/ranking.xlsx", wb);
    }

    private void processRoundSheets(File[] files) {
        Category.loadCategories("/categories");
        for (File file : files) {
            //Process round sheet and make categories
            try {
                FileInputStream excelFile = new FileInputStream(file);
                Workbook wb = new XSSFWorkbook(excelFile);
                Sheet roundSheet = wb.getSheetAt(0);
                int round = (int) roundSheet.getRow(0).getCell(1).getNumericCellValue();
                System.out.println(round + " " + file.getName());
                ArrayList<TossupStat> tossupStats = tossupMap.get(round);
                ArrayList<BonusStat> bonusStats = bonusMap.get(round);
                if (tossupStats == null) {
                    tossupStats = new ArrayList<>();
                    tossupMap.put(round, tossupStats);
                }
                if (bonusStats == null) {
                    bonusStats = new ArrayList<>();
                    bonusMap.put(round, bonusStats);
                }
                Row row;
                int questionN = 0;
                while ((row = roundSheet.getRow(roundSheetHeader + 2 + questionN)) != null && row.getCell(2) != null) {
                    String val = row.getCell(2).toString();
                    try {
                        //Add tossup to list if doesnt exist
                        TossupStat tossup;
                        if (questionN < tossupStats.size()) {
                            tossup = tossupStats.get(questionN);
                        } else {
                            tossup = new TossupStat(row.getCell(0), row.getCell(1));
                            tossupStats.add(tossup);
                        }
                        //Add to categories
                        if (tossup.cat != null && tossup.cat.length() > 0)
                            Category.addCategory(tossup.cat, tossup.subcat);
                        if (!val.equals("UNHEARD")) {
                            int[] dat = Arrays.stream(val.split("/"))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();
                            //DEAD
                            if (dat[0] == 0 && dat[1] == 0) {
                                tossup.stats[4]++;
                            } else {
                                for (int i = 0; i < 3; i++) tossup.stats[i] += dat[i];
                            }
                            //Heard
                            tossup.stats[3]++;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    questionN++;
                }
                questionN = 0;
                while ((row = roundSheet.getRow(roundSheetHeader + 2 + questionN)) != null && row.getCell(off_bonus + 2) != null) {
                    int val = (int) row.getCell(off_bonus + 2).getNumericCellValue();
                    try {
                        //Add bonus to list if doesnt exist
                        BonusStat bonus;
                        if (questionN < bonusStats.size()) {
                            bonus = bonusStats.get(questionN);
                        } else {
                            bonus = new BonusStat(row.getCell(off_bonus), row.getCell(off_bonus + 1));
                            bonusStats.add(bonus);
                        }
                        //Add to categories
                        if (bonus.cat != null && bonus.cat.length() > 0) Category.addCategory(bonus.cat, bonus.subcat);

                        if (val != -1) {
                            //DEAD
                            bonus.stats[3 - val]++;
                            //Heard
                            bonus.stats[4]++;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    questionN++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, file.getName() + " CORRUPTED", "Warning", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println(Category.names);
    }

    private void buildData(File[] files) {
        processRoundSheets(files);
        for (File file : files) {
            try {
                FileInputStream excelFile = new FileInputStream(file);
                Workbook wb = new XSSFWorkbook(excelFile);
                int round = (int) wb.getSheetAt(0).getRow(0).getCell(1).getNumericCellValue();
                Row row;

                //Loop through team sheets
                for (int teamId = 0; teamId < 2; teamId++) {
                    Sheet sheet = wb.getSheetAt(teamId + 1);
                    System.out.println(sheet.getSheetName());
                    //Set up team info
                    String formatTeam = sheet.getRow(0).getCell(1).getStringCellValue();
                    String pureTeam = pure(formatTeam);
                    TeamStat team = teams.get(pureTeam);
                    if (team == null) {
                        team = new TeamStat(formatTeam);
                        teams.put(pureTeam, team);
                    }
                    //Handle players
                    int n = headerRow + 2;
                    while ((row = sheet.getRow(n)) != null && row.getCell(0) != null) {
                        String formatPlayer = row.getCell(0).getStringCellValue();
                        String purePlayer = pure(formatPlayer);
                        PlayerStat player = team.players.get(purePlayer);
                        if (player == null) {
                            player = new PlayerStat(formatPlayer, formatTeam);
                            team.players.put(purePlayer, player);
                        }
                        player.tossupData.get("Total")[4] += row.getCell(5).getNumericCellValue();
                        n++;
                    }
                    //Handle Tossups &&&& write to the sheet tracking tossups
                    Row tossupSheetRow;
                    //Done setting up
                    n = headerRow + 2;
                    while ((row = sheet.getRow(n)) != null && row.getCell(off2) != null) {
                        //Copy to tossup sheet
                        tossupSheetRow = tossupSheet.createRow(tossupSheetRowN++);
                        tossupSheetRow.createCell(0).setCellValue(round);
                        tossupSheetRow.createCell(1).setCellValue(team.formattedName);
                        for (int i = 0; i <= 5; i++) {
                            String nil = null;
                            Cell source = row.getCell(off2 + i);
                            if (source == null) {
                                tossupSheetRow.createCell(i + 2).setCellValue(nil);
                            } else if (source.getCellTypeEnum() == CellType.NUMERIC) {
                                tossupSheetRow.createCell(i + 2).setCellValue(source.getNumericCellValue());
                            } else {
                                tossupSheetRow.createCell(i + 2).setCellValue(source.toString());
                            }
                        }
                        Cell cdepthcell = tossupSheetRow.createCell(8);
                        cdepthcell.setCellStyle(tossupnumberStyle);
                        cdepthcell.setCellValue(row.getCell(off2 + 6).getNumericCellValue());
                        //Handle tossups
                        PlayerStat player = team.players.get(pure(row.getCell(off2 + 1).getStringCellValue()));
                        if (player == null) continue;
                        int points = (int) row.getCell(off2 + 2).getNumericCellValue();
                        float cdepth = (float) row.getCell(off2 + 6).getNumericCellValue();
                        String cat;
                        boolean noCat = true;
                        if (row.getCell(off2 + 3) != null && (cat = row.getCell(off2 + 3).getStringCellValue()).length() > 0) {
                            player.incPoints(cat, points, cdepth, true);
                            noCat = false;
                        }
                        if (row.getCell(off2 + 4) != null && (cat = row.getCell(off2 + 4).getStringCellValue()).length() > 0) {
                            player.incPoints(cat, points, cdepth, false);
                            noCat = false;
                        }
                        if (noCat) {
                            player.incTotal(points, cdepth);
                        }
                        n++;
                    }
                    //Handle Bonus
                    n = headerRow + 2;
                    while ((row = sheet.getRow(n)) != null && row.getCell(off3) != null) {
                        int points = (int) row.getCell(off3 + 1).getNumericCellValue();
                        String cat;
                        boolean noCat = true;
                        if (row.getCell(off3 + 2) != null && (cat = row.getCell(off3 + 2).getStringCellValue()).length() > 0) {
                            team.bonusData.get(cat)[0] += points;
                            team.bonusData.get(cat)[1]++;
                            team.bonusData.get("Total")[0] += points;
                            team.bonusData.get("Total")[1]++;
                            noCat = false;
                        }
                        if (row.getCell(off3 + 3) != null && (cat = row.getCell(off3 + 3).getStringCellValue()).length() > 0) {
                            team.bonusData.get(cat)[0] += points;
                            team.bonusData.get(cat)[1]++;
                            noCat = false;
                        }
                        if (noCat) {
                            team.bonusData.get("Total")[0] += points;
                            team.bonusData.get("Total")[1]++;
                        }
                        n++;
                    }
                    //Handle heard
                    n = headerRow + 3;
                    while ((row = sheet.getRow(n)) != null && row.getCell(off4) != null) {
                        String name = pure(row.getCell(off4).getStringCellValue());
                        //TODO: fix bullshit
                        for (int i = 0; i < Category.names.size(); i++) {
                            int num = (int) row.getCell(off4 + i + 1).getNumericCellValue();
                            PlayerStat player = team.players.get(name);
                            player.tossupData.get(Category.names.get(i))[4] += num;
                        }
                        n++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, file.getName() + " CORRUPTED", "Warning", JOptionPane.ERROR_MESSAGE);
            }

        }
        //Calculate PPB PPTUH
        for (TeamStat team : teams.values()) {
            for (float[] arr : team.bonusData.values()) {
                arr[2] = arr[1] == 0 ? 0 : arr[0] / arr[1];
            }
            for (PlayerStat player : team.players.values()) {
                for (float[] arr : player.tossupData.values()) {
                    arr[5] = arr[4] == 0 ? 0 : arr[3] / arr[4];
                }
            }
        }
        teams.values().forEach(CompileStats::printTeam);
    }


    private static void printTeam(TeamStat team) {
        System.out.println(team.formattedName);
        team.bonusData.forEach((k, v) -> {
            if (v[0] != 0)
                System.out.print(k + "=" + Arrays.toString(v) + " ");
        });
        System.out.println();
        for (PlayerStat player : team.players.values()) {
            System.out.print(player.formattedName + " ");
            player.tossupData.forEach((k, v) -> {
                if (v[4] != 0)
                    System.out.print(k + "=" + Arrays.toString(v) + " ");
            });
            System.out.println();
        }
    }

    private void writeExport(String path, XSSFWorkbook book) {
        expectedGenerated++;
        try {
            File out = new File(path);
            if (!out.getParentFile().exists() && !out.getParentFile().mkdirs()) {
                throw new Exception();
            }
            book.write(new FileOutputStream(out));
            correctlyGenerated++;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't save to " + path + "\nFile open or invalid?", "Warning", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String pure(String name) {
        return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.US);
    }

    private static Row getRow(XSSFSheet sheet, int row) {
        if (sheet.getRow(row) == null) return sheet.createRow(row);
        return sheet.getRow(row);
    }
}
