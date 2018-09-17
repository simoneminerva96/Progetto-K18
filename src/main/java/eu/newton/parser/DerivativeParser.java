package eu.newton.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DerivativeParser {

    private static final Map<String, String> DERIVATIVES = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(FunctionParser.class);

    static {
        DERIVATIVES.put("ln(x)", "1/x");
        DERIVATIVES.put("log(x)", "(1/x)*0.4342944819");
        DERIVATIVES.put("sin(x)", "cos(x)");
        DERIVATIVES.put("cos(x)", "-sin(x)");
        DERIVATIVES.put("tan(x)", "1+tan(x)*tan(x)");
        DERIVATIVES.put("asin(x)", "1/(sqrt(1-x*x))");
        DERIVATIVES.put("acos(x)", "-1/(sqrt(1-x*x))");
        DERIVATIVES.put("atan(x)", "1/(1+x*x)");
        DERIVATIVES.put("sqrt(x)", "1/(2*sqrt(x))");
        DERIVATIVES.put("x", "1");
    }

    public String getDerivative(String function) throws IllegalArgumentException {
        final String original = function;
        LOGGER.trace("Original: {}", original);

        function = Sanitizer.simplify(function);

        LOGGER.trace("Simplified: {}", function);

        List<String> groups = getGroups(function);

        LOGGER.trace("Groups: {}", groups);

        function = parseGroups(groups);

        LOGGER.trace("Function: {}", function);

        return function;
    }

    private List<String> getGroups(String group) {
        List<String> groups = new ArrayList<>();
        int index;

        LOGGER.trace("");
        LOGGER.trace("Parsing: {}", group);
        do {
            index = getGroupIndex(group);
            String sub = group.substring(0, index);
            LOGGER.trace("Index: {}", index);
            LOGGER.trace("Adding {}", sub);
            groups.add(sub);
            LOGGER.trace("");

            group = group.substring(index);
        } while (!group.isEmpty());

        return groups;
    }

    private int getGroupIndex(String group) {
        LOGGER.trace("Index of: {}", group);

        char c = group.charAt(0);

        if (c == '*' || c == '/' || c == '+' || c == '-' || c == '^' || c == 'x' || c == 'e' || c == 'π') {
            return 1;
        }

        if (c >= '0' && c <= '9') {
            for (int i = 1; i < group.length(); i++) {
                char d = group.charAt(i);
                if (!(d >= '0' && d <= '9') && d != '.') {
                    return i;
                }
            }
            return group.length();
        }

        int counter = -1;

        for (int i = 0; i < group.length(); i++) {
            c = group.charAt(i);

            if (c == '(') {
                if (counter == -1) {
                    counter = 0;
                }
                counter++;
            } else if (c == ')') {
                counter--;
            }

            if (counter == 0) {
                return i + 1;
            }
        }

        throw new IllegalArgumentException("Dangling brace");

    }

    private String parseGroups(List<String> groups) {
        LOGGER.trace("");
        LOGGER.trace("Parsing: {}", groups);

        StringBuilder builder = new StringBuilder();

        String current;
        String op;


        for (int i = 0; i < groups.size();) {
            current = groups.get(i);
            LOGGER.trace("Argument 1: {} at {}", current, i);

            if (i == groups.size() - 1) {
                builder.append(differentiate(current));
                return builder.toString();
            }

            op = groups.get(++i);
            LOGGER.trace("Operator: {} at {}", op, i);

            LOGGER.trace("");

            if (op.equals("+") || op.equals("-")) {
                builder.append(differentiate(current)).append(op);
                continue;
            }

            if (op.equals("*") || op.equals("/")) {
                List<String> fucks = new ArrayList<>();

                for (int j = 0; j < groups.size(); j++) {

                    LOGGER.trace("Operator M: {} at {}", op, i);
                    LOGGER.trace("Adding C: {} at {}", current, i);
                    fucks.add(current);

                    current = groups.get(++i);

                    if (op.equals("/")) {
                        current = "1/" + current;
                    }

                    if (i == groups.size() - 1) {
                        break;
                    }

                    op = groups.get(++i);

                    if (!(op.equals("*") || op.equals("/"))) {
                        break;
                    }

                }

                fucks.add(current);

                builder.append(differentiateMultiply(fucks));

                if (i == groups.size() - 1) {
                    break;
                }

                i++;
            }

            if (op.equals("^")) {
                groups.set(++i, current + "^" + groups.get(i));
            }


            LOGGER.trace("");
            LOGGER.trace("Builder: {}", builder);

        }

        String function = builder.toString();

        LOGGER.trace("Done: {}", function);
        LOGGER.trace("");

        return function;
    }

    private String differentiate(String input) {
        LOGGER.trace("");
        LOGGER.trace("Derivative of {}", input);

        if (!input.contains("x")) {
            LOGGER.trace("0");
            return "0";
        }

        if (input.charAt(0) == '(' && input.charAt(input.length() -1) == ')') {
            List<String> l = getGroups(input.substring(1, input.length() - 1));
            return '(' + parseGroups(l) + ')';
        }

        String[] pow = input.split("\\^");

        if (pow.length != 1) {
            String start = pow[0];

            if (!start.contains("x")) {
                if (pow.length != 3) {
                    throw new IllegalArgumentException();
                }

                return input + "*ln(" + pow[0] + ")";
            } else {
                String g = input.substring(pow[0].length() + 1);

                return input + "*(" + differentiate(g) + "*ln(" + pow[0] + ")+" + g + "*(" + differentiate(pow[0]) + "/" + pow[0] + "))";
            }
        }

        String[] div = input.split("/");

        if (div.length == 2) {
            String f = div[0];
            String g = div[1];

            return "(" + differentiate(f) + "*" + g + "-" + f + "*" + differentiate(g) + ")/(" + g + "*" + g + ")";
        }

        String derivative = DERIVATIVES.get(input);

        LOGGER.trace("Is {}", derivative);
        LOGGER.trace("");
        if (derivative == null) {
            throw new IllegalArgumentException();
        }

        return derivative;
    }

    private String differentiateMultiply(List<String> fucks) {
        LOGGER.trace("");
        LOGGER.trace("Fucks {}", fucks);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fucks.size(); i++) {
            for (int j = 0; j < fucks.size(); j++) {
                if (i == j) {
                    builder.append(differentiate(fucks.get(i)));
                } else {
                    builder.append(fucks.get(j));
                }
                if (j != fucks.size() - 1) {
                    builder.append("*");
                }
            }
            if (i != fucks.size() - 1) {
                builder.append("+");
            }
        }
        LOGGER.trace("");
        return builder.toString();
    }

}


