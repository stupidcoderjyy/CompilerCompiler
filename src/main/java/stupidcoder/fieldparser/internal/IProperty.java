package stupidcoder.fieldparser.internal;

import stupidcoder.common.Production;

public interface IProperty {
    void onReduced(Production p, IProperty ... properties);
}
