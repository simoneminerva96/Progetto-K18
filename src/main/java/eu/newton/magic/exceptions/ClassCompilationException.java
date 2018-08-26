package eu.newton.magic.exceptions;

import eu.newton.magic.clazz.CompilationDetails;

public class ClassCompilationException extends Exception {

    private final CompilationDetails compilationDetails;

    public ClassCompilationException(CompilationDetails compilationDetails) {
        super(compilationDetails.toString());
        this.compilationDetails = compilationDetails;
    }

    public ClassCompilationException(Throwable t) {
        super(t);
        this.compilationDetails = null;
    }

    public CompilationDetails getCompilationDetails() {
        return this.compilationDetails;
    }


}
