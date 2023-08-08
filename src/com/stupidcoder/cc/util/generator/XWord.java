package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XWord extends XWritable{
    protected final String info;

    protected XWord(String info) {
        this.info = info;
    }

    public static XWord of(String info) {
        return new XWord(info);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writeIndentations(writer);
        writer.write(info);
    }
}
