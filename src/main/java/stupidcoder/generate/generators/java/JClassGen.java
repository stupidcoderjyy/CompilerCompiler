package stupidcoder.generate.generators.java;

import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;
import stupidcoder.util.input.CompilerInput;

import java.io.FileWriter;

public class JClassGen extends Generator {
    boolean excluded = false;
    final JProjectBuilder root;
    final JPkgGen parent;
    final String clazzName, outPath, scriptPath;
    final SourceCached headSrc = new SourceCached("clazzHead");

    JClassGen(JProjectBuilder root, JPkgGen parent, String clazzName, String outPath, String scriptPath) {
        super(parent);
        this.clazzName = clazzName;
        this.parent = parent;
        this.root = root;
        this.outPath = outPath;
        this.scriptPath = scriptPath;
        registerSrc(headSrc);
        headSrc.writeString(parent.pkgName);
    }

    public void addProjectImport(String clazzName) {
        JClassGen res = root.findClass(clazzName);
        headSrc.writeString(res.parent.pkgName);
        headSrc.writeString(res.clazzName);
    }

    @Override
    public void loadScript(CompilerInput input, FileWriter writer) throws Exception {
        try (CompilerInput headInput = CompilerInput.fromResource(
                "/scripts/internal/genjava/classTitle.txt")){
            super.loadScript(headInput, writer);
            super.loadScript(input, writer);
        }
    }

    public void gen() {
        if (excluded) {
            headSrc.close();
            return;
        }
        loadScript(scriptPath, outPath);
        headSrc.close();
    }
}
