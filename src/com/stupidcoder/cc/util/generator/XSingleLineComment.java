package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XSingleLineComment extends XWord {

    protected XSingleLineComment(String info) {
        super(info);
    }

    public static XSingleLineComment of(String c) {
        return new XSingleLineComment(c);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writeIndentations(writer);
        writer.write("//");
        writer.write(info);
        writer.write("\r\n");
    }
}
