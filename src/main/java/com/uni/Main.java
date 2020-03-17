package com.uni;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SetFormatException extends Exception {
    public SetFormatException(String error) {
        super(error);
    }
}

public class Main {

    public static Window window;
    public static Image launcherIcon;

    public static void main(String[] args) throws IOException, SetFormatException {
        launcherIcon = ImageIO.read(Main.class.getResourceAsStream("/fifteen.png"));
        window = new Window(1000, "i read qb questions");
//        processFile(new File("./dogs.pdf"));
        processFile(new File("./packet1.pdf"));
//        for (int i = 0; i < 5; i++) {
//            window.playermanager.addPlayer("P" + i);
//        }
        window.setQuestion(0);
    }


    //Process pdf file and questionSet list of questions
    public static void processFile(File file) throws IOException, SetFormatException {
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        String text = textStripper.getText(doc);
        doc.close();


        //Match for 1-20
        Pattern pattern = Pattern.compile("^([0-9]|[1-2][0-9])\\.[^a-zA-Z\\d]", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);

        //Hold start index of questions
        ArrayList<Integer> qStart = new ArrayList<>();
        //Hold id of questions
        ArrayList<Integer> qId = new ArrayList<>();
        //Index of where bonuses start
        int bonusStart = Integer.MAX_VALUE;
        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group().replaceAll("[^0-9]", ""));
            qId.add(id);
            if (id == 1 && !qStart.isEmpty() && bonusStart == Integer.MAX_VALUE) {
                bonusStart = qStart.size();
            }
            qStart.add(matcher.start());
        }
        int size = qStart.size();
        bonusStart = Math.min(size,bonusStart);
        Tossup[] tossupSet = new Tossup[bonusStart];
        Bonus[] bonusSet = new Bonus[size - bonusStart];
        System.out.println(tossupSet.length + " " + bonusSet.length);
        //Process tossups
        for (int i = 0; i < bonusStart; i++) {
            int nextIndex = i < size - 1 ? qStart.get(i + 1) : text.length();
            //Raw question
            String rawQuestion = text.substring(qStart.get(i), nextIndex);
            //Remove number
            rawQuestion = rawQuestion.replaceFirst("^([0-9]|[1-2][0-9])\\.[^a-zA-Z\\d]", "");
            //Split off answer line
            Pattern answerPattern = Pattern.compile("^ANSWER:[^a-zA-Z\\d]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher answerMatcher = answerPattern.matcher(rawQuestion);
            if (answerMatcher.find()) {
                tossupSet[i] = new Tossup(qId.get(i), rawQuestion.substring(0, answerMatcher.start()), rawQuestion.substring(answerMatcher.start()).split("<", 2)[0]);
            } else {
                tossupSet[i] = new Tossup(qId.get(i), rawQuestion, "NO ANSWER PROVIDED");
                System.out.println("Answer formatted incorrectly for q" + qId.get(i));
            }
        }
        //Process bonuses
        for (int qi = bonusStart; qi < size; qi++) {
            Bonus bonus = new Bonus();
            int nextIndex = qi < size - 1 ? qStart.get(qi + 1) : text.length();
            //Raw question
            String rawQuestion = text.substring(qStart.get(qi), nextIndex);
            //Remove number
            rawQuestion = rawQuestion.replaceFirst("^([0-9]|[1-2][0-9])\\.[^a-zA-Z\\d]", "");
            rawQuestion = rawQuestion.split("<", 2)[0];

            //Locate [10]s
            int[] tenIdx = new int[3];
            Pattern tenPattern = Pattern.compile("^\\[10]", Pattern.MULTILINE);
            Matcher tenMatcher = tenPattern.matcher(rawQuestion);
            //Locate ANSWERs
            int[] answerIdx = new int[3];
            Pattern answerPattern = Pattern.compile("^ANSWER:[^a-zA-Z\\d]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher answerMatcher = answerPattern.matcher(rawQuestion);

            for (int j = 0; j < 3; j++) {
                if (tenMatcher.find()) {
                    tenIdx[j] = tenMatcher.start();
                } else {
                    System.out.println("Can't find [10] in q" + qId.get(qi));
                }
                if (answerMatcher.find()) {
                    answerIdx[j] = answerMatcher.start();
                } else {
                    System.out.println("Can't find ANSWER: in q" + qId.get(qi));
                }
            }
            //Get leadin
            bonus.leadin = rawQuestion.substring(0, tenIdx[0]);
            for (int i = 0; i < 3; i++) {
                bonus.q[i] = rawQuestion.substring(tenIdx[i], answerIdx[i]);
                bonus.a[i] = rawQuestion.substring(answerIdx[i], i < 2 ? tenIdx[i + 1] : rawQuestion.length());
            }
        }
        Tossup.questionSet = tossupSet;
        Bonus.questionSet = bonusSet;
    }

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

