package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
    private INewtonFunction function;
    private int forder = 0;

    @FXML private JFXTextField input;

    private int n = 0;
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
        this.order.setText(String.valueOf(this.n));
    }

    @FXML
    private void onDecrement() {
        if (this.n > 0) {
            this.n--;
            this.order.setText(String.valueOf(this.n));
        }
    }

    @FXML
    private void onToggleVisible() {
        if (this.function == null) {
            return;
        }
        if (this.toggle.getGraphic() == this.show) {
            this.toggle.setGraphic(this.hide);
            this.plotter.remove(this.function);
        } else {
            this.toggle.setGraphic(this.show);
            this.plotter.plot(this.function);
        }
    }

    @FXML
    private void onRefresh() {
        String input = this.input.getText();
        if (this.toggle.getGraphic() == this.hide) {
            return;
        }

        if (this.function != null) {
            if (input.equals(this.function.toString()) && this.n == this.forder) {
                return;
            }
            this.plotter.remove(this.function);
        }
        try {
            INewtonFunction f;
            if (this.n == 0) {
                f = FunctionFlyWeightFactory.getInstance().getFunction(input);
            } else {
                f = FunctionFlyWeightFactory.getInstance().getDerivative(input, this.n);
            }
            this.function = f;
            this.forder = this.n;
            this.plotter.plot(f);
        } catch (LambdaCreationException e) {
        } catch (IllegalArgumentException ex) {
        }
    }

    @FXML
    private void onRemove() {
        this.plotter.remove(this.function);
        this.input.setText("");
        this.function = null;
    }


}
