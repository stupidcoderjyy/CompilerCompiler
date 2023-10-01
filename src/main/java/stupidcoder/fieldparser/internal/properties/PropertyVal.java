package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;

public class PropertyVal implements IProperty {

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 4 -> reduce0(
                    (PropertyTerminal) properties[0]
            );
            case 5 -> reduce1(
                    (PropertyStrings) properties[0]
            );
        }
    }

    private void reduce0(PropertyTerminal p0) {

    }

    private void reduce1(PropertyStrings p0) {

    }
}
