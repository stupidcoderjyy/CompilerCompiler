package stupidcoder.compile.syntax;

import stupidcoder.util.compile.Production;

import java.util.*;

class LRGroup {
    Set<LRItem> items = new HashSet<>();
    final Map<Integer, LRItem> hashToItem = new HashMap<>();
    final int id;
    private int hash;

    LRGroup(Collection<LRItem> items, int id) {
        this.id = id;
        for (LRItem item : items) {
            item.id = id;
            insertItem(item);
        }
    }

    LRGroup(int id) {
        this.id = id;
    }

    LRGroup() {
        this(-1);
    }

    LRItem registerItem(Production g) {
        LRItem res = new LRItem(g, 0, id);
        this.hash += res.hash;
        hashToItem.put(res.hash, res);
        items.add(res);
        return res;
    }

    void insertItem(LRItem item) {
        this.hash += item.hash;
        hashToItem.put(item.hash, item);
        items.add(item);
    }

    LRItem getItem(Production g, int point) {
        return hashToItem.get(LRItem.calcHash(g, point));
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
        return items.toString();
    }
}
