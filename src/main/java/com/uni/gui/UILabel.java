package com.uni.gui;

import javax.swing.*;
import java.awt.*;

import com.uni.Window;

public class UILabel extends JLabel {
    public UILabel(String text, Font f) {
        super(text, SwingConstants.CENTER);
        setFont(f);
    }

    public UILabel() {
    }

    public UILabel(String text) {
        super(text, SwingConstants.CENTER);
        setFont(Window.defFont);
    }

    public UILabel(String text, boolean bold) {
        super(text, SwingConstants.CENTER);
        if (bold) setFont(Window.boldFont);
        else setFont(Window.defFont);
    }
}
