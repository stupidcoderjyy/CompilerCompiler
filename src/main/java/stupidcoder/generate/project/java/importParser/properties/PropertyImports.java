package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

public class PropertyImports implements IProperty {
    //imports → importList
    private void reduce0(
            PropertyImportList p0) {
        
    }

    //imports → ε
    private void reduce1(
            PropertyTerminal p0) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 4 -> reduce0(
                    (PropertyImportList)properties[0]
            );
            case 5 -> reduce1(
                    (PropertyTerminal)properties[0]
            );
        }
    }
}
