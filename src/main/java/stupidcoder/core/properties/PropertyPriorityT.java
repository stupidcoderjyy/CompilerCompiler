package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.core.tokens.TokenPriorityMarkTerminal;

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
        this.value = t0.value;
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
