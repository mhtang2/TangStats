
package com.uni.question;

import com.uni.Main;
import com.uni.marker.QuestionWord;

import java.util.ArrayList;

public class Tossup {
    public static Tossup[] questionSet = {};
    public static int setidx = -1;

    String question;
    /**
     * ID assigned by set, not index
     **/
    public int id = 0;
    public String answer;
    public QuestionWord[] words;
    private int powerMark = 0;
    public int size = 0;

    public Category category = null;
    public String subcategory = null;
    public int controllingTeam = -1;
    public boolean dead = true;
    ArrayList<String> t1Active = new ArrayList<>();
    ArrayList<String> t2Active = new ArrayList<>();


    public ArrayList<String> getActive(int tid) {
        if (tid == 0) return t1Active;
        return t2Active;
    }

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

    public static Tossup current() {
        if (setidx < 0 || setidx >= questionSet.length) {
            System.out.println("NULL QUESTION RETURNED");
            return new Tossup(-1, "", "");
        }
        return questionSet[setidx];
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

}
