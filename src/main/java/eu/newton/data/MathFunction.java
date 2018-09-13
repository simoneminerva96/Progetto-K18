package eu.newton.data;

import eu.newton.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.DoubleUnaryOperator;

public final class MathFunction implements INewtonFunction {

    private static final Logger logger = LogManager.getLogger(MathFunction.class);
    
    private final String function;
    private final DoubleUnaryOperator f;

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
        double x1 = MathHelper.add(x,-0.0001);
        double x2 = MathHelper.add(x,0.0001);

        double y1 = evaluate(x1);
        double y2 = evaluate(x2);

        double m = MathHelper.add(y2, -y1) / MathHelper.add(x2, -x1);

        while (grade > 1) {
            y1 = differentiate(MathHelper.add(m,-0.0001), 0);
            y2 = differentiate(MathHelper.add(m,0.0001), 0);

            m = MathHelper.add(y2, -y1) / MathHelper.add(x2, -x1);

            grade--;
        }

        return m;
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

    @Override
    public int hashCode() {
        return this.function.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof INewtonFunction && this.function.equals(obj.toString());
    }
}
