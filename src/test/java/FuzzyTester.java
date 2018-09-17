import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import eu.newton.data.INewtonFunction;
import eu.newton.parser.FunctionFlyWeightFactory;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.PrimitiveIterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuzzyTester {

    private static final String WOLFRAM_API_REFERER = "https://www.wolframalpha.com/input/?i=";
    private static final String WOLFRAM_API = "https://www.wolframalpha.com/input/json.jsp?assumption=%22TrigRD%22+-%3E+%22R%22&assumptionsversion=2&async=true&banners=raw&debuggingdata=false&format=plaintext&formattimeout=8&input=";
    private static final String WOLFRAM_API_CENTER = "&output=JSON&parsetimeout=5&podinfosasync=true&proxycode=";
    private static final String WOLFRAM_API_END = "&recalcscheme=parallel&sbsdetails=false&scantimeout=0.5&sponsorcategories=false&statemethod=deploybutton&storesubpodexprs=false";
    private static final String WOLFRAM_PAYLOAD_ENDPOINT = "https://www.wolframalpha.com/input/api/v1/code?ts=";

    private static final String[] MATH_FUNCTIONS = new String[] {
            "ln(", "log(",
            "sin(", "cos(", "tan(", "asin(", "acos(", "atan(", //"sinh(", "cosh(", "tanh(",
            "sqrt(",
    };
    private static final String[] OPS = new String[] {
            "+", "-", "*", "/", "^"
    };

    private static double X = 7.0;

    @RepeatedTest(value = 27)
    void fuzz() throws Exception {
        Random random = new SecureRandom();
        StringBuilder function = new StringBuilder();
        PrimitiveIterator.OfInt iit = random.ints(0, 100).iterator();
        PrimitiveIterator.OfDouble dit = random.doubles(0, 100).iterator();

        int count = 0;

        int first = random.nextInt(3);
        if (first == 0) {
            function.append('x');
        } else if (first == 1) {
            function.append(iit.nextInt());
        } else {
            function.append(dit.nextDouble());
        }

        int insanity = 0;
        do {

            int operator = random.nextInt(OPS.length);
            function.append(OPS[operator]);

            boolean mathf = random.nextBoolean();
            if (mathf) {
                int mf = random.nextInt(MATH_FUNCTIONS.length);
                function.append(MATH_FUNCTIONS[mf]);
                count++;
            }

            int n = random.nextInt(3);
            if (n == 0) {
                function.append('x');
            } else if (n == 1) {
                int number = iit.nextInt();
                if (number >= 0) {
                    function.append(number);
                } else {
                    function.append("(").append(number).append(")");
                }
            } else {
                double number = dit.nextDouble();
                if (number >= 0) {
                    function.append(number);
                } else {
                    function.append("(").append(number).append(")");
                }
            }

            while (count > 0) {
                if (iit.nextInt() % 2 == 0) {
                    break;
                }

                function.append(")");
                count--;
            }

            int stop = random.nextInt(10);
            if (stop < 3) {
                break;
            }

            insanity++;

        } while (insanity < 6);

        while (count > 0) {
            function.append(")");
            count--;
        }

        String f = function.toString();

        String request = f.replaceAll("\\+", "%2B")
                .replaceAll("/", "%2F")
                .replaceAll("\\^", "%5E");

        if (f.contains("x")) {
            request = request + "+where+x%3D" + X;
        }
        request = request.replaceAll("log\\(", "log10(");

        String doc = Jsoup.connect(WOLFRAM_API + request + WOLFRAM_API_CENTER + getPayload() + WOLFRAM_API_END)
                .ignoreContentType(true).referrer(WOLFRAM_API_REFERER + request).execute().body();

        try {
            double webResult = extractResult(doc);
            double result = testParser(f, X);
            if (webResult == webResult) {
                assertEquals(((double) Math.round(webResult * 1000)) / 1000, ((double) Math.round(result * 1000)) / 1000);
            } else {
                if (Double.isInfinite(webResult)) {
                    assertEquals(webResult, result);
                } else {
                    assertTrue(Double.isNaN(result));
                }
            }
        } catch (StackOverflowError ex) {
            // Blame wolfram for not giving exact results
        }
    }

    private boolean inexact = false;
    private double extractResult(String document) throws Exception {
        Any json = JsonIterator.deserialize(document).get("queryresult").get("pods");
        for (int i = 0; i < json.size(); i++) {
            Any title = json.get(i).get("title");
            if (title.toString().equals("Result") || title.toString().equals("Decimal approximation") || title.toString().equals("Decimal form")) {
                String plain = json.get(i).get("subpods").get(0).get("plaintext").toString()
                        .replace('×', '*')
                        .replaceAll("\\(result in radians\\)", "");

                if (plain.contains(" i")) {
                    return Double.NaN;
                }
                plain = plain.replaceAll("\\.\\.\\.", "")
                        .replaceAll("\n", "")
                        .replaceAll(" ", "");

                if (plain.contains("^")) {
                    if (this.inexact) {
                        throw new StackOverflowError();
                    }
                    String exact = Jsoup.connect(WOLFRAM_API + plain + WOLFRAM_API_CENTER + getPayload() + WOLFRAM_API_END)
                            .ignoreContentType(true).referrer(WOLFRAM_API_REFERER + plain).execute().body();
                    this.inexact = true;
                    return extractResult(exact);
                } else {
                    return Double.parseDouble(plain);
                }
            }
        }
        return Double.NaN;
    }

    private String getPayload() throws IOException {
        String doc = Jsoup.connect(WOLFRAM_PAYLOAD_ENDPOINT + System.currentTimeMillis())
                .ignoreContentType(true).execute().body();
        Any json = JsonIterator.deserialize(doc);
        String ts = json.get("code").toString();
        return ts;
    }

    private double testParser(String function, double x) throws Exception {
        INewtonFunction f = FunctionFlyWeightFactory.getFunction(function);
        return f.evaluate(x);
    }

}
