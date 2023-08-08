package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XField extends XCommentable{
    private XWritable prefix = EMPTY;
    private XWritable initializer = EMPTY;

    private XField() {
    }

    public static XField of(String prefix) {
        XField f = new XField();
        f.prefix = new XWord(prefix);
        return f;
    }

    public XField setInitializer(XWritable initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        super.output(writer);
        writeIndentations(writer);
        prefix.output(writer);
        writer.write(" = ");
        initializer.output(writer);
        writer.write(";");
    }
}
