package eu.newton;

import eu.newton.api.IDoubleDifferentiable;
import eu.newton.api.IDoubleExtrema;
import eu.newton.api.IDoubleZero;
import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.parser.FunctionParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.DoubleUnaryOperator;

public final class MathFunction implements IDoubleDifferentiable, IDoubleExtrema, IDoubleZero {

    private static final Logger logger = LogManager.getLogger(MathFunction.class);
    
    private final String function;
    private final DoubleUnaryOperator f;

    public MathFunction(String function) throws LambdaCreationException, IllegalArgumentException {
        this.f = new FunctionParser().parse(function);
        this.function = function;
    }

    @Override
    public String toString() {
        return this.function;
    }

    @Override
    public double differentiate(double x, int grade) {
        //TODO Find a consistent way
        return 0;
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
    public double getZero(double a, double b) {
        double x = a * 100;
        double y;

        double top = b * 100;

        while (x <= top) {
            double value = x / 100;
            y = evaluate(value);
            if (y == 0) {
                return value;
            }
            x += 1;
        }

        return Double.NaN;

    }
}
