package eu.newton.util;

public final class MathHelper {

    public static double add(double a, double b) {
        int ad = (int) ((a - ((int) a)) * 10000);
        int bd = (int) ((b - ((int) b)) * 10000);
        return (((int) a) + ((int) b)) + (((double) (ad + bd)) / 10000);
    }

    public static double minus(double a, double b) {
        int ad = (int) ((a - ((int) a)) * 10000);
        int bd = (int) ((b - ((int) b)) * 10000);
        return (((int) a) - ((int) b)) + (((double) (ad - bd)) / 10000);
    }




}
