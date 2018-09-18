package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import eu.newton.data.INewtonFunction;
import eu.newton.gui.plotter.ProxyPlotter;
import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.parser.FunctionFlyWeightFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class FunctionSlot extends HBox {

    private final FontAwesomeIconView show = new FontAwesomeIconView(FontAwesomeIcon.EYE);
    private final FontAwesomeIconView hide = new FontAwesomeIconView(FontAwesomeIcon.EYE_SLASH);
    private final FontAwesomeIconView update = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
    private final FontAwesomeIconView trash = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

    private final ProxyPlotter plotter;
    private int n = 0;
    private INewtonFunction function;

    @FXML private JFXFunctionTextField input;

    @FXML private Label order;

    @FXML private JFXButton toggle;

    @FXML private JFXButton refresh;

    @FXML private JFXButton delete;

    public FunctionSlot(ProxyPlotter plotter) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SlotView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.toggle.setGraphic(this.show);
        this.refresh.setGraphic(this.update);
        this.delete.setGraphic(this.trash);

        this.plotter = plotter;
    }

    @FXML
    private void onIncrement() {
        this.n++;
    }

    @FXML
    private void onDecrement() {
        if (this.n > 0) {
            this.n--;
        }
    }

    @FXML
    private void onDifferentiate() {
        String input = this.input.getText();
        if (this.toggle.getGraphic() == this.hide || input.equals(this.input.getPrevious())) {
            return;
        }
        try {
            INewtonFunction f = FunctionFlyWeightFactory.getFunction(input);
            this.function = f;
            this.plotter.plot(f, this.n);
            this.input.setPrevious(input);
        } catch (LambdaCreationException e) {
            this.input.setPrevious("");
            //TODO invalid function
        } catch (IllegalArgumentException ex) {
            this.input.setPrevious("");
            //TODO invalid function
        }

        this.plotter.plot(this.function, this.n);

    }

    @FXML
    private void onToggleVisible() {
        if (this.function == null) {
            return;
        }
        if (this.toggle.getGraphic() == this.show) {
            this.toggle.setGraphic(this.hide);
            this.plotter.remove(this.function, this.n);
        } else {
            this.toggle.setGraphic(this.show);
            this.plotter.plot(this.function, this.n);
        }
    }

    @FXML
    private void onRefresh() {
        String input = this.input.getText();
        if (this.toggle.getGraphic() == this.hide) {
            return;
        }
        try {
            INewtonFunction f = FunctionFlyWeightFactory.getFunction(input);
            this.function = f;
            this.plotter.plot(f, this.n);
            this.input.setPrevious(input);
        } catch (LambdaCreationException e) {
            this.input.setPrevious("");
            //TODO invalid function
        } catch (IllegalArgumentException ex) {
            this.input.setPrevious("");
            //TODO invalid function
        }
    }

    @FXML
    private void onRemove() {
        this.plotter.remove(this.function, this.n);
        this.input.setText("");
    }


}
