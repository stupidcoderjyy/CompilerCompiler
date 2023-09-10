package stupidcoder.grammar.internal;

import stupidcoder.common.Production;

import java.util.*;

public class LRGroup {
    public final Set<LRItem> items;
    public final Map<Integer, LRItem> hashToItem = new HashMap<>();
    private int hash;

    public LRGroup(Collection<LRItem> items) {
        this.items = new HashSet<>();
        for (LRItem item : items) {
            addItem(item);
        }
    }

    public void addItem(LRItem item) {
        int itemHash = item.hashCode();
        if (!hashToItem.containsKey(itemHash)) {
            this.hash += itemHash;
            hashToItem.put(itemHash, item);
            items.add(item);
        }
    }

    public LRItem getItem(Production g, int point) {
        return hashToItem.get(LRItem.calcHash(g, point));
    }

    public boolean shouldCreate(Production g, int point) {
        return !hashToItem.containsKey(LRItem.calcHash(g, point));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LRGroup && ((LRGroup) obj).items.equals(items);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return hashToItem.values().toString();
    }
}
