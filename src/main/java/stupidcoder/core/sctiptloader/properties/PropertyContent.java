package stupidcoder.core.sctiptloader.properties;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyContent implements IProperty {
    //content → syntaxBegin syntax
    private void reduce0(
            PropertyTerminal p0,
            PropertySyntax p1) {
        
    }

    //content → tokenBegin tokens
    private void reduce1(
            PropertyTerminal p0,
            PropertyTokens p1) {
        
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 4 -> reduce0(
                    (PropertyTerminal)properties[0],
                    (PropertySyntax)properties[1]
            );
            case 5 -> reduce1(
                    (PropertyTerminal)properties[0],
                    (PropertyTokens)properties[1]
            );
        }
    }
}
