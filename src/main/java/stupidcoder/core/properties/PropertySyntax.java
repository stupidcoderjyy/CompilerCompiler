package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertySyntax implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 6 -> reduce0(
                    (PropertyProduction)properties[0]
            );
            case 7 -> reduce1(
                    (PropertySyntax)properties[0],
                    (PropertyProduction)properties[1]
            );
        }
    }

    //syntax → production
    private void reduce0(
            PropertyProduction p0) {
        
    }

    //syntax → syntax production
    private void reduce1(
            PropertySyntax p0,
            PropertyProduction p1) {
        
    }
}
