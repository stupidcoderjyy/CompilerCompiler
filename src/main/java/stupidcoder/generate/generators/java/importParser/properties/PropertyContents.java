package stupidcoder.generate.generators.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyContents implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 1 -> reduce0(
                    (PropertyImport)properties[0]
            );
            case 2 -> reduce1(
                    (PropertyContents)properties[0],
                    (PropertyImport)properties[1]
            );
        }
    }

    //contents → import
    private void reduce0(
            PropertyImport p0) {
        
    }

    //contents → contents import
    private void reduce1(
            PropertyContents p0,
            PropertyImport p1) {
        
    }
}
