package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;

public class XCommentable extends XWritable{
    private final XWritableList annotations = new XWritableList();

    public XCommentable appendComment(XWritable comment) {
        annotations.append(comment);
        return this;
    }

    @Override
    protected void setIndentations(int i) {
        super.setIndentations(i);
        annotations.setIndentations(i);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        for (XWritable c : annotations.getWritableList()) {
            c.output(writer);
        }
    }
}
