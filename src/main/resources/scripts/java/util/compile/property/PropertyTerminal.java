package stupidcoder.util.compile.property;

import stupidcoder.util.compile.Production;
import stupidcoder.util.compile.token.IToken;

public final class PropertyTerminal implements IProperty{
    private final IToken token;

    public PropertyTerminal(IToken token) {
        this.token = token;
    }

    public <T extends IToken> T getToken() {
        return (T) token;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        //终结符号
    }
}
