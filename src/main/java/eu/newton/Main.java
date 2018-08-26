package eu.newton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        final String f = "x*x+3^2-3^2+1";

        try {
            MathFunction function = new MathFunction(f);

            double result;

            try {
                result = function.evaluate(0.2);
                logger.debug("RESULT: {}", result);
            } catch (NumberFormatException | ArithmeticException ex) {
                logger.error("Are you retarded ? ");
                logger.error(ex.getMessage());
            } catch (Exception ex) {
                logger.error("Stop it java");
                logger.error(ex.getMessage());
                ex.printStackTrace();
            }

        } catch (Exception e) {
            logger.error("Good job, you won. Now fuck off ");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

}


