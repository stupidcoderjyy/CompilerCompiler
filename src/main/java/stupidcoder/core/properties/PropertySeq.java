package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertySeq implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 15 -> reduce0(
                    (PropertySymbol)properties[0]
            );
            case 16 -> reduce1(
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
