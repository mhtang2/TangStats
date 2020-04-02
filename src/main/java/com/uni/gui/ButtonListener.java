package com.uni.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface ButtonListener extends MouseListener {

    @Override
    public default void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public default void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public default void mouseEntered(MouseEvent mouseEvent) {
        UIButton b = (UIButton) mouseEvent.getSource();
        b.setBackground(b.hover);
    }

    @Override
    public default void mouseExited(MouseEvent mouseEvent) {
        UIButton b = (UIButton) mouseEvent.getSource();
        b.setBackground(b.norm);

    }
}
