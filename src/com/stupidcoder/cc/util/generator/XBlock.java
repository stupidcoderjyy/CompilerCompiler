package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XBlock extends XWritable{
    private final XWritable content;

    private XBlock(XWritable content) {
        this.content = content;
    }

    public static XBlock of() {
        return new XBlock(XWritable.EMPTY);
    }

    public static XBlock of(XWritable content) {
        return new XBlock(content);
    }


    @Override
    protected void setIndentations(int i) {
        indentations = i;
        content.setIndentations(i + 1);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writer.write("{\r\n");
        content.output(writer);
        writeIndentations(writer);
        writer.write("}");
    }
}
