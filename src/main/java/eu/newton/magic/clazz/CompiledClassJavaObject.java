package eu.newton.magic.clazz;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

public class CompiledClassJavaObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    CompiledClassJavaObject(String className) {
        super(URI.create("mem:///" + className + Kind.CLASS.extension), Kind.CLASS);
    }

    public byte[] getBytes() {
        return this.byteArrayOutputStream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
        return this.byteArrayOutputStream;
    }
}
