package eu.newton;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.function.Function;


public class Main {

    public static void main(String[] args) {

        BetterParser parser = new BetterParser();
        final String f = "x^sin(x)^x";


        BigDecimal result = null;
        try {
            IMathFunction plzdontdothisathome = parser.parse(f);
            try {
                //result = plzdontdothisathome.differentiate(k(2), 1);
                result = plzdontdothisathome.evaluate(k(2.0));
            } catch (NumberFormatException | ArithmeticException ex) {
                System.err.println("Are you retarded ? ");
                System.err.println(ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Stop it java");
                System.err.println(ex.getMessage());
                ex.printStackTrace();

            }
        } catch (Exception ex) {
            System.err.println("Good job, you won. Now fuck off ");
            System.err.println(ex.getMessage());


        }

        System.out.println("RESULT: " + result);

    }




    public static BigDecimal k(double d) {
        return BigDecimal.valueOf(d);
    }




//    public static BigDecimal precise(BigDecimal a, BigDecimal b) {
//        return a.divide(b, 1000, BigDecimal.ROUND_CEILING);
//    }

}


