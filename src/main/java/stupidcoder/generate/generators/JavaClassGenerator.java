package stupidcoder.generate.generators;

import stupidcoder.generate.Generator;
import stupidcoder.generate.SourceBuffered;

public abstract class JavaClassGenerator extends Generator {
    private final SourceBuffered srcInternalImport;
    private final SourceBuffered srcExternalImport;
    private final JavaProjectGenerator pGen;
    final String clazzName;
    final String childPkg;

    public JavaClassGenerator(JavaProjectGenerator pGen, String clazzName, String childPkg) {
        this(pGen, pGen, clazzName, childPkg);
    }

    public JavaClassGenerator(Generator parent, JavaProjectGenerator pGen, String clazzName, String childPkg) {
        super(parent, outFile(pGen.rootPkg, childPkg, clazzName), scriptPath(childPkg, clazzName));
        this.pGen = pGen;
        this.clazzName = clazzName;
        this.childPkg = childPkg;
        this.srcInternalImport = new SourceBuffered("internal_import");
        this.srcExternalImport = new SourceBuffered("external_import");
        pGen.registerClazz(this);
        SourceBuffered srcPkg = new SourceBuffered("pkg");
        srcPkg.writeString(pGen.rootPkg);
        srcPkg.writeString(childPkg);
        registerSrc(srcPkg);
        registerSrc(srcInternalImport);
        registerSrc(srcExternalImport);
    }

    public final void addInternalImport(JavaClassGenerator other) {
        ensureUnlocked();
        srcInternalImport.writeString(pGen.rootPkg);
        srcInternalImport.writeString(other.childPkg);
        srcInternalImport.writeString(other.clazzName);
    }

    public final void addExternalImport(String clazz) {
        ensureUnlocked();
        srcExternalImport.writeString(clazz);
    }

    private static String outFile(String rootPkg, String childPkg, String clazzName) {
        String res = rootPkg + '/' + childPkg + '/' + clazzName;
        res.replace('.', '/');
        res += ".java";
        return res;
    }

    private static String scriptPath(String childPkg, String clazzName) {
        String res = "scripts/" + childPkg + '/' + clazzName;
        res.replace('.', '/');
        res += ".java";
        return res;
    }

    @Override
    public String toString() {
        return "JavaClassGenerator(" + clazzName + ")";
    }
}
