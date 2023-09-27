package stupidcoder.fieldparser.internal;

import stupidcoder.common.Production;
import stupidcoder.common.token.IToken;

public final class PropertyTerminal<T extends IToken> implements IProperty{
    public final T token;

    public PropertyTerminal(T token) {
        this.token = token;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        //终结符号
    }
}
