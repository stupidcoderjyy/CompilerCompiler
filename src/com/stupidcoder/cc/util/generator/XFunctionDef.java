package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XFunctionDef extends XCommentable {
    private final List<String> types = new ArrayList<>();
    private final List<String> names = new ArrayList<>();

    private String prefix;

    private int paraCount = 0;

    protected XFunctionDef(String prefix) {
        this.prefix = prefix;
    }

    public static XFunctionDef of(String prefix) {
        return new XFunctionDef(prefix);
    }

    public XFunctionDef appendParameter(String type, String name) {
        types.add(type);
        names.add(name);
        paraCount++;
        return this;
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        super.output(writer);
        writeIndentations(writer);
        writer.write(prefix);
        writer.write('(');
        if (paraCount > 0) {
            for (int i = 0 ; i < paraCount - 1 ; i ++) {
                writer.write(types.get(i));
                writer.write(' ');
                writer.write(names.get(i));
                writer.write(", ");
            }
            writer.write(types.get(paraCount - 1));
            writer.write(' ');
            writer.write(names.get(paraCount - 1));
        }
        writer.write(");");
    }
}
