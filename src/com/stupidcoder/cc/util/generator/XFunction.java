package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XFunction extends XCommentable {
    private XWritable prefix = EMPTY;
    private final XWritableList argsType = new XWritableList();
    private final XWritableList argsName = new XWritableList();

    private final XWritableList blockContent = new XWritableList();
    private final XBlock block = XBlock.of(blockContent);

    private XFunction() {
    }

    public static XFunction of(String prefix) {
        XFunction f = new XFunction();
        f.prefix = new XWord(prefix);
        return f;
    }

    public XFunction arg(String type, String name) {
        argsType.append(type);
        argsName.append(name);
        return this;
    }

    public XFunction addContent(XWritable writable) {
        blockContent.append(writable);
        return this;
    }

    public XFunction addContent(String content) {
        blockContent.append(content);
        return this;
    }

    public XFunction addContent(String format, Object ... args) {
        blockContent.append(String.format(format, args));
        return this;
    }

    @Override
    protected void setIndentations(int i) {
        super.setIndentations(i);
        this.indentations = i;
        block.setIndentations(i);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        super.output(writer);
        writeIndentations(writer);
        prefix.output(writer);
        writer.write('(');
        List<XWritable> types = argsType.getWritableList();
        List<XWritable> names = argsName.getWritableList();
        for (int i = 0; i < types.size(); i ++) {
            types.get(i).output(writer);
            writer.write(' ');
            names.get(i).output(writer);
            if (i < types.size() - 1) {
                writer.write(", ");
            }
        }
        writer.write(") ");
        block.output(writer);
    }
}
