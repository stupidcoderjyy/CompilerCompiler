package stupidcoder.generate.generators.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.generate.generators.java.importParser.tokens.TokenId;

public class PropertySeq implements IProperty {
    StringBuilder path;
    boolean isPkg;

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 4 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 5 -> reduce1(
                    (PropertySeq)properties[0],
                    (PropertyTerminal)properties[1],
                    (PropertyTerminal)properties[2]
            );
        }
    }

    //seq → id
    private void reduce0(
            PropertyTerminal p0) {
        path = new StringBuilder();
        TokenId t0 = p0.getToken();
        path.append(t0.lexeme);
    }

    //seq → seq . id
    private void reduce1(
            PropertySeq p0,
            PropertyTerminal p1,
            PropertyTerminal p2) {
        path = p0.path;
        isPkg = p0.isPkg;
        TokenId t2 = p2.getToken();
        if (t2.lexeme.equals("*")) {
            isPkg = true;
        }
        path.append(".").append(t2.lexeme);
    }
}
