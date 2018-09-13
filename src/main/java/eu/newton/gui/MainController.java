package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import eu.newton.gui.plotter.CartesianPlane;
import eu.newton.gui.plotter.ProxyPlotter;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    private ProxyPlotter plotter;

    @FXML private CartesianPlane plane;

    @FXML private VBox menu;

    @FXML private VBox slots;

    @FXML private JFXButton show;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StackPane.setAlignment(this.menu, Pos.TOP_LEFT);
        StackPane.setAlignment(this.show, Pos.TOP_LEFT);

        this.plotter = new ProxyPlotter(this.plane);
        this.show.setVisible(false);
        this.slots.getChildren().add(new FunctionSlot(this.plotter));
    }

    @FXML
    public void onHide() {
        TranslateTransition t = new TranslateTransition(new Duration(250), this.menu);
        t.setToX(-this.menu.getMaxWidth());
        t.play();
        t.setOnFinished(e -> this.show.setVisible(true));
    }

    @FXML
    public void onShow() {
        TranslateTransition t = new TranslateTransition(new Duration(250), this.menu);
        t.setToX(-this.menu.getMinWidth());
        t.play();
        t.setOnFinished(e -> this.show.setVisible(false));
    }

    @FXML
    public void onAdd() {
        this.slots.getChildren().add(new FunctionSlot(this.plotter));
    }

    @FXML
    public void onClear() {
        this.slots.getChildren().clear();
        this.plotter.clear();
        onAdd();
    }



}
