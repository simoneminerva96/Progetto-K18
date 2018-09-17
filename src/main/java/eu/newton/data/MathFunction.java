package eu.newton.data;

import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.parser.FunctionFlyWeightFactory;
import eu.newton.util.MathHelper;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.DoubleUnaryOperator;

public final class MathFunction implements INewtonFunction {
    
    private final String function;
    private final DoubleUnaryOperator f;
    private INewtonFunction derivative;
    private int order;

    public MathFunction(DoubleUnaryOperator f, String function) {
        this.f = f;
        this.function = function;
    }

    @Override
    public String toString() {
        return this.function;
    }

    @Override
    public double differentiate(double x, int grade) {
        if (this.derivative == null || this.order != grade) {
            try {
                this.derivative = FunctionFlyWeightFactory.getDerivative(this.function, grade);
                this.order = grade;
            } catch (LambdaCreationException e) {
                //TODO couldn't differentiate this function
            }
        }

        return this.derivative.evaluate(x);
    }

    @Override
    public double evaluate(double x) {
        return this.f.applyAsDouble(x);
    }

    @Override
    public double getMaximum(double a, double b) {

        double x = a * 100;
        double y = evaluate(x / 100);

        double max = x / 100;
        double temp = y;

        double top = b * 100;

        x += 1;
        while (x <= top) {
            double value = x / 100;
            y = evaluate(value);
            if (y > temp) {
                temp = y;
                max = value;
            }

            x += 1;
        }

        return max;
    }

    @Override
    public double getMinimum(double a, double b) {
        double x = a * 100;
        double y = evaluate(x / 100);

        double min = x / 100;
        double temp = y;

        double top = b * 100;

        x += 1;
        while (x <= top) {
            double value = x / 100;
            y = evaluate(value);
            if (y < temp) {
                temp = y;
                min = value;
            }

            x += 1;
        }

        return min;
    }

    @Override
    public double[] getZeros(double a, double b) {
        DoubleArrayList zeros = new DoubleArrayList();

        double x = a * 100;
        double y;

        double top = b * 100;
        double step = (a - b) / 16;

        while (x <= top) {
            double value = x / 100;
            y = evaluate(value);

            if (y == 0) {
                zeros.add(x);
            }
            x += step;
        }

        return zeros.toDoubleArray();

    }

    @Override
    public int hashCode() {
        return this.function.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof INewtonFunction && this.function.equals(obj.toString());
    }
}
