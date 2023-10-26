package stupidcoder.generate.project.java;

import stupidcoder.generate.Generator;
import stupidcoder.util.input.CompilerInput;

import java.io.FileWriter;

public class JClassGen extends Generator {
    boolean excluded = false;
    final JProjectBuilder root;
    final JPkgGen parent;
    final String clazzName, outPath, scriptPath;
    final JClassHeadOut headOut;

    JClassGen(JProjectBuilder root, JPkgGen parent, String clazzName, String outPath, String scriptPath) {
        super(parent);
        this.clazzName = clazzName;
        this.parent = parent;
        this.root = root;
        this.outPath = outPath;
        this.scriptPath = scriptPath;
        this.headOut = new JClassHeadOut(this);
    }

    @Override
    public void loadScript(CompilerInput input, FileWriter writer) throws Exception {
        root.parser.run(input, this);
        headOut.writeContentOnce(writer, null);
        super.loadScript(input, writer);
    }

    public void gen() {
        if (excluded) {
            return;
        }
        loadScript(scriptPath, outPath);
    }

    public void addInternalClazzImport(String clazzName) {
        headOut.internalClazzImports.add(root.findClass(clazzName));
    }

    public void addPkgImport(String fullPkg) {
        headOut.pkgImports.add(root.findPkg(fullPkg));
    }

    public void addExternalImport(String pkgOrClazz) {
        headOut.externalImports.add(pkgOrClazz);
    }

    @Override
    public String toString() {
        return parent.pkgName + "." + clazzName;
    }
}
