package stupidcoder.common.syntax;

import stupidcoder.common.Production;

public interface IProperty {
    void onReduced(Production p, IProperty ... properties);
}
