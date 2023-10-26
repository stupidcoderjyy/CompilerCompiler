package stupidcoder.core.sctiptloader.properties;

import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.sctiptloader.tokens.TokenId;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyHead implements IProperty {
    private final SyntaxLoader loader;

    public PropertyHead(SyntaxLoader loader) {
        this.loader = loader;
    }
    //head → id
    private void reduce0(
            PropertyTerminal p0) {
        TokenId t0 = p0.getToken();
        loader.beginProd(t0.lexeme);
    }

    //head → endHead
    private void reduce1(
            PropertyTerminal p0) {
        loader.beginEndSymbolsCustomize();
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 9 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 10 -> reduce1(
                    (PropertyTerminal)properties[0]
            );
        }
    }
}
