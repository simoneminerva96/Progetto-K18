package eu.newton.parser;

import eu.newton.data.INewtonFunction;
import eu.newton.magic.exceptions.LambdaCreationException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class FunctionFlyWeightFactory {

    private static final Map<String, INewtonFunction> functions = new Object2ObjectOpenHashMap<>();

    public static INewtonFunction getFunction(String input) throws LambdaCreationException, IllegalArgumentException {
        INewtonFunction f = functions.get(input);
        if (f == null) {
            f = new FunctionParser().parse(input);
            functions.put(input, f);
        }
        return f;
    }

}
