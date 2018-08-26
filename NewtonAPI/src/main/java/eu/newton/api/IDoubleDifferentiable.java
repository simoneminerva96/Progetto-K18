package eu.newton.api;

public interface IDoubleDifferentiable extends IDoubleFunction {

    double differentiate(double x, int grade);

}
