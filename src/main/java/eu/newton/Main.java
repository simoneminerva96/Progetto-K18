package eu.newton;

import eu.newton.data.INewtonFunction;
import eu.newton.parser.DerivativeParser;
import eu.newton.parser.FunctionFlyWeightFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        final String f = "sin(pi)+ln(e)";

        try {
            INewtonFunction function = FunctionFlyWeightFactory.getFunction(f);

            double result;

            try {
                result = function.evaluate(7.0);
                LOGGER.debug("RESULT: {}", result);
            } catch (NumberFormatException | ArithmeticException ex) {
                LOGGER.error(":(");
                LOGGER.error(ex.getMessage());
            } catch (Exception ex) {
                LOGGER.error("Stop it java");
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }

        } catch (Exception e) {
            LOGGER.error(":(((");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

    }

}


