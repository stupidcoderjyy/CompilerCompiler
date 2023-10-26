package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertyScript implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 1 -> reduce0(
                    (PropertyBlock)properties[0]
            );
            case 2 -> reduce1(
                    (PropertyScript)properties[0],
                    (PropertyBlock)properties[1]
            );
        }
    }

    //script → block
    private void reduce0(
            PropertyBlock p0) {
        
    }

    //script → script block
    private void reduce1(
            PropertyScript p0,
            PropertyBlock p1) {
        
    }
}
