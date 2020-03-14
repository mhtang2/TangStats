
package com.uni;

public class Question {
    public static Question[] questionSet;
    public static int setidx = -1;
    public int id;
    public QuestionWord[] words;
    public int powerMark = 0;
    public int size = 0;
    String question, answer;

    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        String[] splits = question.split("[\n ]");
        size = splits.length;
        words = new QuestionWord[size];
        for (int i = 0; i < size; i++) {
            words[i] = new QuestionWord(i, splits[i]);
            if (splits[i].contains("(*)")) powerMark = i;
        }
    }


    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
