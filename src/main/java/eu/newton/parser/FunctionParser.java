package eu.newton.parser;

import eu.newton.data.ConstantFunction;
import eu.newton.data.INewtonFunction;
import eu.newton.data.MathFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.magic.LambdaFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

class FunctionParser {

    private static final Logger LOGGER = LogManager.getLogger(FunctionParser.class);

    private static final Pattern LN = Pattern.compile("\\bln\\b");
    private static final Pattern LOG = Pattern.compile("\\blog\\b");
    private static final Pattern SIN = Pattern.compile("\\bsin\\b");
    private static final Pattern COS = Pattern.compile("\\bcos\\b");
    private static final Pattern TAN = Pattern.compile("\\btan\\b");
    private static final Pattern ASIN = Pattern.compile("\\basin\\b");
    private static final Pattern ACOS = Pattern.compile("\\bacos\\b");
    private static final Pattern ATAN = Pattern.compile("\\batan\\b");
    private static final Pattern SINH = Pattern.compile("\\bsinh\\b");
    private static final Pattern COSH = Pattern.compile("\\bcosh\\b");
    private static final Pattern TANH = Pattern.compile("\\btanh\\b");
    private static final Pattern SQRT = Pattern.compile("\\bsqrt\\b");

    private static final Pattern DOUBLE = Pattern.compile("(\\d+(\\.\\d+)?)");
    private static final Pattern INT = Pattern.compile("(\\d+)/");
    private static final Pattern PI = Pattern.compile("\\bπ\\b");
    private static final Pattern E = Pattern.compile("\\be\\b");

    private static final LambdaFactory factory = LambdaFactory.get();

    INewtonFunction parse(String function) throws LambdaCreationException, IllegalArgumentException {
        final String original = function;
        LOGGER.trace("Original: {}", original);

        function = Sanitizer.simplify(function);

        LOGGER.trace("Simplified: {}", function);

        List<String> groups = getGroups(function);

        LOGGER.trace("Groups: {}", groups);

        function = parseGroups(groups);

        function = PI.matcher(function).replaceAll("java.lang.Math.PI");
        function = E.matcher(function).replaceAll("java.lang.Math.E");
        function = INT.matcher(function).replaceAll("$1*1.0/");

        LOGGER.trace("Function: {}", function);

        DoubleUnaryOperator f = factory.createLambda("(x) -> " + function);

        if (!original.contains("x")) {
            return new ConstantFunction(f.applyAsDouble(0), original);
        }

        return new MathFunction(f, original);
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
            LOGGER.trace("Group: {}", sub);

            String parsed = parseGroup(sub);
            LOGGER.trace("Adding {}", parsed);
            groups.add(parsed);
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

    private String parseGroup(String group) {
        if (group.length() == 1) {
            return group;
        }

        char c = group.charAt(0);

        if (c == '(' && group.charAt(group.length() - 1) == ')') {
            List<String> l = getGroups(group.substring(1, group.length() - 1));
            return '(' + parseGroups(l) + ')';
        }

        if (c != 'x' && Character.isLetter(c)) {
            return parseNestedGroups(group);
        }

        if (DOUBLE.matcher(group).find()) {
            return group;
        }

        throw new IllegalArgumentException("Unsupported group");

    }

    private String parseNestedGroups(String group) {
        LOGGER.trace("");
        LOGGER.trace("============BLOCK=============");

        char c;

        for (int i = 0; i < group.length(); i++) {
            c = group.charAt(i);

            if (c == '(') {
                String f = replaceMathFunctions(group.substring(0, i));
                LOGGER.trace("Math Function: {}", f);
                String parsed = f + "(" + parseGroups(getGroups(group.substring(i + 1, group.length() - 1))) + ")";
                LOGGER.trace("============BLOCK=============");
                LOGGER.trace("");
                return parsed;
            }
        }

        throw new IllegalArgumentException("You're not supposed to be here");
    }

    private String parseGroups(List<String> groups) {
        LOGGER.trace("");
        LOGGER.trace("Parsing: {}", groups);

        if (groups.size() == 1) {
            LOGGER.trace("DONE: {} ", groups.get(0));
            LOGGER.trace("");
            return groups.get(0);
        }

        StringBuilder builder = new StringBuilder();

        String current;
        String op;
        String to;

        parsing:
        for (int i = 0; i < groups.size() - 1;) {
            current = groups.get(i);
            LOGGER.trace("Argument 1: {} at {}", current, i);

            op = groups.get(++i);
            LOGGER.trace("Operator: {} at {}", op, i);

            to = groups.get(++i);
            LOGGER.trace("Argument 2: {} at {}", to, i);

            if (op.equals("^")) {
                if (i == groups.size() - 1) {
                    builder.append("java.lang.Math.pow(").append(current).append(",").append(to).append(")");
                    break;
                }

                StringBuilder pow = new StringBuilder();
                pow.append("eu.newton.util.MathHelper.pow(").append(current).append(",").append(to);

                while (i < groups.size()) {
                    op = groups.get(++i);
                    LOGGER.trace("Operator: {} at {}", op, i);

                    if (!op.equals("^")) {
                        i++;
                        break;
                    }

                    pow.append(",").append(groups.get(++i));
                    if (i == groups.size() - 1) {
                        pow.append(")");
                        builder.append(pow.toString());
                        break parsing;
                    }
                }
                pow.append(")");
                current = pow.toString();

            } else if ((current.equals("x") || current.contains(".")) && (op.equals("+") || op.equals("-")) && (to.equals("x") || to.contains("."))) {
                if (i < 3 || groups.get(i - 3).equals("+")) {
                    to = op.equals("+") ? to : "-" + to;
                    if (i == groups.size() - 1) {
                        builder.append("eu.newton.util.MathHelper.add(").append(current).append(",").append(to).append(")");
                        break;
                    }

                    String nextOp = groups.get(i + 1);
                    if (nextOp.equals("+") || nextOp.equals("-")) {
                        current = "eu.newton.util.MathHelper.add(" + current + "," + to + ")";
                    }
                }
            }

            builder.append(current).append(op);

            LOGGER.trace("Builder : {} ", builder);

            if (i == groups.size() - 1) {
                builder.append(to);
                break;
            }
        }

        String function = builder.toString();

        LOGGER.trace("Done: {}", function);
        LOGGER.trace("");

        return function;
    }

    private String replaceMathFunctions(String input) {
        LOGGER.trace("");
        LOGGER.trace("Replacing Math Function: {}", input);

        input = LOG.matcher(input).replaceAll("java.lang.Math.log10");
        input = LN.matcher(input).replaceAll("java.lang.Math.log");
        input = SIN.matcher(input).replaceAll("java.lang.Math.sin");
        input = COS.matcher(input).replaceAll("java.lang.Math.cos");
        input = TAN.matcher(input).replaceAll("java.lang.Math.tan");
        input = ASIN.matcher(input).replaceAll("java.lang.Math.asin");
        input = ACOS.matcher(input).replaceAll("java.lang.Math.acos");
        input = ATAN.matcher(input).replaceAll("java.lang.Math.atan");
        input = SINH.matcher(input).replaceAll("java.lang.Math.sinh");
        input = COSH.matcher(input).replaceAll("java.lang.Math.cosh");
        input = TANH.matcher(input).replaceAll("java.lang.Math.tanh");
        input = SQRT.matcher(input).replaceAll("java.lang.Math.sqrt");

        LOGGER.trace("Replaced: {}", input);

        return input;
    }
}
