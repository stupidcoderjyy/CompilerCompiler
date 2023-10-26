package stupidcoder.generate.project.java;

import stupidcoder.Config;
import stupidcoder.generate.Generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JPkgGen extends Generator {
    final String pkgName;
    final String outPath;
    boolean excluded = false;
    final Map<String, JPkgGen> childPackages = new HashMap<>();
    final Map<String, JClassGen> targets = new HashMap<>();
    final JPkgGen parent;

    JPkgGen(JPkgGen parent, String dirName) {
        super(parent);
        this.parent = parent;
        this.pkgName = parent.pkgName + "." + dirName;
        this.outPath = parent.outPath + "/" + dirName;
    }

    JPkgGen(String rootPkg) {
        this.parent = null;
        this.pkgName = rootPkg;
        this.outPath = rootPkg.replace('.', '/');
    }

    void gen() {
        if (excluded) {
            return;
        }
        new File(Config.outputPath(outPath)).mkdirs();
        targets.forEach((n, g) -> g.gen());
        childPackages.forEach((n, g) -> g.gen());
    }

    @Override
    public String toString() {
        return pkgName;
    }
}
