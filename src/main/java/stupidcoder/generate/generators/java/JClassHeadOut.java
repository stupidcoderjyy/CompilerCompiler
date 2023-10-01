package stupidcoder.generate.generators.java;

import stupidcoder.generate.OutUnit;
import stupidcoder.util.input.BufferedInput;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class JClassHeadOut extends OutUnit {
    final Set<String> imports = new HashSet<>();
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
                JPkgGen pkg = builder.findPkg(s.substring(1));
                writer.write(pkg.pkgName + ".*");
            } else {
                JClassGen clazz = builder.findClass(s);
                writer.write(clazz.parent.pkgName + "." + clazz.clazzName);
            }
            writer.write(";\r\n");
        }
        imports.clear();
        written = true;
    }
}
