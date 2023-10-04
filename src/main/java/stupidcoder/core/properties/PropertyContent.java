package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;

public class PropertyContent implements IProperty {
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
}
