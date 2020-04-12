package com.uni.datamanager;

import com.uni.Main;
import com.uni.Team;
import com.uni.marker.BuzzData;
import com.uni.marker.QuestionWord;
import com.uni.question.Bonus;
import com.uni.question.Category;
import com.uni.question.Tossup;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportRound {
    static int off_bonus = 3;
    static int off2 = 7;
    static int off3 = 15;
    static int off4 = 20;
    static int headerRow = 2;
    static int roundSheetHeader = 9;

    //Save data
    public static void saveRoundData(JFileChooser filechoose) {
        if (Main.window.roundNumber < 1) {
            JOptionPane.showMessageDialog(null, "Select a valid round number");
            return;
        }
        ArrayList<Integer> missingTossups = new ArrayList<>();
        ArrayList<Integer> missingBonuses = new ArrayList<>();
        for (int i = 0; i < Tossup.questionSet.length; i++) {
            if (Tossup.questionSet[i].category == null) {
                missingTossups.add(i + 1);
            }
        }
        for (int i = 0; i < Bonus.questionSet.length; i++) {
            if (Bonus.questionSet[i].category == null) {
                missingBonuses.add(i + 1);
            }
        }
        if (!(missingBonuses.isEmpty() && missingTossups.isEmpty())) {
            int res = JOptionPane.showConfirmDialog(null, "Missing categories for\nTossups: " + missingTossups + "\nMissing bonuses: " + missingBonuses + "\nContinue?", "Warning", JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.OK_OPTION) {
                System.out.println(res);
                return;
            }
        }
        filechoose.setFileFilter(new FileNameExtensionFilter(".xlsx", "Excel"));
        filechoose.setCurrentDirectory(new File("/home/me/Documents"));
        filechoose.setSelectedFile(new File("round" + Main.window.roundNumber + "_" + Team.teams[0].name.replaceAll("[^a-zA-Z\\d]","") + "_" + Team.teams[1].name.replaceAll("[^a-zA-Z\\d]","") + ".xlsx"));
        int r = filechoose.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                String path = filechoose.getSelectedFile() + "";
                if (!path.endsWith(".xlsx")) path += ".xlsx";
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet roundSheet = workbook.createSheet("Round Overview");
                XSSFSheet teamSheet1 = workbook.createSheet("TeamOverview1");
                XSSFSheet teamSheet2 = workbook.createSheet("TeamOverview2");
                XSSFCellStyle numberStyle = workbook.createCellStyle();
                numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.000"));
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
                font.setFontHeightInPoints((short) 12);
                style.setFont(font);
                //Write round sheet
                int roundrow = 0;
                Row row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Round");
                row.createCell(1).setCellValue(Main.window.roundNumber);
                //Calculate TUH TED
                int tuh = 0;
                int ted = 0;
                for (Tossup t : Tossup.questionSet) {
                    if (!t.getActive(0).isEmpty() && !t.getActive(1).isEmpty()) {
                        tuh++;
                        if (t.dead) ted++;
                    }
                }
                //TUH
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Tossups Heard");
                row.createCell(1).setCellValue(tuh);
                //TED
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Tossups dead");
                row.createCell(1).setCellValue(ted);
                //Teams
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Teams");
                row.createCell(1).setCellValue(Team.teams[0].name);
                row.createCell(2).setCellValue(Team.teams[1].name);
                //Final score
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Final score");
                row.createCell(1).setCellValue(Team.teams[0].teamStats[3]);
                row.createCell(2).setCellValue(Team.teams[1].teamStats[3]);
                //Bonuses heard and bonus points
                int[] bonusT1 = bonusStats(0);
                int[] bonusT2 = bonusStats(1);
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Bonuses Heard");
                row.createCell(1).setCellValue(bonusT1[0]);
                row.createCell(2).setCellValue(bonusT2[0]);
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("Points from bonuses");
                row.createCell(1).setCellValue(bonusT1[1]);
                row.createCell(2).setCellValue(bonusT2[1]);
                row = roundSheet.createRow(roundrow++);
                row.createCell(0).setCellValue("PPB");
                row.createCell(1).setCellValue(bonusT1[0] == 0 ? 0 : bonusT1[1] / (double) bonusT1[0]);
                row.createCell(2).setCellValue(bonusT2[0] == 0 ? 0 : bonusT2[1] / (double) bonusT2[0]);
                //Misc Cat data

                roundrow = roundSheetHeader;
                row = getRow(roundSheet, roundrow++);
                row.createCell(0).setCellValue("Misc. data for processing");
                row = getRow(roundSheet, roundrow++);
                row.createCell(0).setCellValue("Tossups");
                for (Tossup q : Tossup.questionSet) {
                    row = getRow(roundSheet, roundrow++);
                    int[] dat = new int[3];
                    for (QuestionWord qword : q.words) {
                        if (qword.buzzData.point > -1) {
                            dat[qword.buzzData.point]++;
                        }
                    }
                    String out = dat[0] + "/" + dat[1] + "/" + dat[2];
                    if (q.getActive(0).isEmpty() && q.getActive(1).isEmpty()) out = "UNHEARD";
                    row.createCell(0).setCellValue(q.category == null ? null : q.category.toString());
                    row.createCell(1).setCellValue(q.subcategory);
                    row.createCell(2).setCellValue(out);
                }

                roundrow = roundSheetHeader + 1;
                row = getRow(roundSheet, roundrow++);
                row.createCell(off_bonus).setCellValue("Bonuses");
                for (Bonus q : Bonus.questionSet) {
                    row = getRow(roundSheet, roundrow++);
                    row.createCell(off_bonus).setCellValue(q.category == null ? null : q.category.toString());
                    row.createCell(off_bonus + 1).setCellValue(q.subcategory);
                    int right = 0;
                    for (int i : q.score) {
                        if (i > -1) right++;
                    }
                    if (q.controllingTeam == -1) right = -1;
                    row.createCell(off_bonus + 2).setCellValue(right);
                }

                writeTeamSheet(teamSheet1, Team.teams[0], style, numberStyle);
                writeTeamSheet(teamSheet2, Team.teams[1], style, numberStyle);
                for (int i = 0; i < 1; i++) {
                    roundSheet.autoSizeColumn(i);
                }
                workbook.write(new FileOutputStream(new File(path)));
                JOptionPane.showMessageDialog(null, "Successfully saved to " + path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Invalid file or file locked");
                ex.printStackTrace();
            }
        }
    }

    private static void writeTeamSheet(XSSFSheet sheet, Team team, CellStyle boldstyle, CellStyle numberStyle) {
        int rownum = 0;
        Row row = getRow(sheet, rownum++);
        row.createCell(0).setCellValue("Team");
        row.createCell(1).setCellValue(team.name);
        rownum++;
        //Player data
        getRow(sheet, rownum++).createCell(0).setCellValue("Player data");
        sheet.getRow(rownum - 1).getCell(0).setCellStyle(boldstyle);
        String[] headers = new String[]{"Player", "Powers", "10's", "Negs", "Total Points", "TUH"};
        row = getRow(sheet, rownum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(boldstyle);
        }
        getPlayerTUH(team);
        for (String player : team.playerList) {
            row = getRow(sheet, rownum++);
            row.createCell(0).setCellValue(player);
            for (int i = 0; i < 5; i++) {
                row.createCell(i + 1).setCellValue(team.playerData.get(player)[i]);
            }
        }

        //Tossup data
        rownum = headerRow;

        getRow(sheet, rownum++).createCell(off2).setCellValue("Tossup data");
        getRow(sheet, rownum - 1).getCell(off2).setCellStyle(boldstyle);
        headers = new String[]{"Tossup #", "Player", "Points Earned", "Category", "Subcategory", "Answer", "Cdepth"};
        row = getRow(sheet, rownum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i + off2).setCellValue(headers[i]);
            row.getCell(i + off2).setCellStyle(boldstyle);
        }
        for (Tossup tossup : Tossup.questionSet) {
            for (QuestionWord qword : tossup.words) {
                BuzzData data = qword.buzzData;
                if (data.point != -1 && data.teamId == team.teamId) {
                    row = getRow(sheet, rownum++);
                    row.createCell(off2).setCellValue(tossup.id);
                    row.createCell(off2 + 1).setCellValue(data.name);
                    row.createCell(off2 + 2).setCellValue(BuzzData.pointVals[data.point]);
                    row.createCell(off2 + 3).setCellValue(tossup.category == null ? "" : tossup.category.toString());
                    row.createCell(off2 + 4).setCellValue(tossup.subcategory);
                    row.createCell(off2 + 5).setCellValue(tossup.answer.split("[\\[\\(]", 2)[0].replaceAll("(<b>|</b>)","").replace("ANSWER: ", "").replaceAll("[^\\x00-\\x7F]", "").trim());
                    Cell cdepthcell = row.createCell(off2 + 6);
                    cdepthcell.setCellValue((double) qword.wordID / tossup.size);
                    cdepthcell.setCellStyle(numberStyle);
                }
            }
        }
        //Bonus data
        rownum = headerRow;
        row = getRow(sheet, rownum++);
        row.createCell(off3).setCellValue("Bonus data");
        row.getCell(off3).setCellStyle(boldstyle);
        headers = new String[]{"Bonus #", "Points earned", "Category", "Subcategory"};
        row = getRow(sheet, rownum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(off3 + i).setCellValue(headers[i]);
            row.getCell(off3 + i).setCellStyle(boldstyle);
        }
        for (Bonus bonus : Bonus.questionSet) {
            if (bonus.controllingTeam == team.teamId) {
                row = getRow(sheet, rownum++);
                row.createCell(off3).setCellValue(bonus.id);
                int points = 0;
                for (int score : bonus.score) {
                    if (score == team.teamId) points += 10;
                }
                row.createCell(off3 + 1).setCellValue(points);
                row.createCell(off3 + 2).setCellValue(bonus.category == null ? "" : bonus.category.toString());
                row.createCell(off3 + 3).setCellValue(bonus.subcategory);
            }
        }
        //Heard data
        rownum = headerRow;
        row = getRow(sheet, rownum++);
        row.createCell(off4).setCellValue("Heard");
        row.getCell(off4).setCellStyle(boldstyle);
        row = getRow(sheet, rownum++);
        row.createCell(off4).setCellValue("NAME");
        row.getCell(off4).setCellStyle(boldstyle);
        //Generate headers and init category map
        HashMap<String, Counter> catHeard = new HashMap<>();
        int offC = 1;
        for (String cat : Category.names) {
            catHeard.put(cat, new Counter(0));
            row.createCell(off4 + offC).setCellValue(cat);
            row.getCell(off4 + offC).setCellStyle(boldstyle);
            offC++;
        }
        //Handle bonuses
        row = getRow(sheet, rownum++);
        row.createCell(off4).setCellValue("Bonuses");
        for (Bonus q : Bonus.questionSet) {
            if (q.controllingTeam == team.teamId) {
                if (q.category != null) {
                    catHeard.get(q.category.toString()).inc();
                }
                if (q.subcategory != null) {
                    catHeard.get(q.subcategory).inc();
                }
            }
        }
        offC = 1;
        for (String cat : Category.names) {
            row.createCell(off4 + offC++).setCellValue(catHeard.get(cat).x);
        }
        //Handle players
        for (String player : team.playerList) {
            for (String cat : Category.names) {
                catHeard.get(cat).reset();
            }
            for (Tossup q : Tossup.questionSet) {
                if (q.getActive(team.teamId).contains(player)) {
                    if (q.category != null) {
                        catHeard.get(q.category.toString()).inc();
                    }
                    if (q.subcategory != null) {
                        catHeard.get(q.subcategory).inc();
                    }
                }
            }
            row = getRow(sheet, rownum++);
            row.createCell(off4).setCellValue(player);
            offC = 1;
            for (String cat : Category.names) {
                row.createCell(off4 + offC++).setCellValue(catHeard.get(cat).x);
            }
        }

        for (int i = 0; i < 52; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static Row getRow(XSSFSheet sheet, int row) {
        if (sheet.getRow(row) == null) return sheet.createRow(row);
        return sheet.getRow(row);
    }

    /**
     * player-> <catgory,count></>
     **/
    private static void getPlayerTUH(Team team) {
        for (String player : team.playerList) {
            team.playerData.get(player)[4] = 0;
        }
        for (Tossup t : Tossup.questionSet) {
            for (String player : t.getActive(team.teamId)) {
                team.playerData.get(player)[4]++;
            }
        }
    }

    /**
     * int[0] is bonuses heard
     * int[1] is points from bonuses heard
     **/
    private static int[] bonusStats(int teamId) {
        int[] ret = new int[2];
        for (Bonus b : Bonus.questionSet) {
            if (b.controllingTeam == teamId) {
                ret[0]++;
                for (int s : b.score) {
                    if (s == teamId) ret[1] += 10;
                }
            }
        }
        return ret;
    }
}
