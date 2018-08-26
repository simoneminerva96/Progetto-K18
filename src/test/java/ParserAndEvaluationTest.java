import eu.newton.MathFunction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserAndEvaluationTest {

    @ParameterizedTest
    @CsvFileSource(resources = "functions.csv", numLinesToSkip = 2)
    void testFunction(String function, double expected) throws Exception {
        MathFunction f = new MathFunction(function);
        double result = f.evaluate(2.0);

        assertEquals(((double) ((int) (expected * 1000))) / 1000, ((double) ((int) (result * 1000))) / 1000);
        System.out.println(result);

    }

}
