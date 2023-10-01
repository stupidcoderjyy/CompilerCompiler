package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;

public class PropertyStmt implements IProperty {

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
                (PropertyTerminal) properties[0],
                (PropertyVal) properties[1]
        );
    }

    private void reduce0(
            PropertyTerminal p0,
            PropertyVal p1) {

    }
}
