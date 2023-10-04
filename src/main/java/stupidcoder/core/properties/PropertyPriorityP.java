package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.tokens.TokenPriorityMarkProd;

public class PropertyPriorityP implements IProperty {
    private final SyntaxLoader loader;

    public PropertyPriorityP(SyntaxLoader loader) {
        this.loader = loader;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 13 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 14 -> reduce1(
                    (PropertyTerminal)properties[0]
            );
        }
    }

    //priorityP → ε
    private void reduce0(
            PropertyTerminal p0) {
        loader.finish();
    }

    //priorityP → pMarkP
    private void reduce1(
            PropertyTerminal p0) {
        TokenPriorityMarkProd t0 = p0.getToken();
        loader.finish(t0.value);
    }
}
