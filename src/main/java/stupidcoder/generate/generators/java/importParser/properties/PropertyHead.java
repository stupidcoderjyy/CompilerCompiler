package stupidcoder.generate.generators.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

public class PropertyHead implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyContents)properties[0],
            (PropertyTerminal)properties[1]
        );
    }

    //head → contents id
    private void reduce0(
            PropertyContents p0,
            PropertyTerminal p1) {
        
    }
}
