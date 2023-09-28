package stupidcoder.generate.generators.java;

import stupidcoder.Config;
import stupidcoder.generate.Generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JPkgGen extends Generator {
    String pkgName;
    String outPath;
    boolean excluded = false;
    final Map<String, JPkgGen> childPackages = new HashMap<>();
    final Map<String, JClassGen> targets = new HashMap<>();
    final JPkgGen parent;

    JPkgGen(JPkgGen parent) {
        super(parent);
        this.parent = parent;
    }

    void gen() {
        if (excluded) {
            return;
        }
        new File(Config.outputPath(outPath)).mkdirs();
        targets.forEach((n, g) -> g.gen());
        childPackages.forEach((n, g) -> g.gen());
    }
}
