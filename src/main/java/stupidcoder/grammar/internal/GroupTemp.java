package stupidcoder.grammar.internal;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

import java.util.*;

public class GroupTemp extends LRGroup {
    private final CoreGroup core;
    private final List<LRItem> spreadSrc = new ArrayList<>();
    private final Map<Symbol, List<LRItem>> moveMap = new HashMap<>();
    private final IGroupExpandAction action;
    private final IGrammarAccess loader;
    private final Stack<LRItem> unchecked = new Stack<>();
    private final int coreItemsCount;

    public static void expand(IGrammarAccess loader, IGroupExpandAction action, CoreGroup core) {
        new GroupTemp(loader, action, core).expand();
    }

    private GroupTemp(IGrammarAccess loader, IGroupExpandAction action, CoreGroup core) {
        super(List.of());
        this.action = action;
        this.loader = loader;
        this.core = core;
        coreItemsCount = core.items.size();
        Iterator<LRItem> it = core.items.iterator();
        for (int c = 0 ; c < core.items.size() ; c ++) {
            LRItem t = new LRItem(it.next(), c);
            addItem(t);
            spreadSrc.add(t);
            unchecked.add(t);
        }
    }

    private void expand() {
        Set<Symbol> tempForward = new HashSet<>();
        while (!unchecked.empty()) {
            LRItem start = unchecked.pop();
            Symbol next = start.nextSymbol();
            if (next == null) {
                continue;
            }
            setDest(next, start);
            if (next.isTerminal) {
                continue;
            }
            boolean spread = loader.calcForward(tempForward, start.production, start.point + 1);
            if (spread) {
                tempForward.addAll(start.forwardSymbols);
            }
            List<Production> productions = loader.productionsWithHead(next);
            for (Production g : productions) {
                //创建所有子项
                LRItem child = hashToItem.getOrDefault(LRItem.calcHash(g, 0), null);
                if (child == null) {
                    child = new LRItem(g, 0, items.size());
                    addItem(child);
                    spreadSrc.add(null);
                    unchecked.add(child);
                }
                if (spread) {
                    if (start.id < coreItemsCount) {
                        spreadSrc.set(child.id, start);
                    } else {
                        spreadSrc.set(child.id, spreadSrc.get(start.id));
                    }
                }
                child.forwardSymbols.addAll(tempForward);
            }
            tempForward.clear();
        }
        action.onGroupExpandFinished(this, core, moveMap);
    }

    private void setDest(Symbol next, LRItem item) {
        List<LRItem> destItems = moveMap.getOrDefault(next, new ArrayList<>());
        destItems.add(item);
        moveMap.putIfAbsent(next, destItems);
    }

    public LRItem getSrc(LRItem temp) {
        return spreadSrc.get(temp.id);
    }
}
