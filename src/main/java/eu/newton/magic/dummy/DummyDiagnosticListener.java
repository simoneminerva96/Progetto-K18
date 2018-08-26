package eu.newton.magic.dummy;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class DummyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {

    }

}
