package eu.newton.api;

public interface IExtrema<T> extends IFunction<T> {

    T getMaximum(T a, T b);

    T getMinimum(T a, T b);

}
