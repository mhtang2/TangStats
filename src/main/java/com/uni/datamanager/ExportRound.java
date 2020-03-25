package com.uni.datamanager;

import com.uni.Main;
import com.uni.Team;
import com.uni.marker.BuzzData;
import com.uni.marker.QuestionWord;
import com.uni.question.Bonus;
import com.uni.question.Tossup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;

public class ExportRound {
    //Save data
    public static void saveRoundData(JFileChooser filechoose) {
        filechoose.setFileFilter(new FileNameExtensionFilter(".xlsx", "Excel"));
        filechoose.setCurrentDirectory(new File("/home/me/Documents"));
        int r = filechoose.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                String path = filechoose.getSelectedFile() + "";
                if (!path.endsWith(".xlsx")) path += ".xlsx";
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet roundSheet = workbook.createSheet("Round Overview");
                XSSFSheet teamSheet1 = workbook.createSheet("TeamOverview1");
                XSSFSheet teamSheet2 = workbook.createSheet("TeamOverview2");

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
                row.createCell(1).setCellValue(bonusT1[1] / (double) bonusT1[0]);
                row.createCell(2).setCellValue(bonusT2[1] / (double) bonusT2[0]);
                writeTeamSheet(teamSheet1, Team.teams[0], style);
                writeTeamSheet(teamSheet2, Team.teams[1], style);
                for (int i = 0; i < 10; i++) {
                    roundSheet.autoSizeColumn(i);
                }
                workbook.write(new FileOutputStream(new File(path)));
                JOptionPane.showMessageDialog(null, "Successfully saved to " + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid file or file locked");
                ex.printStackTrace();
            }
        }
    }

    private static void writeTeamSheet(XSSFSheet sheet, Team team, CellStyle boldstyle) {
        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        row.createCell(0).setCellValue("Team");
        row.createCell(1).setCellValue(team.name);
        rownum++;
        //Player data
        sheet.createRow(rownum++).createCell(0).setCellValue("Player data");
        sheet.getRow(rownum - 1).getCell(0).setCellStyle(boldstyle);
        String[] headers = new String[]{"Player", "Powers", "10's", "Negs", "Total Points", "TUH"};
        row = sheet.createRow(rownum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(boldstyle);
        }
        getPlayerTUH(team);
        for (String player : team.playerList) {
            row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(player);
            for (int i = 0; i < 5; i++) {
                row.createCell(i + 1).setCellValue(team.playerData.get(player)[i]);
            }
        }
        rownum++;
        int questionRow = rownum;
        //Tossup data
        sheet.createRow(rownum++).createCell(0).setCellValue("Tossup data");
        sheet.getRow(rownum - 1).getCell(0).setCellStyle(boldstyle);
        headers = new String[]{"Tossup #", "Player", "Points Earned", "Category", "Subcategory", "Cdepth"};
        row = sheet.createRow(rownum++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(boldstyle);
        }
        for (Tossup tossup : Tossup.questionSet) {
            for (QuestionWord qword : tossup.words) {
                BuzzData data = qword.buzzData;
                if (data.point != -1 && data.teamId == team.teamId) {
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(tossup.id);
                    row.createCell(1).setCellValue(data.name);
                    row.createCell(2).setCellValue(BuzzData.pointVals[data.point]);
                    row.createCell(3).setCellValue(tossup.category == null ? "" : tossup.category.toString());
                    row.createCell(4).setCellValue(tossup.subcategory);
                    row.createCell(5).setCellValue(qword.wordID + "/" + tossup.size);
                }
            }
        }
        //Bonus data
        row = sheet.getRow(questionRow++);
        row.createCell(7).setCellValue("Bonus data");
        sheet.getRow(questionRow - 1).getCell(7).setCellStyle(boldstyle);
        headers = new String[]{"Bonus #", "Points earned", "Category", "Subcategory"};
        row = sheet.getRow(questionRow++);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(7 + i).setCellValue(headers[i]);
            row.getCell(7 + i).setCellStyle(boldstyle);
        }
        for (Bonus bonus : Bonus.questionSet) {
            if (bonus.controllingTeam == team.teamId) {
                row = sheet.getRow(questionRow++);
                row.createCell(7).setCellValue(bonus.id);
                int points = 0;
                for (int score : bonus.score) {
                    if (score == team.teamId) points += 10;
                }
                row.createCell(8).setCellValue(points);
                row.createCell(9).setCellValue(bonus.category == null ? "" : bonus.category.toString());
                row.createCell(10).setCellValue(bonus.subcategory);
            }
        }
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
    }

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
