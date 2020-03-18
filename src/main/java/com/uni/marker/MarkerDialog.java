package com.uni.marker;

import com.uni.Main;
import com.uni.Team;
import com.uni.question.QuestionWord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class MarkerDialog extends JDialog {
    BuzzData returnData = null;

    public MarkerDialog(QuestionWord qword) {
        setIconImage(Main.launcherIcon);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setLocationRelativeTo(qword);
        setLayout(new GridLayout(1, 2));
        add(new MarkerContainer(qword, 0, this));
        add(new MarkerContainer(qword, 1, this));
        pack();
    }


    public BuzzData query() {
        setVisible(true);
        revalidate();
        pack();
        return returnData;
    }
}
