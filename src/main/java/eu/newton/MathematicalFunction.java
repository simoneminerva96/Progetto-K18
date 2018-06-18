package eu.newton;

import javax.script.ScriptException;

public final class MathematicalFunction {

    private final String function;
    private final IMathFunction f;

    public MathematicalFunction(String function) throws ScriptException {
        this.f = new BetterParser().parse(function);
        this.function = function;
    }






    @Override
    public String toString() {
        return function;
    }
}
