package stupidcoder.generate.generators;

import stupidcoder.generate.Generator;

import java.util.HashMap;
import java.util.Map;

public class JavaProjectGenerator extends Generator {
    private final Map<String, Generator> generators = new HashMap<>();
    final String rootPkg;

    public JavaProjectGenerator(String rootPkg) {
        super(rootPkg.replace('.', '/'), "");
        this.rootPkg = rootPkg;
    }

    void registerClazz(JavaClassGenerator g) {
        generators.put(g.childPkg + '.' + g.clazzName, g);
    }

    @Override
    public void run() {
        generators.forEach((pkg, g) -> g.run());
    }
}
