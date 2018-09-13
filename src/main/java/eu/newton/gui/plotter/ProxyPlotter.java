package eu.newton.gui.plotter;

import eu.newton.data.INewtonFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Path;

import java.util.Map;

public class ProxyPlotter {

    private final Map<INewtonFunction, Int2ObjectOpenHashMap<Path>> functions = new Object2ObjectOpenHashMap<>();

    private final CartesianPlane plane;

    public ProxyPlotter(CartesianPlane plane) {
        this.plane = plane;
        setupDragAndZoom();
    }

    private void setupDragAndZoom() {
        this.plane.setFocusTraversable(true);

        this.plane.setOnMouseClicked(event -> this.plane.requestFocus());

        this.plane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() + 1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() + 1);
                repaint();
            } else if (event.getCode() == KeyCode.LEFT) {
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() - 1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() - 1);
                repaint();
            } else if (event.getCode() == KeyCode.UP) {
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() + 1);
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() + 1);
                repaint();
            } else if (event.getCode() == KeyCode.DOWN) {
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() - 1);
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() - 1);
                repaint();
            } else if (event.getCode() == KeyCode.PLUS) {
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() + 0.1);
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() - 0.1);
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() + 0.1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() - 0.1);
                repaint();
            } else if (event.getCode() == KeyCode.MINUS) {
                this.plane.getyAxis().setLowerBound(this.plane.getyAxis().getLowerBound() - 0.1);
                this.plane.getyAxis().setUpperBound(this.plane.getyAxis().getUpperBound() + 0.1);
                this.plane.getxAxis().setLowerBound(this.plane.getxAxis().getLowerBound() - 0.1);
                this.plane.getxAxis().setUpperBound(this.plane.getxAxis().getUpperBound() + 0.1);
                repaint();
            }
        });
    }

    public void plot(INewtonFunction f, int order) {
        Int2ObjectOpenHashMap<Path> map = this.functions.get(f);
        Path path = null;
        if (map == null) {
            map = new Int2ObjectOpenHashMap<>();
            this.functions.put(f, map);
        } else {
            path = map.get(order);
        }
        if (path == null) {
            if (order == 0) {
                path = this.plane.plot(f);
            } else {
                path = this.plane.plot(f, order);
            }
            map.put(order, path);
        }
    }

    public void remove(INewtonFunction f, int order) {
        Int2ObjectOpenHashMap<Path> map = this.functions.get(f);
        if (map != null) {
            Path old = map.remove(order);
            this.plane.getChildren().remove(old);
        }
    }

    public void clear() {
        for (Map.Entry<INewtonFunction, Int2ObjectOpenHashMap<Path>> entry : this.functions.entrySet()) {
            for (Int2ObjectMap.Entry<Path> fastEntry : entry.getValue().int2ObjectEntrySet()) {
                this.plane.getChildren().remove(fastEntry.getValue());
            }
        }
        this.functions.clear();
    }

    public void repaint() {
        for (Map.Entry<INewtonFunction, Int2ObjectOpenHashMap<Path>> entry : this.functions.entrySet()) {
            for (Int2ObjectMap.Entry<Path> fastEntry : entry.getValue().int2ObjectEntrySet()) {
                this.plane.getChildren().remove(fastEntry.getValue());
                int order = fastEntry.getIntKey();
                Path path;
                if (order == 0) {
                    path = this.plane.plot(entry.getKey());
                } else {
                    path = this.plane.plot(entry.getKey(), fastEntry.getIntKey());
                }
                entry.getValue().replace(order, path);
            }
        }
    }



}
