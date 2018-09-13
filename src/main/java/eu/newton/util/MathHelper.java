package eu.newton.util;

public final class MathHelper {

    public static double add(double a, double b) {
        long temp = ((long) (a * 10000)) + ((long) (b * 10000));
        return ((double) temp) / 10000;
    }

}
