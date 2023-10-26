package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyImportList implements IProperty {
    //importList → import
    private void reduce0(
            PropertyImport p0) {
        
    }

    //importList → importList import
    private void reduce1(
            PropertyImportList p0,
            PropertyImport p1) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 6 -> reduce0(
                    (PropertyImport)properties[0]
            );
            case 7 -> reduce1(
                    (PropertyImportList)properties[0],
                    (PropertyImport)properties[1]
            );
        }
    }
}
