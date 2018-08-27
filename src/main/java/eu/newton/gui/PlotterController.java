package eu.newton.gui;

import com.jfoenix.controls.JFXButton;
import eu.newton.MathFunction;
import javafx.animation.TranslateTransition;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlotterController implements Initializable {

    @FXML private CartesianPlane plane;

    @FXML private VBox menu;

    @FXML private FunctionVBox slots;

    @FXML private JFXButton show;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StackPane.setAlignment(this.menu, Pos.TOP_LEFT);
        StackPane.setAlignment(this.show, Pos.TOP_LEFT);

        this.show.setVisible(false);

        this.slots.getFunctions().addListener((MapChangeListener<String, MathFunction>) change -> {
            if (change.wasAdded()) {
                this.plane.plot(change.getValueAdded());
            } else if (change.wasRemoved()) {
                this.plane.getFunctions().remove(change.getValueRemoved());
            }
        });

        this.plane.setFocusTraversable(true);

        this.plane.setOnMouseClicked(event -> PlotterController.this.plane.requestFocus());

        this.plane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() + 1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() + 1);
            } else if (event.getCode() == KeyCode.LEFT) {
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() - 1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() - 1);
            } else if (event.getCode() == KeyCode.UP) {
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() + 1);
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() + 1);
            } else if (event.getCode() == KeyCode.DOWN) {
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() - 1);
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() - 1);
            }

            for (MathFunction f : this.slots.getFunctions().values()) {
                this.plane.getFunctions().remove(f);
                this.plane.plot(f);
            }
        });
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
    public void onAdd() throws IOException {
        this.slots.getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/slot.fxml")));
    }

    @FXML
    public void onClear() {
        this.slots.getChildren().remove(1, this.slots.getChildren().size());
        this.slots.getFunctions().clear();

    }



}
