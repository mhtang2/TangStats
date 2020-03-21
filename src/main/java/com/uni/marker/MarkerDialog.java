package com.uni.marker;

import com.uni.Main;

import javax.swing.*;
import java.awt.*;


public class MarkerDialog extends JDialog {
    BuzzData returnData = null;
    private MarkerContainer containerT1;
    private MarkerContainer containerT2;

    public MarkerDialog(QuestionWord qword) {
        setIconImage(Main.launcherIcon);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setLocationRelativeTo(qword);
        setLayout(new GridLayout(1, 2));
        containerT1 = new MarkerContainer(qword, 0, this);
        containerT2 = new MarkerContainer(qword, 1, this);
        add(containerT1);
        add(containerT2);
        update();
        pack();
    }


    public void query() {
        setVisible(true);
        revalidate();
        pack();
    }

    public void update() {
       containerT1.update();
       containerT2.update();
    }
}
