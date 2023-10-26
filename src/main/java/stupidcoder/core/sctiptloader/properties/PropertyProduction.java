package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyProduction implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyHead)properties[0],
            (PropertyTerminal)properties[1],
            (PropertyBody)properties[2],
            (PropertyTerminal)properties[3]
        );
    }

    //production → head point body ;
    private void reduce0(
            PropertyHead p0,
            PropertyTerminal p1,
            PropertyBody p2,
            PropertyTerminal p3) {
        
    }
}
