package eu.newton.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.newton.magic.exceptions.LambdaCreationException;
import eu.newton.magic.LambdaFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

public class FunctionParser {

    private static final Logger logger = LogManager.getLogger(FunctionParser.class);

    private static final Pattern LN = Pattern.compile("\\bln\\b");
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
    private static final Pattern PI = Pattern.compile("\\bπ\\b");
    private static final Pattern E = Pattern.compile("\\be\\b");


    private static final LambdaFactory factory = LambdaFactory.get();

    public DoubleUnaryOperator parse(String function) throws LambdaCreationException, IllegalArgumentException {
        logger.trace("ORIGINAL: {}", function);

        function = Sanitizer.simplify(function);

        logger.trace("SIMPLIFIED: {}", function);

        List<String> groups = getGroups(function);

        logger.trace("THE LIST: {}", groups);

        function = parseGroups(groups);

        function = PI.matcher(function).replaceAll("java.lang.Math.PI");
        function = E.matcher(function).replaceAll("java.lang.Math.E");

        logger.trace("FINAL: {}", function);
        logger.trace("");

        DoubleUnaryOperator f = factory.createLambda("(x) -> " + function);

        return f;
    }

    private List<String> getGroups(String group) {
        List<String> groups = new ArrayList<>();
        int index;

        logger.trace("");
        logger.trace("Parsing: {}", group);
        do {
            index = getGroupIndex(group);
            String sub = group.substring(0, index);
            logger.trace("INDEX: {}", index);
            logger.trace("F: {}", sub);

            String parsed = parseGroup(sub);
            logger.trace("ADDING {}", parsed);
            groups.add(parsed);
            logger.trace("");

            group = group.substring(index);
        } while (!group.isEmpty());

        return groups;
    }

    private int getGroupIndex(String group) {
        logger.trace("Index of: {}", group);

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

        char opening = 0;
        char closing = 0;

        for (int i = 0; i < group.length(); i++) {
            c = group.charAt(i);

            if (opening == 0) {
                if (c == '(') {
                    opening = '(';
                    closing = ')';
                }
            }

            if (c == opening) {
                if (counter == -1) {
                    counter = 0;
                }
                counter++;
            } else if (c == closing) {
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
        logger.trace("");
        logger.trace("============BLOCK=============");

        char c;

        for (int i = 0; i < group.length(); i++) {
            c = group.charAt(i);

            if (c == '(') {
                String f = replaceMathFunctions(group.substring(0, i));
                logger.trace("MATHF: {}", f);
                String parsed = f + "(" + parseGroups(getGroups(group.substring(i + 1, group.length() - 1))) + ")";
                logger.trace("============BLOCK=============");
                logger.trace("");
                return parsed;
            }
        }

        throw new IllegalArgumentException("You're not supposed to be here");
    }

    private int powCounter = 0;
    private String parseGroups(List<String> groups) {
        logger.trace("");
        logger.trace("PARSING: {}", groups);

        String function;

        if (groups.size() != 1) {

            StringBuilder builder = new StringBuilder();

            String current = groups.get(0);
            String op = groups.get(1);

            for (int i = 2; i < groups.size(); i++) {

                logger.trace("CURRENT: {}", current);
                logger.trace("OP: {}", op);

                if (op.equals("^")) {
                    String to = groups.get(i);
                    logger.trace("TO: {}", to);
                    if (i < groups.size() - 1 && groups.get(i + 1).equals("^")) {
                        current = current + ',' + "java.lang.Math.pow(" + to;
                        op = groups.get(i + 1);
                        this.powCounter++;
                        i++;
                        continue;
                    } else {
                        current = "java.lang.Math.pow(" + current + ',' + to + ')';
                        for (int j = 0; j < this.powCounter; j++) {
                            current += ')';
                        }
                        if (i < groups.size() - 1) {
                            op = groups.get(i + 1);
                            i++;
                            continue;
                        } else {
                            builder.append(current);
                            break;
                        }

                    }

                } else if (op.equals("*") || op.equals("/")) {
                    String to = groups.get(i);
                    current = '(' + current + op + to + ')';
                    i++;
                    if (i < groups.size()) {
                        op = groups.get(i);
                        continue;
                    } else {
                        builder.append(current);
                        break;
                    }
                } else if (current.equals("x") || current.contains(".")) {
                    if (op.equals("+")) {
                        String to = groups.get(i);
                        logger.trace("TO: {}", to);
                        if (to.equals("x") || to.contains(".")) {
                            if (i < groups.size() - 1 && (groups.get(i + 1).equals("*") || groups.get(i + 1).equals("/") || groups.get(i + 1).equals("^"))) {
                                builder.append(current).append(op);
                            } else {
                                current = "eu.newton.util.MathHelper.add(" + current + "," + to + ")";
                                i++;
                                if (i < groups.size()) {
                                    op = groups.get(i);
                                    continue;
                                } else {
                                    builder.append(current);
                                    break;
                                }
                            }
                        } else {
                            builder.append(current).append(op);
                        }
                    } else if (op.equals("-")) {
                        String to = groups.get(i);
                        logger.trace("TO: {}", to);
                        if (to.equals("x") || to.contains(".")) {
                            if (i < groups.size() - 1 && (groups.get(i + 1).equals("*") || groups.get(i + 1).equals("/") || groups.get(i + 1).equals("^"))) {
                                builder.append(current).append(op);
                            } else {
                                current = "eu.newton.util.MathHelper.minus(" + current + "," + to + ")";
                                i++;
                                if (i < groups.size()) {
                                    op = groups.get(i);
                                    continue;
                                } else {
                                    builder.append(current);
                                    break;
                                }
                            }
                        } else {
                            builder.append(current).append(op);
                        }
                    } else {
                        builder.append(current).append(op);
                    }
                } else {
                    builder.append(current).append(op);
                }

                logger.trace("Builder: {} ", builder);

                if (i < groups.size() - 2) {
                    current = groups.get(i);
                    op = groups.get(i + 1);
                    i += 1;
                } else {
                    builder.append(groups.get(i));
                    break;
                }
            }

            function = builder.toString();

        } else {
            function = groups.get(0);
        }

        logger.trace("DONE: {}", function);
        logger.trace("");

        return function;
    }

    private String replaceMathFunctions(String input) {
        logger.trace("");
        logger.trace("ReplaceMath: {}", input);

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

        logger.trace("EndMath: {}", input);

        return input;
    }
}
