package stupidcoder.generate.project.java;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class JClassHeadOut extends OutUnit {
    List<JClassGen> internalClazzImports = new ArrayList<>();
    List<JPkgGen> pkgImports = new ArrayList<>();
    List<String> externalImports = new ArrayList<>();
    private final JClassGen parent;

    JClassHeadOut(JClassGen parent) {
        this.parent = parent;
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        writer.write("package " + parent.parent.pkgName + ";\r\n\r\n");
        for (JClassGen clazz : internalClazzImports) {
            writer.write("import ");
            writer.write(clazz.parent.pkgName + "." + clazz.clazzName);
            writer.write(";\r\n");
        }
        for (JPkgGen p : pkgImports) {
            writer.write("import ");
            writer.write(p.pkgName);
            writer.write(".*;\r\n");
        }
        for (String s : externalImports) {
            writer.write("import ");
            writer.write(s);
            writer.write(";\r\n");
        }
        if (!internalClazzImports.isEmpty() || !externalImports.isEmpty()) {
            writer.write("\r\n");
        }
        internalClazzImports = null;
        pkgImports = null;
        externalImports = null;
    }

    @Override
    public String toString() {
        return "JClassHead: " + parent.parent.pkgName + "." + parent.clazzName;
    }
}
