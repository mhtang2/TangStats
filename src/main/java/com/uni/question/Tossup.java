
package com.uni.question;

import com.uni.Main;
import com.uni.Team;
import com.uni.marker.QuestionWord;

import java.awt.*;

public class Tossup {
    public static Tossup[] questionSet;
    public static int setidx = -1;

    String question;
    /**
     * ID assigned by set, not index
     **/
    public int id;
    public String answer;
    public QuestionWord[] words;
    private int powerMark = 0;
    private int size = 0;
    public Category category = null;
    public String subcategory = null;

    public int controllingTeam = -1;
    public boolean dead = true;

    public Tossup(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        String[] splits = question.split("[\n ]");
        size = splits.length;
        words = new QuestionWord[size];
        for (int i = 0; i < size; i++) {
            words[i] = new QuestionWord(i, splits[i], this);
            if (splits[i].contains("(*)")) powerMark = i;
        }
    }

    public void setControllingTeam(int control) {
        controllingTeam = control;
        dead = false;
        if (control == -1) {
            dead = true;
            //Search for any bonus control
            for (QuestionWord qword : words) {
                if (qword.buzzData.point == 0 || qword.buzzData.point == 1) {
                    System.out.println(qword.getText());
                    setControllingTeam(qword.buzzData.teamId);
                    return;
                }
            }
            //No bonus control found
        }
        Main.window.setControllingTeam(control);
    }

    public static int getCorrespondingBonus() {
        int temp = setidx;
        for (int i = 0; i < setidx; i++) {
            if (questionSet[i].dead)
                temp--;
        }
        return temp;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
