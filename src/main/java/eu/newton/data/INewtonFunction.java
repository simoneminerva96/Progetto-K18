package eu.newton.data;

import eu.newton.api.IDoubleDifferentiable;
import eu.newton.api.IDoubleExtrema;
import eu.newton.api.IDoubleZero;

public interface INewtonFunction extends IDoubleDifferentiable, IDoubleExtrema, IDoubleZero {
}
