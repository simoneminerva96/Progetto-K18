package eu.newton.magic.exceptions;

import eu.newton.magic.clazz.CompilationDetails;

import java.util.Optional;

public class LambdaCreationException extends Exception {
    private final CompilationDetails compilationDetails;

    public LambdaCreationException(ClassCompilationException ex){
        super(ex);
        this.compilationDetails = ex.getCompilationDetails();
    }

    public LambdaCreationException(Throwable t) {
        super(t);
        this.compilationDetails = null;
    }

    public Optional<CompilationDetails> getCompilationDetails() {
        return Optional.ofNullable(this.compilationDetails);
    }
}
