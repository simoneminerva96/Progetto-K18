import eu.newton.data.INewtonFunction;
import eu.newton.data.MathFunction;
import eu.newton.parser.FunctionFlyWeightFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserAndEvaluationTest {

    @ParameterizedTest
    @CsvFileSource(resources = "functions.csv")
    void testFunction(String function, double expected) throws Exception {
        INewtonFunction f = FunctionFlyWeightFactory.getInstance().getFunction(function);
        double result = f.evaluate(2.0);

        assertEquals(((double) Math.round(expected * 1000)) / 1000, ((double) Math.round(result * 1000)) / 1000);
    }

}
