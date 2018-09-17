package eu.newton.gui.plotter;

import eu.newton.data.INewtonFunction;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

public class CartesianPlane extends Pane {

    private final Color color = Color.ORANGE.deriveColor(0, 1, 1, 0.6);

    private final NumberAxis xAxis = new NumberAxis(-8, 8, 1);
    private final NumberAxis yAxis = new NumberAxis(-8, 8, 1);

    public CartesianPlane() {
        this.xAxis.layoutYProperty().bind(heightProperty().divide(2));
        this.xAxis.prefWidthProperty().bind(widthProperty());
        this.yAxis.layoutXProperty().bind(widthProperty().divide(2));
        this.yAxis.prefHeightProperty().bind(heightProperty());

        this.xAxis.setSide(Side.BOTTOM);
        this.yAxis.setSide(Side.RIGHT);

        getChildren().addAll(this.xAxis, this.yAxis);

    }

    Path plot(INewtonFunction f) {

        Path path  = new Path();
        path.setStroke(this.color);
        path.setStrokeWidth(2);
        path.setClip(new Rectangle(0, 0, getWidth(), getHeight()));

        double x = this.xAxis.getLowerBound() * 100;
        double y = f.evaluate(x / 100);

        path.getElements().add(new MoveTo(mapX(x), mapY(y)));

        double top = this.xAxis.getUpperBound() * 100;
        double step = (this.xAxis.getUpperBound() - this.xAxis.getLowerBound()) / 16;

        while (x <= top) {
            double value = x / 100;

            y = f.evaluate(value);

            path.getElements().add(new LineTo(mapX(value), mapY(y)));

            x += step;
        }

        getChildren().add(path);
        return path;
    }

    private double mapX(double x) {
        return (x - this.xAxis.getLowerBound()) * getWidth() / (this.xAxis.getUpperBound() - this.xAxis.getLowerBound());
    }

    private double mapY(double y) {
        return (this.yAxis.getUpperBound() - y) * getHeight() / (this.yAxis.getUpperBound() - this.yAxis.getLowerBound());
    }

    public NumberAxis getxAxis() {
        return this.xAxis;
    }

    public NumberAxis getyAxis() {
        return this.yAxis;
    }
}
