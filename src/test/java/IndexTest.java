import eu.newton.BetterParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.script.ScriptException;
import java.util.List;

public class IndexTest {


    @ParameterizedTest
    @CsvSource({
            "'(+ln(+x+1))*acos(-sinh(+x)*0)', 1.725696147611601330716910291909042822576095015512846424902",
            "'ln((sin(cos(tan(x))))*0 + 1)+x*0+3*0+89742389744*0+5*0', 0",
            "'2*x', 4.0",
            "'2+x-3-4+sin(2*x*3)', -2.79208830918224066289825771559487483378341523937227616359",
            "'x*x+x*(ln(x+x))-sin(x+x*x)*x', 6.563531795704474294869260176227710034140688798491834788496",
            "'ln(-2 *sin(x) +3)+sqrt(x*3*5423543)', 5706.271070724258",
            "'(sin(sin(cos(x)) + 10) + 7 + (5*x + (x-3)))*x', 31.659698031905815",
            "'x + 2', 4.0",
            "'3 + 4', 7.0",
            "'-1', -1.0",
            "'(-15+12*x)+12*x*(+x)', 57",
            "'1 * (x - sin(x - 2))', 3.0",
            "'(5+sin(2*x))', 9.0"
    })
    void testFunction(String function, double expected) throws ScriptException {
        BetterParser parser = new BetterParser();

        function = parser.getUnretardedFunction(function);
//        int index;
//
        System.out.println("F: " + function);
        System.out.println();
        System.out.println();

        List<String> list = parser.getXD(function, true);

        System.out.println(list);


    }




}
