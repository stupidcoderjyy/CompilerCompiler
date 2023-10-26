package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.Config;
import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.generate.project.java.JClassGen;
import stupidcoder.generate.project.java.importParser.ImportParsingException;
import stupidcoder.generate.project.java.importParser.tokens.TokenId;

public class PropertyImport implements IProperty {
    private static final String internalPrefix = Config.getString(Config.GEN_PKG_PREFIX);
    private final JClassGen clazz;

    public PropertyImport(JClassGen clazz) {
        this.clazz = clazz;
    }

    //import → $import id . paths ;
    private void reduce0(
            PropertyTerminal p0,
            PropertyTerminal p1,
            PropertyTerminal p2,
            PropertyPaths p3,
            PropertyTerminal p4) {
        TokenId t1 = p1.getToken();
        String prefix = t1.lexeme;
        if (prefix.equals(internalPrefix)) {
            try {
                if (p3.isPkg) {
                    clazz.addPkgImport(p3.path.toString());
                } else {
                    clazz.addInternalClazzImport(p3.postfix);
                }
            } catch (Exception e) {
                throw new ImportParsingException(clazz.toString(), e);
            }
        } else {
            clazz.addExternalImport(prefix + "." + p3.path.toString());
        }
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
                (PropertyTerminal)properties[0],
                (PropertyTerminal)properties[1],
                (PropertyTerminal)properties[2],
                (PropertyPaths)properties[3],
                (PropertyTerminal)properties[4]
        );
    }
}
