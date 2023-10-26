package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;

public class PropertyRoot implements IProperty {
    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyScript)properties[0]
        );
    }

    //root → script
    private void reduce0(
            PropertyScript p0) {
        
    }
}
