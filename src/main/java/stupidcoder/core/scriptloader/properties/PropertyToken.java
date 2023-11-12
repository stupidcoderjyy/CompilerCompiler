package stupidcoder.core.scriptloader.properties;

import stupidcoder.core.scriptloader.tokens.TokenId;
import stupidcoder.core.scriptloader.tokens.TokenString;
import stupidcoder.lex.NFARegexParser;
import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.property.IProperty;
import stupidcoder.util.compile.property.PropertyTerminal;

public class PropertyToken implements IProperty {
    private final NFARegexParser parser;

    public PropertyToken(NFARegexParser parser) {
        this.parser = parser;
    }

    //token → id : string ;
    private void reduce0(
            PropertyTerminal p0,
            PropertyTerminal p1,
            PropertyTerminal p2,
            PropertyTerminal p3) {
        TokenId t0 = p0.getToken();
        TokenString t2 = p2.getToken();
        parser.register(t2.lexeme, t0.lexeme);
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        reduce0(
            (PropertyTerminal)properties[0],
            (PropertyTerminal)properties[1],
            (PropertyTerminal)properties[2],
            (PropertyTerminal)properties[3]
        );
    }
}
