package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;
import stupidcoder.fieldparser.internal.TokenId;

public class PropertyStmt implements IProperty {

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0((PropertyTerminal<TokenId>) properties[0],
                (PropertyVal) properties[1]);
    }

    private void reduce0(PropertyTerminal<TokenId> p0, PropertyVal p1) {

    }
}
