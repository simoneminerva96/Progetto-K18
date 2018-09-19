package eu.newton.magic.exceptions;

public class LambdaCreationException extends Exception {

    public LambdaCreationException(ClassCompilationException ex){
        super(ex);
    }

    public LambdaCreationException(Throwable t) {
        super(t);
    }

}
