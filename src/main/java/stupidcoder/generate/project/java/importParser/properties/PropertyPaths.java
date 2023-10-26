package stupidcoder.generate.project.java.importParser.properties;

import stupidcoder.common.Production;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.generate.project.java.importParser.tokens.TokenId;

public class PropertyPaths implements IProperty {
    StringBuilder path;
    String postfix;
    boolean isPkg;
    //paths → id
    private void reduce0(
            PropertyTerminal p0) {
        path = new StringBuilder();
        TokenId t0 = p0.getToken();
        path.append(t0.lexeme);
        postfix = t0.lexeme;
    }

    //paths → paths . id
    private void reduce1(
            PropertyPaths p0,
            PropertyTerminal p1,
            PropertyTerminal p2) {
        path = p0.path;
        isPkg = p0.isPkg;
        TokenId t2 = p2.getToken();
        if (t2.lexeme.equals("*")) {
            isPkg = true;
        }
        path.append(".").append(t2.lexeme);
        postfix = t2.lexeme;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 9 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 10 -> reduce1(
                    (PropertyPaths)properties[0],
                    (PropertyTerminal)properties[1],
                    (PropertyTerminal)properties[2]
            );
        }
    }
}
