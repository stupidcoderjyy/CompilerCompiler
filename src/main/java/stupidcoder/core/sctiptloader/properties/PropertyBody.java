package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyBody implements IProperty {
    //body → slice
    private void reduce0(
            PropertySlice p0) {
        
    }

    //body → body | slice
    private void reduce1(
            PropertyBody p0,
            PropertyTerminal p1,
            PropertySlice p2) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 11 -> reduce0(
                    (PropertySlice)properties[0]
            );
            case 12 -> reduce1(
                    (PropertyBody)properties[0],
                    (PropertyTerminal)properties[1],
                    (PropertySlice)properties[2]
            );
        }
    }
}
