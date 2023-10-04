package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

public class PropertyBody implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 10 -> reduce0(
                    (PropertySlice)properties[0]
            );
            case 11 -> reduce1(
                    (PropertyBody)properties[0],
                    (PropertyTerminal)properties[1],
                    (PropertySlice)properties[2]
            );
        }
    }

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
}
