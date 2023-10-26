package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertySlice implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertySeq)properties[0],
            (PropertyPriorityP)properties[1]
        );
    }

    //slice → seq priorityP
    private void reduce0(
            PropertySeq p0,
            PropertyPriorityP p1) {
        
    }
}
