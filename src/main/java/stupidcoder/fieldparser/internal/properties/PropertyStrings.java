package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;
import stupidcoder.fieldparser.internal.TokenString;

public class PropertyStrings implements IProperty {

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 1 -> reduce0(
                    (PropertyTerminal<TokenString>) properties[0]
            );
            case 2 -> reduce1(
                    (PropertyStrings) properties[0],
                    (PropertyTerminal<TokenString>) properties[1]
            );
        }
    }

    private void reduce0(
            PropertyTerminal<TokenString> p0) {

    }

    private void reduce1(
            PropertyStrings p0,
            PropertyTerminal<TokenString> p1) {

    }
}
