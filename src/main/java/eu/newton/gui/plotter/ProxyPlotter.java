package eu.newton.gui.plotter;

import eu.newton.data.INewtonFunction;
import eu.newton.util.MathHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Path;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.DoubleConsumer;

public class ProxyPlotter {

    private final Map<INewtonFunction, Int2ObjectOpenHashMap<Path>> functions = new Object2ObjectOpenHashMap<>();
    private final EnumMap<KeyCode, DoubleConsumer> commands = new EnumMap<>(KeyCode.class);
    private final CartesianPlane plane;

    public ProxyPlotter(CartesianPlane plane) {
        this.plane = plane;
        initCommands();
        setupDragAndZoom();
    }

    private void setupDragAndZoom() {
        this.plane.setFocusTraversable(true);

        this.plane.setOnMouseClicked(event -> this.plane.requestFocus());

        this.plane.setOnKeyPressed(event -> {
            DoubleConsumer command = this.commands.get(event.getCode());

            if (command != null) {
                double step = getStep();
                command.accept(step);
                repaint();
                this.plane.requestFocus();
            }
        });
    }

    private void initCommands() {
        this.commands.put(KeyCode.RIGHT, step -> {
            this.plane.getxAxis().setLowerBound(MathHelper.add(this.plane.getxAxis().getLowerBound(), step));
            this.plane.getxAxis().setUpperBound(MathHelper.add(this.plane.getxAxis().getUpperBound(), step));
        });

        this.commands.put(KeyCode.LEFT, step -> {
            this.plane.getxAxis().setLowerBound(MathHelper.add(this.plane.getxAxis().getLowerBound(), -step));
            this.plane.getxAxis().setUpperBound(MathHelper.add(this.plane.getxAxis().getUpperBound(), -step));
        });

        this.commands.put(KeyCode.UP, step -> {
            this.plane.getyAxis().setUpperBound(MathHelper.add(this.plane.getyAxis().getUpperBound(), step));
            this.plane.getyAxis().setLowerBound(MathHelper.add(this.plane.getyAxis().getLowerBound(), step));
        });

        this.commands.put(KeyCode.DOWN, step -> {
            this.plane.getyAxis().setLowerBound(MathHelper.add(this.plane.getyAxis().getLowerBound(), -step));
            this.plane.getyAxis().setUpperBound(MathHelper.add(this.plane.getyAxis().getUpperBound(), -step));
        });

        this.commands.put(KeyCode.PLUS, step -> {
            double half = ((this.plane.getxAxis().getUpperBound() - this.plane.getxAxis().getLowerBound()) / 2);
            if (half > 0.1) {
                this.plane.getyAxis().setLowerBound(MathHelper.add(this.plane.getyAxis().getLowerBound(), step));
                this.plane.getyAxis().setUpperBound(MathHelper.add(this.plane.getyAxis().getUpperBound(), -step));
                this.plane.getxAxis().setLowerBound(MathHelper.add(this.plane.getxAxis().getLowerBound(), step));
                this.plane.getxAxis().setUpperBound(MathHelper.add(this.plane.getxAxis().getUpperBound(), -step));
            }
        });

        this.commands.put(KeyCode.MINUS, step -> {
            this.plane.getyAxis().setLowerBound(MathHelper.add(this.plane.getyAxis().getLowerBound(), -step));
            this.plane.getyAxis().setUpperBound(MathHelper.add(this.plane.getyAxis().getUpperBound(), step));
            this.plane.getxAxis().setLowerBound(MathHelper.add(this.plane.getxAxis().getLowerBound(), -step));
            this.plane.getxAxis().setUpperBound(MathHelper.add(this.plane.getxAxis().getUpperBound(), step));
        });
    }

    private double getStep() {
        double half = ((this.plane.getxAxis().getUpperBound() - this.plane.getxAxis().getLowerBound()) / 2);
        return half < 10 ? 0.1 : 1;
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
