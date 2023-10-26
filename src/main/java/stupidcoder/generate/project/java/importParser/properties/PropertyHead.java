package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyHead implements IProperty {
    //head → pkg imports
    private void reduce0(
            PropertyPkg p0,
            PropertyImports p1) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyPkg)properties[0],
            (PropertyImports)properties[1]
        );
    }
}
