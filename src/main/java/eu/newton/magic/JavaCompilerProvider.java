package eu.newton.magic;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

final class JavaCompilerProvider {

    static JavaCompiler findDefaultJavaCompiler() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            compiler = new EclipseCompiler();
        }

        return compiler;
    }
}
