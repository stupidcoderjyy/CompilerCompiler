package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertyTokens implements IProperty {
    //tokens → token
    private void reduce0(
            PropertyToken p0) {
        
    }

    //tokens → tokens token
    private void reduce1(
            PropertyTokens p0,
            PropertyToken p1) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 22 -> reduce0(
                    (PropertyToken)properties[0]
            );
            case 23 -> reduce1(
                    (PropertyTokens)properties[0],
                    (PropertyToken)properties[1]
            );
        }
    }
}
