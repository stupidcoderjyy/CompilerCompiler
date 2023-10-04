package stupidcoder.compile.syntax;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;

import java.util.*;

public class LRGroupBuilder {
    private final SyntaxLoader loader;
    private final ISyntaxAnalyzerSetter receiver;
    private final List<LRGroup> idToCore = new ArrayList<>();
    private final Map<LRGroup, Integer> coreToId = new HashMap<>();
    private final Map<LRItem, List<LRItem>> spreadMap = new HashMap<>();
    private final List<Map<Symbol, LRGroup>> groupToTargets = new ArrayList<>();

    public static void build(SyntaxLoader l, ISyntaxAnalyzerSetter receiver) {
        new LRGroupBuilder(l, receiver).build();
    }

    private LRGroupBuilder(SyntaxLoader l, ISyntaxAnalyzerSetter receiver) {
        this.loader = l;
        this.receiver = receiver;
    }

    public void build() {
        LRItem root = new LRItem(loader.root(), 0);
        registerCore(new LRGroup(List.of(root), 0));
        expandGroups();
        spreadSymbols(root);
        emitActions();
    }

    private final Stack<LRGroup> unchecked = new Stack<>();
    private Map<Symbol, List<LRItem>> tempGoto;
    private List<Integer> spreadSrc;
    private List<LRItem> tempCoreItems;
    private LRGroup tempGroup;
    private LRGroup curCore;

    private void expandGroups() {
        while (!unchecked.empty()) {
            tempGoto = new HashMap<>();
            spreadSrc = new ArrayList<>();
            tempCoreItems = new ArrayList<>();
            tempGroup = new LRGroup();
            curCore = unchecked.pop();
            initTempItems(curCore);
            expandTempItems();
            tempGoto.forEach(this::buildTargetCores);
        }
    }

    private void initTempItems(LRGroup core) {
        for (LRItem item : core.items) {
            int id = tempGroup.items.size();
            LRItem temp = new LRItem(item.production, item.point, id);
            tempGroup.insertItem(temp);
            unexpanded.push(temp);
            spreadSrc.add(id);
            tempCoreItems.add(item);
        }
    }

    private void expandTempItems() {
        while (!unexpanded.isEmpty()) {
            LRItem item = unexpanded.pop();
            Symbol next = item.nextSymbol();
            if (next == null || next == DefaultSymbols.EPSILON) {
                continue;
            }
            setDest(next, item);
            if (next.isTerminal) {
                continue;
            }
            boolean spread = loader.calcForward(tempForward, item.production, item.point);
            if (spread) {
                tempForward.addAll(item.forwardSymbols);
            }
            for (Production p : loader.productionsWithHead(next)) {
                LRItem other = tempGroup.getItem(p, 0);
                if (other == null) {
                    other = new LRItem(p, 0, tempGroup.items.size());
                    tempGroup.insertItem(other);
                    unexpanded.push(other);
                    spreadSrc.add(-1);
                }
                if (spread) {
                    int preSrc = spreadSrc.get(item.id);
                    if (preSrc >= 0) {
                        spreadSrc.set(other.id, preSrc);
                    }
                }
                other.forwardSymbols.addAll(tempForward);
            }
            tempForward.clear();
        }
    }

    private void setDest(Symbol input, LRItem item) {
        if (tempGoto.containsKey(input)) {
            tempGoto.get(input).add(item);
        } else {
            List<LRItem> items = new ArrayList<>();
            items.add(item);
            tempGoto.put(input, items);
        }
    }

    private void buildTargetCores(Symbol input, List<LRItem> items) {
        LRGroup target = getOrCreateCore(items);
        groupToTargets.get(curCore.id).put(input, target);
        for (LRItem temp : items) {
            LRItem targetItem = target.getItem(temp.production, temp.point + 1);
            if (spread(temp, targetItem)) {
                itemsToSpread.push(targetItem);
            }
            if (spreadSrc.get(temp.id) < 0) {
                continue;
            }
            LRItem src = tempCoreItems.get(spreadSrc.get(temp.id));
            setSpread(src, targetItem);
        }
    }

    private boolean spread(LRItem src, LRItem dest) {
        int pre = dest.forwardSymbols.size();
        dest.forwardSymbols.addAll(src.forwardSymbols);
        return dest.forwardSymbols.size() > pre;
    }

    private void setSpread(LRItem from, LRItem to) {
        if (spreadMap.containsKey(from)) {
            spreadMap.get(from).add(to);
        } else {
            List<LRItem> targets = new ArrayList<>();
            targets.add(to);
            spreadMap.put(from, targets);
        }
    }
    
    private LRGroup getOrCreateCore(List<LRItem> items) {
        LRGroup target = new LRGroup(idToCore.size());
        for (LRItem item : items) {
            LRItem next = new LRItem(item.production, item.point + 1);
            target.insertItem(next);
        }
        if (coreToId.containsKey(target)) {
            target = idToCore.get(coreToId.get(target));
        } else {
            for (LRItem item : target.items) {
                item.id = target.id;
            }
            registerCore(target);
        }
        return target;
    }

    private void registerCore(LRGroup g) {
        idToCore.add(g);
        coreToId.put(g, g.id);
        unchecked.push(g);
        groupToTargets.add(new HashMap<>());
    }

    private final Stack<LRItem> itemsToSpread = new Stack<>();

    private void spreadSymbols(LRItem root) {
        root.forwardSymbols.add(DefaultSymbols.FILE_END);
        itemsToSpread.push(root);
        while (!itemsToSpread.empty()) {
            LRItem src = itemsToSpread.pop();
            if (!spreadMap.containsKey(src)) {
                continue;
            }
            for (LRItem dest : spreadMap.get(src)) {
                if (!spread(src, dest) || !spreadMap.containsKey(dest)) {
                    continue;
                }
                itemsToSpread.push(dest);
            }
        }
    }

    private void emitActions() {
        if (receiver == null) {
            return;
        }
        for (LRGroup group : idToCore) {
            expand(group);
            emitActions(group);
            group.items.clear();
            group.hashToItem.clear();
        }
        receiver.setStatesCount(idToCore.size());
        receiver.setOthers(loader);
    }

    private final Deque<LRItem> unexpanded = new ArrayDeque<>();
    private final Set<Symbol> tempForward = new HashSet<>();

    private void expand(LRGroup core) {
        unexpanded.addAll(core.items);
        while (!unexpanded.isEmpty()) {
            LRItem item = unexpanded.pop();
            Symbol next = item.nextSymbol();
            if (next == null || next.isTerminal) {
                continue;
            }
            if (loader.calcForward(tempForward, item.production, item.point)) {
                tempForward.addAll(item.forwardSymbols);
            }
            for (Production p : loader.productionsWithHead(next)) {
                LRItem other = core.getItem(p, 0);
                if (other == null) {
                    other = core.registerItem(p);
                    unexpanded.push(other);
                }
                other.forwardSymbols.addAll(tempForward);
            }
            tempForward.clear();
        }
    }

    private void emitActions(LRGroup core) {
        Map<Symbol, LRGroup> goToMap = groupToTargets.get(core.id);
        Set<Integer> addedTarget = new HashSet<>();
        //输出数据
        for (LRItem item : core.items) {
            Symbol next = item.nextSymbol();
            if (next == null || next == DefaultSymbols.EPSILON) {
                emitReduceAndAccept(item, goToMap, core);
            } else {
                //GOTO和移入
                emitGotoAndShift(next, goToMap, core, addedTarget);
            }
        }
    }

    private void emitGotoAndShift(Symbol next, Map<Symbol, LRGroup> goToMap, LRGroup core, Set<Integer> addedTarget) {
        int targetCore = goToMap.get(next).id;
        if (next.isTerminal) {
            receiver.setActionShift(core.id, targetCore, next.id);
        } else {
            if (addedTarget.contains(targetCore)) {
                return;
            }
            addedTarget.add(targetCore);
            receiver.setGoto(core.id, targetCore, next.id);
        }
    }

    private void emitReduceAndAccept(LRItem item, Map<Symbol, LRGroup> goToMap, LRGroup core) {
        //接受
        if (item.production == loader.root()) {
            receiver.setActionAccept(core.id, DefaultSymbols.FILE_END.id);
            return;
        }
        //规约
        for (Symbol f : item.forwardSymbols) {
            boolean conflict = goToMap.containsKey(f);
            if (!conflict || loader.shouldReduce(item.production, f)) {
                receiver.setActionReduce(core.id, f.id, item.production.id());
                if (conflict) {
                    warnConflict(item, f, false);
                }
                continue;
            }
            warnConflict(item, f, true);
        }
    }

    private void warnConflict(LRItem item, Symbol f, boolean shift) {
        System.err.println("shift-reduce conflict:");
        System.err.println("    item:" + item);
        System.err.println("    forward:" + f);
        System.err.println("    action:" + (shift ? "SHIFT" : "REDUCE"));
    }
}
