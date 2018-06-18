package eu.newton.api;

@FunctionalInterface
public interface IFunction<T> {

    T evaluate(T x);

}
