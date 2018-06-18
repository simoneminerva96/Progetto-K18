import eu.newton.BetterParser;
import eu.newton.IMathFunction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserAndEvaluationTest {

    private static final boolean assertResult = true;

    @ParameterizedTest
    @CsvSource({
            "'(+ln(+x+1))*acos((-sinh(+x)*0))', 1.7256961476116015",
            "'ln((sin(cos(tan(x))))*0 + 1)+x*0+3*0+89742389744*0+5*0', 0",
            "'2*x', 4.0",
            "'2+x-(3)-4+sin(2*x*3)', -3.53657291800043497166537422824240179231573852827804064839",
            "'x*x+x*(ln(x+x))-sin(x+x*x)*x', 7.331419718637632983292039379056495791557990266077429653449",
            "'ln((-2) + (+sin(x)) + (+3))+sqrt(x*3*5423543)', 5705.141279113647",
            "'(sin(sin(cos(x)) + 10) + 7 + (5*x + (x-3)))*x', 31.659698031905815",
            "'x + 2', 4.0",
            "'3 + 4', 7.0",
            "'-1', -1.0",
            "'(-15+12*x)+12*x*(+x)', 57",
            "'(1 +x) * (x - sin(( x - 2)))', 6.0",
            "'(5+(2*x))', 9.0",
            "'4*sin(x)', 3.637189707302726781584079463646979370809019885791561073515",
            "'44*sin(x)', 40.00908678032999459742487410011677307889921874370717180867",
            "'x+ 4 * cos(x) *2+ sin(x) + 2', 1.5801227344485425",
            "'x*x*x*x*x*x*x*x/x/x/x/x/x/x/x/x/x', 0.5",
            "'x*x+x*sin(x)+x*x+x*cos(x)', 8.986301180557078616796903272821965305872508400744690755247",
            "'x + sin(x+(x*x))', 1.720584501801074127188444553388105240372005135681795681516",
            "'(x*x)*x + x * sin(x+(x*x))', 7.441169003602148254376889106776210480744010271363591363033",
            "'sin(x) + cos(x) * sin(x)', 0.530896179171717569709700318655830295634298527779653982636",
            "'4*cos((sqrt(sin(x))/x)/x) - tan(ln(x))', 3.0562342205391601",
            "'4*cos(x - 4*sin(x + 9*ln(x + 2*sqrt(sin(x))/x)/x) - tan(ln(x)))', 1.9647154184993636",
            "'x^sin(x + cos(x))', 1.999881836756494026784735758298842837729176391109873370694",
            "'x^sqrt(x)', 2.665144142690225188650297249873139848274211313714659492835"
    })
    void testFunction(String function, double expected) throws ScriptException {
        IMathFunction f = new BetterParser().parse(function);
        BigDecimal result = f.evaluate(BigDecimal.valueOf(2));

        if (assertResult) {
            assertEquals(result.doubleValue(), expected);
            System.out.println(result);
        }

    }


}
