package stupidcoder.generate.generators.java;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class JClassHeadOut extends OutUnit {
    final Set<String> imports = new HashSet<>();
    final Set<String> externalImports = new HashSet<>();
    private final JProjectBuilder builder;
    private final JClassGen parent;
    private boolean written = false;

    JClassHeadOut(JProjectBuilder builder, JClassGen parent) {
        this.builder = builder;
        this.parent = parent;
    }

    @Override
    public void writeContentOnce(FileWriter writer, BufferedInput srcIn) throws Exception {
        if (!written) {
            writer.write("package " + parent.parent.pkgName + ";\r\n\r\n");
        }
        for (String s : imports) {
            writer.write("import ");
            if (s.startsWith("$")) {
                writer.write(builder.getRootPackage() + "." + s.substring(1) + ".*");
            } else {
                JClassGen clazz = builder.findClass(s);
                writer.write(clazz.parent.pkgName + "." + clazz.clazzName);
            }
            writer.write(";\r\n");
        }
        for (String s : externalImports) {
            writer.write("import ");
            writer.write(s);
            writer.write(";\r\n");
        }
        if (!imports.isEmpty() || !externalImports.isEmpty()) {
            writer.write("\r\n");
        }
        imports.clear();
        externalImports.clear();
        written = true;
    }

    @Override
    public String toString() {
        return "JClassHead: " + parent.parent.pkgName + "." + parent.clazzName;
    }
}
