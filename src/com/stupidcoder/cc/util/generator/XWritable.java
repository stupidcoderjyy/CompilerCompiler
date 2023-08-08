package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public abstract class XWritable {

    public static final XWritable EMPTY = new XWritable() {
        @Override
        public void output(FileWriter writer) {
        }
    };

    protected int indentations = 0;

    public abstract void output(FileWriter writer) throws IOException;

    protected void setIndentations(int i) {
        this.indentations = i;
    }

    protected void writeIndentations(FileWriter writer) throws IOException {
        writer.write(" ".repeat(Math.max(0, 4 * indentations)));
    }
}
