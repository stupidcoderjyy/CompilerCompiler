package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;

public class PropertyRoot implements IProperty {
    //root → head
    private void reduce0(
            PropertyHead p0) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyHead)properties[0]
        );
    }
}
