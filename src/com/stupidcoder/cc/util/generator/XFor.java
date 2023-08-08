package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XFor extends XWritable{
    private final XWritable expr1;
    private final XWritable expr2;
    private final XWritable expr3;
    private final XWritableList blockContent = new XWritableList();
    private final XBlock block = XBlock.of(blockContent);

    private boolean oldFor;

    public XFor(XWritable expr1, XWritable expr2, XWritable expr3) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
        oldFor = true;
    }

    public XFor(XWritable def, XWritable collection) {
        this.expr1 = def;
        this.expr2 = collection;
        this.expr3 = null;
        oldFor = false;
    }

    public static XFor of(XWritable expr1, XWritable expr2, XWritable expr3) {
        return new XFor(expr1, expr2, expr3);
    }

    public static XFor of(String expr1, String expr2, String expr3) {
        return new XFor(XWord.of(expr1), XWord.of(expr2), XWord.of(expr3));
    }

    public static XFor of(XWritable def, XWritable collection) {
        return new XFor(def, collection);
    }

    public XFor addContent(XWritable content) {
        blockContent.append(content);
        return this;
    }

    public XFor addContent(String content) {
        blockContent.append(content);
        return this;
    }

    public XFor addContent(String format, Object ... args) {
        blockContent.append(String.format(format, args));
        return this;
    }


    @Override
    protected void setIndentations(int i) {
        this.indentations = i;
        block.setIndentations(i);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        writeIndentations(writer);
        writer.write("for (");
        expr1.output(writer);
        if (oldFor) {
            writer.write(" ; ");
            expr2.output(writer);
            writer.write(" ; ");
            expr3.output(writer);
            writer.write(") ");
        } else {
            writer.write(" : ");
            expr2.output(writer);
        }
        block.output(writer);
    }
}
