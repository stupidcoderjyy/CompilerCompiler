package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

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
