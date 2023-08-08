package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XAnnotation extends XWord {

    protected XAnnotation(String info) {
        super(info);
    }

    public static XAnnotation of(String a) {
        return new XAnnotation(a);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writeIndentations(writer);
        writer.write('@');
        writer.write(info);
        writer.write("\r\n");
    }
}
