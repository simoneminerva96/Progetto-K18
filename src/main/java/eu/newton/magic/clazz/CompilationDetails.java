package eu.newton.magic.clazz;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationDetails {
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private final String className;
    private final String sourceCode;
    private final String standardError;

    public CompilationDetails(String className, String sourceCode,
                              List<Diagnostic<? extends JavaFileObject>> diagnostics, String standardError) {
        this.diagnostics = diagnostics;
        this.className = className;
        this.sourceCode = sourceCode;
        this.standardError = standardError;

    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return Collections.unmodifiableList(this.diagnostics);
    }

    public String getClassName() {
        return this.className;
    }

    public String getSourceCode() {
        return this.sourceCode;
    }

    public String getStandardError() {
        return this.standardError;
    }

    @Override
    public String toString() {
        return String.format("Class compilation details:\n" +
                        "Class name: %s\n" +
                        "Class source:\n" +
                        "%s\n" +
                        "Compiler messages:\n" +
                        "%s\n" +
                        "Compiler standard error output:\n" +
                        "%s\n",
                this.className, this.sourceCode, diagnosticsListToString(this.diagnostics), this.standardError);
    }

    private static String diagnosticsListToString(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        return diagnostics.stream().map(CompilationDetails::diagnosticToString).collect(Collectors.joining("\n"));
    }

    private static String diagnosticToString(Diagnostic<?> diagnostic) {
        return String.format("%s: %s", diagnostic.getKind(), diagnostic.getMessage(null));
    }



}
