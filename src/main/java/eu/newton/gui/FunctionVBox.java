package eu.newton.gui;

import eu.newton.MathFunction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;

public class FunctionVBox extends VBox {

    private final ObservableMap<String, MathFunction> functions = FXCollections.observableMap(new LinkedHashMap<>());

    public ObservableMap<String, MathFunction> getFunctions() {
        return this.functions;
    }
}
