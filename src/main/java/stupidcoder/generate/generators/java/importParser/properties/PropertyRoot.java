package stupidcoder.generate.generators.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyRoot implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyContents)properties[0]
        );
    }

    //root → contents
    private void reduce0(
            PropertyContents p0) {
        
    }
}
