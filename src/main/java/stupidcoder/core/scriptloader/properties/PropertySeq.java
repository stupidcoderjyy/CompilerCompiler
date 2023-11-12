package stupidcoder.core.scriptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertySeq implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 16 -> reduce0(
                    (PropertySymbol)properties[0]
            );
            case 17 -> reduce1(
                    (PropertySeq)properties[0],
                    (PropertySymbol)properties[1]
            );
        }
    }

    //seq → symbol
    private void reduce0(
            PropertySymbol p0) {
        
    }

    //seq → seq symbol
    private void reduce1(
            PropertySeq p0,
            PropertySymbol p1) {
        
    }
}
