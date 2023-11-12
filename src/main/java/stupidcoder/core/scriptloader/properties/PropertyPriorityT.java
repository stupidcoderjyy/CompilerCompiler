package stupidcoder.core.scriptloader.properties;

import stupidcoder.core.scriptloader.tokens.TokenPriorityMarkTerminal;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyPriorityT implements IProperty {
    int value;

    //priorityT → ε
    private void reduce0(
            PropertyTerminal p0) {
        
    }

    //priorityT → pMarkT
    private void reduce1(
            PropertyTerminal p0) {
        TokenPriorityMarkTerminal t0 = p0.getToken();
        this.value = t0.val;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 20 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 21 -> reduce1(
                    (PropertyTerminal)properties[0]
            );
        }
    }
}
