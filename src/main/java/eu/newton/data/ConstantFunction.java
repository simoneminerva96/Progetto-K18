package eu.newton.data;

public final class ConstantFunction implements INewtonFunction {

    private final double k;
    private final String function;

    public ConstantFunction(double k, String function) {
        this.k = k;
        this.function = function;
    }


    @Override
    public double differentiate(double x, int grade) {
        return 0;
    }

    @Override
    public double getMaximum(double a, double b) {
        return k;
    }

    @Override
    public double getMinimum(double a, double b) {
        return k;
    }

    @Override
    public double[] getZeros(double a, double b) {
        return null;
    }

    @Override
    public double evaluate(double x) {
        return this.k;
    }

    @Override
    public int hashCode() {
        return this.function.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof INewtonFunction && this.function.equals(obj.toString());
    }

    @Override
    public String toString() {
        return this.function;
    }
}
