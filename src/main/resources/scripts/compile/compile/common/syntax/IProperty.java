package stupidcoder.common.syntax;

import stupidcoder.util.common.Production;

public interface IProperty {
    void onReduced(Production p, stupidcoder.util.common.syntax.IProperty... properties);
}
