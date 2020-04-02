package com.uni;

import com.uni.question.Bonus;
import com.uni.question.Tossup;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacketProcess {
    //Process pdf file and questionSet list of questions
    public static void processFile(File file) throws IOException {
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
        bonusStart = Math.min(size, bonusStart);
        Tossup[] tossupSet = new Tossup[bonusStart];
        Bonus[] bonusSet = new Bonus[size - bonusStart];
        System.out.println(tossupSet.length + " " + bonusSet.length);
        //Process tossups
        for (int i = 0; i < bonusStart; i++) {
            int nextIndex = i < size - 1 ? qStart.get(i + 1) : text.length();
            //Raw question
            String rawQuestion = text.substring(qStart.get(i), nextIndex);
            //Remove number
//            rawQuestion = rawQuestion.replaceFirst("^([0-9]|[1-2][0-9])\\.[^a-zA-Z\\d]", "");
            //Split off answer line
            Pattern answerPattern = Pattern.compile("ANSWER:[^a-zA-Z\\d]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher answerMatcher = answerPattern.matcher(rawQuestion);
            if (answerMatcher.find()) {
                tossupSet[i] = new Tossup(qId.get(i), rawQuestion.substring(0, answerMatcher.start()), rawQuestion.substring(answerMatcher.start()).split("<", 2)[0]);
            } else {
                tossupSet[i] = new Tossup(qId.get(i), rawQuestion, "NO ANSWER PROVIDED");
                JOptionPane.showMessageDialog(null, "Answer formatted incorrectly for q" + qId.get(i));
            }
        }
        //Process bonuses
        bonusloop:
        for (int qi = bonusStart; qi < size; qi++) {
            Bonus bonus = new Bonus();
            int nextIndex = qi < size - 1 ? qStart.get(qi + 1) : text.length();
            //Raw question
            String rawQuestion = text.substring(qStart.get(qi), nextIndex);
            //Remove number
//            rawQuestion = rawQuestion.replaceFirst("^([0-9]|[1-2][0-9])\\.[^a-zA-Z\\d]", "");
            rawQuestion = rawQuestion.split("<", 2)[0];

            //Locate [10]s
            int[] tenIdx = new int[3];
            Pattern tenPattern = Pattern.compile("^\\[10]", Pattern.MULTILINE);
            Matcher tenMatcher = tenPattern.matcher(rawQuestion);
            //Locate ANSWERs
            int[] answerIdx = new int[3];
            Pattern answerPattern = Pattern.compile("ANSWER:[^a-zA-Z\\d]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher answerMatcher = answerPattern.matcher(rawQuestion);

            for (int j = 0; j < 3; j++) {
                if (tenMatcher.find()) {
                    tenIdx[j] = tenMatcher.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Bad format, Can't find [10] in question" + qId.get(qi));
                    continue bonusloop;
                }
                if (answerMatcher.find()) {
                    answerIdx[j] = answerMatcher.start();
                } else {
                    JOptionPane.showMessageDialog(null, "Bad format,Can't find ANSWER: in question" + qId.get(qi));
                    continue bonusloop;
                }
            }
            //Get leadin
            bonus.leadin = rawQuestion.substring(0, tenIdx[0]);
            for (int i = 0; i < 3; i++) {
                bonus.q[i] = rawQuestion.substring(tenIdx[i], answerIdx[i]);
                bonus.a[i] = rawQuestion.substring(answerIdx[i], i < 2 ? tenIdx[i + 1] : rawQuestion.length());
            }
            bonus.id = 1 + qi - bonusStart;
            bonusSet[qi - bonusStart] = bonus;
        }
        Tossup.questionSet = tossupSet;
        Bonus.questionSet = bonusSet;
    }
}
