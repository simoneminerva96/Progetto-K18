package eu.newton.magic.clazz;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class ClassSourceJavaObject extends SimpleJavaFileObject {

    private final String source;
    private final String name;

    public ClassSourceJavaObject(String name, String source) {
        super(URI.create("file:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
        this.source = source;
        this.name = name;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.source;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
