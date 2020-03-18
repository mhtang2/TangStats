
package com.uni.question;

import com.uni.Category;

public class Tossup {
    public static Tossup[] questionSet;
    public static int setidx = -1;

    public int id;
    public QuestionWord[] words;
    public int powerMark = 0;
    public int size = 0;
    Category category = null;
    String subcategory = null;

    //Hold team buzz data
    String whoBuzzed[] = new String[]{null, null};
    int pointValue[] = new int[]{0, 0};

    //Array of point values
    public static final int[] pointVals = new int[]{15, 10, -5};
    String question;
    public String answer;

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

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
