package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyTokens implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 21 -> reduce0(
                    (PropertyToken)properties[0]
            );
            case 22 -> reduce1(
                    (PropertyTokens)properties[0],
                    (PropertyToken)properties[1]
            );
        }
    }

    //tokens → token
    private void reduce0(
            PropertyToken p0) {
        
    }

    //tokens → tokens token
    private void reduce1(
            PropertyTokens p0,
            PropertyToken p1) {
        
    }
}
