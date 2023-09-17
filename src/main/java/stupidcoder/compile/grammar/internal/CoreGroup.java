package stupidcoder.compile.grammar.internal;

import java.util.Collection;

public class CoreGroup extends LRGroup {
    public final int id;

    public CoreGroup(int id, Collection<LRItem> items) {
        super(items);
        this.id = id;
    }
}
