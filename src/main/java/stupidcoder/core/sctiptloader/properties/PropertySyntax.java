package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertySyntax implements IProperty {
    //syntax → syntax production
    private void reduce0(
            PropertySyntax p0,
            PropertyProduction p1) {

    }

    //syntax → production
    private void reduce1(
            PropertyProduction p0) {

    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 6 -> reduce0(
                    (PropertySyntax)properties[0],
                    (PropertyProduction)properties[1]
            );
            case 7 -> reduce1(
                    (PropertyProduction)properties[0]
            );
        }
    }
}
