package com.uni;

import javax.swing.*;

public class ExportData {
    //Save data
    public static void saveData(JFileChooser filechoose) {
//        filechoose.setFileFilter(new FileNameExtensionFilter(".xlsx", "Excel"));
//        filechoose.setCurrentDirectory(new File("/home/me/Documents"));
//        int r = filechoose.showSaveDialog(null);
//        if (r == JFileChooser.APPROVE_OPTION) {
//            try {
//                String path = filechoose.getSelectedFile() + "";
//                if (!path.endsWith(".xlsx")) path += ".xlsx";
//                XSSFWorkbook workbook = new XSSFWorkbook();
//                XSSFSheet playerSheet = workbook.createSheet("Player stats");
//                XSSFSheet pointSheet = workbook.createSheet("Point stats");
//                XSSFSheet cdepthSheet = workbook.createSheet("C-Depth stats");
//                int rownum = 0;
//                //Generate PlayerSheet
//                Row r1 = playerSheet.createRow(rownum++);
//                String[] header = {"Player", "+15", "+10", "-5", "Points"};
//                for (int i = 0; i < header.length; i++) {
//                    r1.createCell(i).setCellValue(header[i]);
//                }
//                for (String player : PlayerManager.playerList) {
//                    Row row = playerSheet.createRow(rownum++);
//                    row.createCell(0).setCellValue(player);
//                    for (int i = 0; i < 4; i++) {
//                        row.createCell(i + 1).setCellValue(PlayerManager.playerData.get(player)[i]);
//                    }
//                }
//                //Generate other sheets
//                //Headers
//                r1 = cdepthSheet.createRow(0);
//                Row r2 = pointSheet.createRow(0);
//                for (int i = 0; i < PlayerManager.playerList.size(); i++) {
//                    r1.createCell(i + 1).setCellValue(PlayerManager.playerList.get(i));
//                    r2.createCell(i + 1).setCellValue(PlayerManager.playerList.get(i));
//                }
//                int i = 1;
//                for (Tossup q : Tossup.questionSet) {
//                    Row pointSheetRow = pointSheet.createRow(i);
//                    Row cdepthSheetRow = cdepthSheet.createRow(i);
//                    pointSheetRow.setHeight((short) 500);
//                    cdepthSheetRow.setHeight((short) 500);
//                    Cell headerCell = pointSheetRow.createCell(0);
//                    headerCell.setCellValue("Tossup " + q.id + "\n" + q.powerMark + "/" + q.size);
//                    headerCell = cdepthSheetRow.createCell(0);
//                    headerCell.setCellValue("Tossup " + q.id + "\n" + q.powerMark + "/" + q.size);
//
//                    for (QuestionWord qword : q.words) {
//                        if (qword.pointValue != 0) {
//                            int idx = 1 + PlayerManager.playerList.indexOf(qword.whoBuzzed);
//                            pointSheetRow.createCell(idx).setCellValue(qword.pointValue);
//                            cdepthSheetRow.createCell(idx).setCellValue(qword.wordID + "/" + q.size);
//                        }
//                    }
//                    i++;
//                }
//                workbook.write(new FileOutputStream(new File(path)));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }
}
