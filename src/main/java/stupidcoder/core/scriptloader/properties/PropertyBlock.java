package stupidcoder.core.scriptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyBlock implements IProperty {
    //block → content blockEnd
    private void reduce0(
            PropertyContent p0,
            PropertyTerminal p1) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyContent)properties[0],
            (PropertyTerminal)properties[1]
        );
    }
}
