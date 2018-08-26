package eu.newton.magic;

import eu.newton.magic.clazz.CompiledClassJavaObject;

import java.util.Map;

public class LambdaClassLoader extends ClassLoader {

    private final Map<String, CompiledClassJavaObject> classes;

    public LambdaClassLoader(Map<String, CompiledClassJavaObject> classes) {
        this.classes = classes;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.charAt(0) == 'D') {
            return findClass(name);
        }
        return LambdaClassLoader.class.getClassLoader().loadClass(name);

    }

    public Class<?> loadClass(String fullClassName, Map<String, CompiledClassJavaObject> compiledClassesBytes) throws ClassNotFoundException {
        this.classes.put(compiledClassesBytes.keySet().iterator().next(), compiledClassesBytes.values().iterator().next());
        return loadClass(fullClassName);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        CompiledClassJavaObject clazz = this.classes.get(name);
        if (clazz != null) {
            byte[] classBytes = clazz.getBytes();
            return defineClass(name, classBytes, 0, classBytes.length);
        } else {
            throw new ClassNotFoundException("Class " + name  + " could not be found in the LambdaClassLoader.");
        }
    }

}
