package com.uni.datamanager;

import com.sun.media.sound.SoftTuning;
import com.sun.net.httpserver.Headers;
import com.uni.Team;
import com.uni.marker.BuzzData;
import com.uni.question.Category;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CompileStats {
    private Map<String, TeamStat> teams = new HashMap<>();
    private static int off2 = ExportRound.off2;
    private static int off3 = ExportRound.off3;
    private static int off4 = ExportRound.off4;
    private static int headerRow = ExportRound.headerRow;
    Workbook tossupBook = new XSSFWorkbook();
    Sheet tossupSheet;
    int tossupSheetRowN = 0;

    public void compile(File[] files, String savePath) {
        //Setup sheet
        tossupSheet = tossupBook.createSheet();
        Row tossupSheetRow = tossupSheet.createRow(tossupSheetRowN);
        CellStyle style = tossupBook.createCellStyle();
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
        buildData(files);
        rankTeams();
        try {
            File out = new File("./exportdata/every_buzz.xlsx");
            out.getParentFile().mkdirs();
            tossupBook.write(new FileOutputStream(out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rankTeams() {
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        //list of categories + "Total"
        List<String> catsAndTotal = new ArrayList<>();
        catsAndTotal.add("Total");
        catsAndTotal.addAll(Arrays.asList(Category.names));

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
                row.createCell(column + 1).setCellValue(ts.bonusData.get(cat)[2]);
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
                row.createCell(column + 6).setCellValue((dat[4] == 0 || dat[6] == 0) ? 1 : dat[6] / dat[4]);
                //PPTUH
                row.createCell(column + 7).setCellValue(dat[5]);
            }
            column += headers.length;
        }
        for (int i = 0; i < catsAndTotal.size() * headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            File out = new File("./exportdata/ranking.xlsx");
            out.getParentFile().mkdirs();
            wb.write(new FileOutputStream(out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildData(File[] files) {
        for (File file : files) {
            try {
                FileInputStream excelFile = new FileInputStream(file);
                Workbook wb = new XSSFWorkbook(excelFile);
                int round = (int) wb.getSheetAt(0).getRow(0).getCell(1).getNumericCellValue();
                System.out.println(round + " " + file.getName());
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
                    Row row;
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
                        for (int i = 0; i <= 6; i++) {
                            tossupSheetRow.createCell(i + 2).setCellValue(row.getCell(off2 + i) == null ? null : row.getCell(off2 + i).toString());
                        }
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
                        for (int i = 0; i < Category.names.length; i++) {
                            int num = (int) row.getCell(off4 + i + 1).getNumericCellValue();
                            PlayerStat player = team.players.get(name);
                            player.tossupData.get(Category.names[i])[4] += num;
                            if (Category.majorCats.contains(i)) {
                                player.tossupData.get("Total")[4] += num;
                            }
                        }
                        n++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    static void printTeam(TeamStat team) {
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

    static String pure(String name) {
        return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.US);
    }

    private static Row getRow(XSSFSheet sheet, int row) {
        if (sheet.getRow(row) == null) return sheet.createRow(row);
        return sheet.getRow(row);
    }
}
