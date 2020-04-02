package com.uni.marker;

import com.uni.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class MarkerDialog extends JDialog {
    BuzzData returnData = null;
    private MarkerContainer containerT1;
    private MarkerContainer containerT2;

    public MarkerDialog(QuestionWord qword) {
        setIconImage(Main.launcherIcon);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setLayout(new GridLayout(1, 2));
        containerT1 = new MarkerContainer(qword, 0, this);
        containerT2 = new MarkerContainer(qword, 1, this);
        containerT1.setBorder(new EmptyBorder(0, 0, 0, 5));
        containerT2.setBorder(new EmptyBorder(0, 5, 0, 0));
        add(containerT1);
        add(containerT2);
        update();
        pack();
        setLocationRelativeTo(null);
    }


    public void query() {
        setVisible(true);
        repaint();
        revalidate();
        pack();
        setLocationRelativeTo(null);
    }

    public void update() {
        containerT1.update();
        containerT2.update();
    }
}
