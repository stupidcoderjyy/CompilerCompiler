package stupidcoder.fieldparser.internal.properties;

import stupidcoder.common.Production;
import stupidcoder.fieldparser.internal.IProperty;
import stupidcoder.fieldparser.internal.PropertyTerminal;

public class PropertyList implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
                (PropertyTerminal) properties[0],
                (PropertyStrings) properties[1],
                (PropertyTerminal) properties[2]
        );
    }

    private void reduce0(
            PropertyTerminal p0,
            PropertyStrings p1,
            PropertyTerminal p2) {

    }
}
