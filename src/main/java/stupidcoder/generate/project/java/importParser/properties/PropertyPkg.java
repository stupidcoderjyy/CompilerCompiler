package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

public class PropertyPkg implements IProperty {
    //pkg → $package paths ;
    private void reduce0(
            PropertyTerminal p0,
            PropertyPaths p1,
            PropertyTerminal p2) {
        
    }

    //pkg → ε
    private void reduce1(
            PropertyTerminal p0) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 2 -> reduce0(
                    (PropertyTerminal)properties[0],
                    (PropertyPaths)properties[1],
                    (PropertyTerminal)properties[2]
            );
            case 3 -> reduce1(
                    (PropertyTerminal)properties[0]
            );
        }
    }
}
