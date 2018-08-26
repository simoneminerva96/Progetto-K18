package eu.newton;

import eu.newton.api.IDoubleDifferentiable;
import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.parser.FunctionParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.DoubleUnaryOperator;

public final class MathFunction implements IDoubleDifferentiable {

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
}
