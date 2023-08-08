package com.stupidcoder.cc.util.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XClass extends XCommentable {
    protected final String childPackage;
    protected final String name;

    private final XWritable prefix;
    private String fullPackage;
    private final XBlock block;

    private final XWritableList listImport = new XWritableList().setFinalLineBreak(2);

    /**
     * 内部的导入，在生成类文件过程中需要加上前缀
     * @see CodeWriter
     */
    protected final List<String> internalImportList = new ArrayList<>();

    private final XWritableList fields = new XWritableList();
    private final XWritableList functions = new XWritableList().setExtraLineBreak(2);

    private XClass(String childPackage, String type, String name, String suffix) {
        XWritableList list = new XWritableList().disableFinalLineBreak();
        this.name = name;
        this.childPackage = childPackage;
        this.prefix = XWord.of("public " + type +  " " + name + " " + suffix);
        list.append(fields);
        list.append(functions);
        block = XBlock.of(list);
    }

    public static XClass of(String name, String childPackage, String type, String suffix) {
        return new XClass(childPackage, type, name, suffix);
    }

    public static XClass of(String name, String childPackage, String type) {
        return new XClass(childPackage, type, name, "");
    }

    public static XClass of(String name, String childPackage) {
        return new XClass(childPackage, "class", name, "");
    }

    public static XClass of(String name) {
        return new XClass("", "class", name, "");
    }

    public XClass add3rdPartyImport(String pkg) {
        listImport.append("import " + pkg + ";");
        return this;
    }

    public XClass addInternalImport(String pkg) {
        internalImportList.add(pkg);
        return this;
    }

    public XClass field(XWritable field) {
        fields.append(field);
        return this;
    }

    public XClass field(String field) {
        fields.append(XWord.of(field));
        return this;
    }

    public XClass function(XWritable function) {
        functions.append(function);
        return this;
    }

    @Override
    protected void setIndentations(int i) {
        super.setIndentations(i);
        prefix.setIndentations(indentations);
        block.setIndentations(indentations);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        if (functions.getWritableList().size() == 0) {
            fields.disableFinalLineBreak();
        }
        writer.write(fullPackage);
        listImport.output(writer);
        //类注释
        super.output(writer);
        //类内容
        setIndentations(indentations);
        writeIndentations(writer);
        prefix.output(writer);
        block.output(writer);
    }

    /**
     * 设置类文件所在的包名，由{@link CodeWriter}调用<p>
     * 会同步生成设置包名和导入的代码
     * @param root 根路径（如：com.stupidcoder.compiler）
     */
    protected void setClazzPackage(String root) {
        if (childPackage.isEmpty()) {
            fullPackage = "package " + root + ";\r\n\r\n";
        } else {
            fullPackage = "package " + root + "." + childPackage + ";\r\n\r\n";
        }

        for (String s : internalImportList) {
            listImport.append("import " + root + '.' + s + ";");
        }
    }
}
