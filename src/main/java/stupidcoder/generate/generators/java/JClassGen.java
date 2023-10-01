package stupidcoder.generate.generators.java;

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
        this.headOut = new JClassHeadOut(root, this);
        registerParser("head", new ParserJClassHead(this));
    }

    void addProjectImport(String clazzName) {
        headOut.imports.add(clazzName);
    }

    void addPkgImport(String name) {
        headOut.imports.add('$' + name);
    }

    @Override
    public void loadScript(CompilerInput input, FileWriter writer) throws Exception {
        headOut.writeContentOnce(writer, null);
        super.loadScript(input, writer);
    }

    public void gen() {
        if (excluded) {
            return;
        }
        loadScript(scriptPath, outPath);
    }
}
