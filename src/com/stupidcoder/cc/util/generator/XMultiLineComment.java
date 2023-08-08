package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XMultiLineComment extends XWord {

    private final String[] comments;

    public XMultiLineComment(String info) {
        super(info);
        comments = info.split("\r\n");
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writeIndentations(writer);
        writer.write("/*\r\n");
        for (String s : comments) {
            writeIndentations(writer);
            writer.write(s);
            writer.write("\r\n");
        }
        writeIndentations(writer);
        writer.write("*/\r\n");
    }
}
