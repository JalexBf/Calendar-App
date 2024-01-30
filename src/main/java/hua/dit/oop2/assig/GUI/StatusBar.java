package hua.dit.oop2.assig.GUI;

import javax.swing.*;


public class StatusBar extends JLabel {

    public StatusBar() {
        super();
        setMessage("Ready");
    }

    public void setMessage(String message) {
        setText(" Status: " + message);
    }
}
