package eu.newton.magic.clazz;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> implements StandardJavaFileManager {

    public MemoryFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    private final Map<String, CompiledClassJavaObject> classes = new HashMap<>(1);


    public Map<String,CompiledClassJavaObject> getClasses(){
        return this.classes;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            CompiledClassJavaObject clazz = this.classes.get(className);
            if (clazz == null) {
                clazz = new CompiledClassJavaObject(className);
                this.classes.put(className, clazz);
            }
            return clazz;
        }
        throw new IOException(this.getClass().getSimpleName() + " cannot open files for writing. " +
                "Only .class output is supported and stored in memory.");
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files) {
        return this.fileManager.getJavaFileObjectsFromFiles(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(File... files) {
        return this.fileManager.getJavaFileObjects(files);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names) {
        return this.fileManager.getJavaFileObjectsFromStrings(names);
    }

    @Override
    public Iterable<? extends JavaFileObject> getJavaFileObjects(String... names) {
        return this.fileManager.getJavaFileObjects(names);
    }

    @Override
    public void setLocation(Location location, Iterable<? extends File> path) throws IOException {
        this.fileManager.setLocation(location, path);
    }

    @Override
    public Iterable<? extends File> getLocation(Location location) {
        return this.fileManager.getLocation(location);
    }
}
