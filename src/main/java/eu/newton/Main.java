package eu.newton;

import eu.newton.data.INewtonFunction;
import eu.newton.data.MathFunction;
import eu.newton.parser.FunctionFlyWeightFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        final String f = "sin(pi)+ln(e)";

        try {
            INewtonFunction function = FunctionFlyWeightFactory.getFunction(f);

            double result;

            try {
                result = function.evaluate(0.2);
                logger.debug("RESULT: {}", result);
            } catch (NumberFormatException | ArithmeticException ex) {
                logger.error(":(");
                logger.error(ex.getMessage());
            } catch (Exception ex) {
                logger.error("Stop it java");
                logger.error(ex.getMessage());
                ex.printStackTrace();
            }

        } catch (Exception e) {
            logger.error(":(((");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

}


