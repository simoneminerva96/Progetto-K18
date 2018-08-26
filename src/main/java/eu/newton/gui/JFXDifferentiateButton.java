package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import javafx.beans.NamedArg;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class JFXDifferentiateButton extends JFXButton {

    private IntegerProperty i;

    public JFXDifferentiateButton(@NamedArg("text") String text, int i) {
        super(text);
        this.i = new SimpleIntegerProperty(i);
    }

    public JFXDifferentiateButton(@NamedArg("text") String text) {
        this(text, 0);
    }

    public IntegerProperty orderProperty() {
        return this.i;
    }

    public void increment() {
        this.i.set(this.i.get() + 1);
    }

    public void decrement() {
        if (this.i.get() > 1) {
            this.i.set(this.i.get() - 1);
        }
    }

    public void setValue(int i) {
        if (i > 0) {
            this.i.set(i);
        }
    }
}
