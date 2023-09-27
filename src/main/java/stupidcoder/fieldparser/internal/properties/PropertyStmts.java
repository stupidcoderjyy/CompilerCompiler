package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;

public class PropertyStmts implements IProperty {

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (properties.length) {
            case 1 -> reduce0((PropertyStmt) properties[0]);
            case 2 -> reduce1((PropertyStmts) properties[0],
                    (PropertyStmt) properties[1]);
        }
    }

    private void reduce0(PropertyStmt p0) {

    }

    private void reduce1(PropertyStmts p0, PropertyStmt p1) {

    }
}
