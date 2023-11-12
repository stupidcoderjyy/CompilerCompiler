package stupidcoder.util.compile.property;

import stupidcoder.util.compile.Production;

public interface IProperty {
    void onReduced(Production p, IProperty ... properties);
}
