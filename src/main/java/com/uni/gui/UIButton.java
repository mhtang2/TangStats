package com.uni.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.uni.Window;

public class UIButton extends JPanel {

    public final static Color defaultNorm = new Color(0xe3e3e3);
    final static Color defaultHover = new Color(0xafeeee);
    Color norm = defaultNorm;
    Color hover = defaultHover;
    private UILabel label = new UILabel("");

    public UIButton(String text, ButtonListener bl) {
        setFocusable(true);
        setBackground(norm);
        label.setText(text);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(label, new GridBagConstraints());
        addButtonListener(bl);

    }

    public UIButton(String text) {
        this(text, null);
    }

    public void addButtonListener(ButtonListener bl) {
        if (bl != null)
            addMouseListener(bl);
    }

    public void setText(String s) {
        label.setText(s);
        repaint();
    }

    public void setNorm(Color c) {
        norm = c;
        setBackground(c);
    }
}
