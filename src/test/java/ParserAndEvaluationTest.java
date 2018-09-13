import eu.newton.data.INewtonFunction;
import eu.newton.data.MathFunction;
import eu.newton.parser.FunctionFlyWeightFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserAndEvaluationTest {

    @ParameterizedTest
    @CsvFileSource(resources = "functions.csv", numLinesToSkip = 2)
    void testFunction(String function, double expected) throws Exception {
        INewtonFunction f = FunctionFlyWeightFactory.getFunction(function);
        double result = f.evaluate(2.0);

        assertEquals(((double) ((long) (expected * 1000))) / 1000, ((double) ((long) (result * 1000))) / 1000);
    }

}
