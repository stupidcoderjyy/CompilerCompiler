package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XWritableList extends XWritable{
    private final List<XWritable> writableList = new ArrayList<>();
    private int extraLineBreak = 1;
    private int finalLineBreak = 1;

    public XWritableList append(XWritable writable) {
        writableList.add(writable);
        return this;
    }

    public XWritableList append(String name) {
        writableList.add(new XWord(name));
        return this;
    }

    public XWritableList append(String format, Object ... args) {
        writableList.add(new XWord(String.format(format, args)));
        return this;
    }

    public XWritableList setExtraLineBreak(int count) {
        extraLineBreak = count;
        return this;
    }

    public XWritableList setFinalLineBreak(int count) {
        finalLineBreak = count;
        return this;
    }

    public XWritableList disableExtraLineBreak() {
        extraLineBreak = 0;
        return this;
    }

    public XWritableList disableFinalLineBreak() {
        finalLineBreak = 0;
        return this;
    }

    public List<XWritable> getWritableList() {
        return writableList;
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        int size = writableList.size();
        if (extraLineBreak > 0 && size > 1) {
            for (int i = 0 ; i < size - 1 ; i ++) {
                writableList.get(i).output(writer);
                writer.write("\r\n".repeat(extraLineBreak));
            }
            writableList.get(size - 1).output(writer);
        } else {
            for (XWritable xWritable : writableList) {
                xWritable.output(writer);
            }
        }
        if (finalLineBreak > 0 && size > 0) {
            writer.write("\r\n".repeat(finalLineBreak));
        }
    }

    @Override
    protected void setIndentations(int i){
        indentations = i;
        for (XWritable xWritable : writableList) {
            xWritable.setIndentations(i);
        }
    }
}
