package stupidcoder.generate.generators.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.generate.generators.java.JClassGen;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.generators.java.importParser.tokens.TokenId;

public class PropertyImport implements IProperty {
    private final JProjectBuilder builder;
    private final JClassGen clazz;

    public PropertyImport(JProjectBuilder builder, JClassGen gen) {
        this.builder = builder;
        this.clazz = gen;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyTerminal)properties[0],
            (PropertyTerminal)properties[1],
            (PropertyTerminal)properties[2],
            (PropertySeq)properties[3],
            (PropertyTerminal)properties[4]
        );
    }

    //import → id id . seq ;
    private void reduce0(
            PropertyTerminal p0,
            PropertyTerminal p1,
            PropertyTerminal p2,
            PropertySeq p3,
            PropertyTerminal p4) {
        TokenId t1 = p1.getToken();
        String path = p3.path.toString();
        if (!t1.lexeme.equals(builder.getRootPackage())) {
            clazz.addExternalImport(t1.lexeme + "." + path);
            return;
        }
        if (p3.isPkg) {
            clazz.addPkgImport(path.replace(".*", ""));
        } else {
            clazz.addClazzImport(path);
        }
    }
}
