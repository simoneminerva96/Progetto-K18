package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import eu.newton.MathFunction;
import eu.newton.magic.exceptions.LambdaCreationException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


public class FunctionSlotController implements Initializable {

    private final FontAwesomeIconView show = new FontAwesomeIconView(FontAwesomeIcon.EYE);

    private final FontAwesomeIconView hide = new FontAwesomeIconView(FontAwesomeIcon.EYE_SLASH);

    private final FontAwesomeIconView update = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);

    private final FontAwesomeIconView trash = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

    @FXML private JFXFunctionTextField function;

    @FXML private JFXDifferentiateButton derivative;

    @FXML private Label order;

    @FXML private JFXButton toggle;

    @FXML private JFXButton refresh;

    @FXML private JFXButton delete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.order.textProperty().bind(this.derivative.orderProperty().asString());
        this.derivative.prefHeightProperty().bind(this.delete.heightProperty());

        this.toggle.setGraphic(this.show);
        this.refresh.setGraphic(this.update);
        this.delete.setGraphic(this.trash);

    }

    @FXML
    private void onIncrement() {
        this.derivative.increment();
    }

    @FXML
    private void onDecrement() {
        this.derivative.decrement();
    }

    @FXML
    private void onDifferentiate() {

    }

    @FXML
    private void onToggle() {
        FunctionVBox box = ((FunctionVBox) this.derivative.getParent().getParent().getParent());
        String last = this.function.getPrevious();
        if (this.toggle.getGraphic() == this.show) {
            this.toggle.setGraphic(this.hide);
            box.getFunctions().remove(last);
        } else {
            this.toggle.setGraphic(this.show);
            MathFunction f = box.getFunctions().get(last);
            if (f == null && !last.isEmpty()) {
                try {
                    f = new MathFunction(last);
                    box.getFunctions().put(last, f);
                    this.function.setPrevious(last);
                } catch (LambdaCreationException e) {
                    this.function.setPrevious("");
                    //TODO invalid function. (It's probably safe to ignore this)
                } catch (IllegalArgumentException ex) {
                    this.function.setPrevious("");
                    //TODO invalid function. (It's probably safe to ignore this)
                }
            }
        }
    }

    @FXML
    private void onRefresh() {
        String input = this.function.getText();
        if (this.toggle.getGraphic() == this.hide || input.equals(this.function.getPrevious())) {
            return;
        }
        FunctionVBox box = ((FunctionVBox) this.derivative.getParent().getParent().getParent());
        box.getFunctions().remove(this.function.getPrevious());
        MathFunction f = box.getFunctions().get(input);
        if (f == null) {
            if (input.isEmpty()) {
                this.function.setPrevious("");
                return;
            }
            try {
                f = new MathFunction(input);
                box.getFunctions().put(input, f);
                this.function.setPrevious(input);
            } catch (LambdaCreationException e) {
                this.function.setPrevious("");
                //TODO invalid function
            } catch (IllegalArgumentException ex) {
                this.function.setPrevious("");
                //TODO invalid function
            }
        }
    }

    @FXML
    private void onRemove() {
        FunctionVBox box = ((FunctionVBox) this.derivative.getParent().getParent().getParent());
        ObservableList<Node> slots = box.getChildren();
        if (slots.size() != 1) {
            box.getFunctions().remove(this.function.getPrevious());
            slots.remove(this.derivative.getParent().getParent());
        }
    }

}
