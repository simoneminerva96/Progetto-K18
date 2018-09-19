package eu.newton.parser;

import eu.newton.data.INewtonFunction;
import eu.newton.magic.exceptions.LambdaCreationException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class FunctionFlyWeightFactory {

    private final Map<String, INewtonFunction> FUNCTIONS = new Object2ObjectOpenHashMap<>();
    private static final FunctionFlyWeightFactory INSTANCE = new FunctionFlyWeightFactory();

    public static FunctionFlyWeightFactory getInstance() {
        return INSTANCE;
    }

    public INewtonFunction getFunction(String input) throws LambdaCreationException, IllegalArgumentException {
        INewtonFunction f = FUNCTIONS.get(input);
        if (f == null) {
            f = new FunctionParser().parse(input);
            FUNCTIONS.put(input, f);
        }
        return f;
    }

    public INewtonFunction getDerivative(String input, int order) throws LambdaCreationException, IllegalArgumentException {
        while (order-- > 0) {
            input = new DerivativeParser().getDerivative(input);
        }

        return getFunction(input);
    }


}
