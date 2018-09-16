package eu.newton.util;

public final class MathHelper {

    public static double add(double a, double b) {
        if (a != a || b != b) {
            return Double.NaN;
        }
        if (Double.isInfinite(a) || Double.isInfinite(b)) {
            return a + b;
        }
        long temp = (Math.round(a * 10000)) + (Math.round(b * 10000));
        return ((double) temp) / 10000;
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    public static double pow(double a, double b, double c) {
        return Math.pow(a, Math.pow(b, c));
    }

    public static double pow(double a, double b, double c, double d) {
        return Math.pow(a, Math.pow(b, Math.pow(c, d)));
    }

    public static double pow(double... n) {
        double result = n[n.length - 1];
        for (int i = n.length - 2; i >= 0; i--) {
            result = Math.pow(n[i], result);
        }
        return result;
    }

}
