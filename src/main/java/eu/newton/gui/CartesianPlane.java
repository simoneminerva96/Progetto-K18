package eu.newton.gui;

import eu.newton.MathFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import java.util.Map;

public class CartesianPlane extends Pane {

    private final static int MIN_X = -8;
    private final static int MAX_X = 8;
    private final static double X_INC = 0.01;

    private final static int MIN_Y = -6;
    private final static int MAX_Y = 6;

    private final static int AXIS_TICK = 1;

    private final NumberAxis xAxis = new NumberAxis(MIN_X, MAX_X, AXIS_TICK);
    private final NumberAxis yAxis = new NumberAxis(MIN_Y, MAX_Y, AXIS_TICK);

    private final ObservableMap<MathFunction, Int2ObjectOpenHashMap<Path>> functions = FXCollections.observableHashMap();


    public CartesianPlane() {
        this.xAxis.layoutYProperty().bind(heightProperty().divide(2));
        this.xAxis.prefWidthProperty().bind(widthProperty());
        this.yAxis.layoutXProperty().bind(widthProperty().divide(2));
        this.yAxis.prefHeightProperty().bind(heightProperty());

        this.xAxis.setSide(Side.BOTTOM);
        this.yAxis.setSide(Side.RIGHT);

        this.functions.addListener((MapChangeListener<MathFunction, Int2ObjectOpenHashMap<Path>>) change -> {
            if (change.wasRemoved()) {
                getChildren().removeAll(change.getValueRemoved().values());
            }
        });

        getChildren().addAll(this.xAxis, this.yAxis);

    }

    public void plot(MathFunction f) {

        Int2ObjectOpenHashMap<Path> map = this.functions.get(f);
        Path path = null;
        if (map == null) {
            this.functions.put(f, new Int2ObjectOpenHashMap<>());
        } else {
            path = map.get(0);
        }
        if (path == null) {
            path = new Path();
            path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
            path.setStrokeWidth(2);

            Rectangle r = new Rectangle(0, 0, getWidth(), getHeight());
            path.setClip(r);

            double x = this.xAxis.getLowerBound();
            double y = f.evaluate(x);

            path.getElements().add(new MoveTo(mapX(x), mapY(y)));


            x += X_INC;
            while (x <= this.xAxis.getUpperBound() * 2) {
                y = f.evaluate(x);

                path.getElements().add(new LineTo(mapX(x), mapY(y)));


                x += X_INC;
            }
            this.functions.get(f).put(0, path);
            getChildren().add(path);
        }
    }

    public void plot(MathFunction f, int order) {

        Path path = new Path();
        path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
        path.setStrokeWidth(2);

        Rectangle r = new Rectangle(0, 0, getWidth(), getHeight());
        path.setClip(r);

        double x = this.xAxis.getLowerBound();
        double y = f.evaluate(x);

        path.getElements().add(new MoveTo(mapX(x), mapY(y)));

        x += X_INC;
        while (x <= this.xAxis.getUpperBound() * 2) {
            y = f.evaluate(x);

            path.getElements().add(new LineTo(mapX(x), mapY(y)));


            x += X_INC;
        }

        getChildren().add(path);
    }

    private double mapX(double x) {
        return  (x - this.xAxis.getLowerBound()) * getWidth() / (this.xAxis.getUpperBound() - this.xAxis.getLowerBound());
    }

    private double mapY(double y) {
        return  (this.yAxis.getUpperBound() - y) * getHeight() / (this.yAxis.getUpperBound() - this.yAxis.getLowerBound());
    }

    public ObservableMap<MathFunction, Int2ObjectOpenHashMap<Path>> getFunctions() {
        return this.functions;
    }

    public NumberAxis getxAxis() {
        return this.xAxis;
    }

    public NumberAxis getyAxis() {
        return this.yAxis;
    }
}
