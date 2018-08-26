package eu.newton;

import eu.newton.api.IDoubleDifferentiable;
import eu.newton.api.IDoubleFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.DoubleUnaryOperator;

public final class MathFunction implements IDoubleFunction, IDoubleDifferentiable {

    private static final Logger logger = LogManager.getLogger(MathFunction.class);
    
    private final String function;
    private final DoubleUnaryOperator f;

    public MathFunction(String function) throws Exception {
        this.f = new FunctionParser().parse(function);
        this.function = function;
    }

    @Override
    public String toString() {
        return function;
    }

    @Override
    public double differentiate(double x, int grade) {
        //TODO Find a consistent way
        return 0;
    }

    @Override
    public double evaluate(double x) {
        return f.applyAsDouble(x);
    }
}
