package eu.newton.magic;

import eu.newton.magic.clazz.CompilationDetails;
import eu.newton.magic.dummy.DummyDiagnosticListener;
import eu.newton.magic.dummy.DummyWriter;
import eu.newton.magic.exceptions.ClassCompilationException;
import eu.newton.magic.clazz.ClassSourceJavaObject;
import eu.newton.magic.clazz.CompiledClassJavaObject;
import eu.newton.magic.clazz.MemoryFileManager;
import eu.newton.magic.exceptions.LambdaCreationException;

import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public final class LambdaFactory {

    private final LambdaClassLoader classLoader;
    private static final JavaCompiler JAVA_COMPILER = JavaCompilerProvider.findDefaultJavaCompiler();
    private int classCounter = 0;

    public static LambdaFactory get() {
        return new LambdaFactory();
    }

    private LambdaFactory() {
        this.classLoader = new LambdaClassLoader(new HashMap<>());
    }

    public DoubleUnaryOperator createLambda(String code) throws LambdaCreationException {
        String source = getClassSource(code);
        try {
            String name = "DummyClass" + this.classCounter;
            this.classCounter++;

            Map<String, CompiledClassJavaObject> compiledClass = compileClass(name, source);
            Class<?> clazz = this.classLoader.loadClass(name, compiledClass);


            Method m = clazz.getMethod("getLambda");
            DoubleUnaryOperator lambda = (DoubleUnaryOperator) m.invoke(null);
            return lambda;
        } catch (ReflectiveOperationException | RuntimeException | NoClassDefFoundError e) {
            throw new LambdaCreationException(e);
        } catch (ClassCompilationException ex) {
            throw new LambdaCreationException(ex);
        }
    }

    private String getClassSource(String lambda) {
        return
                "public final class DummyClass" + this.classCounter + '{' +
                    "public static final java.util.function.DoubleUnaryOperator getLambda() {" +
                        "return" + lambda + ';' +
                    "}" +
                "}";
    }

    private static final Writer stdErrWriter = new DummyWriter();
    private static final DiagnosticListener<JavaFileObject> diagnosticsCollector = new DummyDiagnosticListener();
    private Map<String, CompiledClassJavaObject> compileClass(String name, String source) throws ClassCompilationException {

        ClassSourceJavaObject classSourceObject = new ClassSourceJavaObject(name, source);

        try (MemoryFileManager stdFileManager = new MemoryFileManager(JAVA_COMPILER.getStandardFileManager(null, null, null))) {

            JavaCompiler.CompilationTask compilationTask = JAVA_COMPILER.getTask(null,
                    stdFileManager, null,
                    Arrays.asList("-target", "1.8", "-source", "1.8"), null, Collections.singletonList(classSourceObject));

            boolean success;
            try {
                success = compilationTask.call();
            } catch (Throwable t) {
                throw new ClassCompilationException(t.getCause());
            }

            if (!success) {
                throw new ClassCompilationException(
                        new CompilationDetails(name, source, ((DiagnosticCollector<JavaFileObject>) diagnosticsCollector).getDiagnostics(), stdErrWriter.toString()));
            }

            return stdFileManager.getClasses();
        }
    }

}
