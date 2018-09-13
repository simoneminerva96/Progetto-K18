package eu.newton.gui;

import com.jfoenix.controls.JFXTextField;

public class JFXFunctionTextField extends JFXTextField {

    private String previous;

    public String getPrevious() {
        return this.previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
