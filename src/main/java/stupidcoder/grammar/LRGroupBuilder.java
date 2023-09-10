package stupidcoder.grammar;

import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.symbol.Symbol;
import stupidcoder.grammar.internal.GrammarLoader;
import stupidcoder.grammar.internal.IGrammarAccess;
import stupidcoder.grammar.internal.CoreGroup;
import stupidcoder.grammar.internal.GroupTemp;
import stupidcoder.grammar.internal.IGroupExpandAction;
import stupidcoder.grammar.internal.LRItem;
import stupidcoder.grammar.internal.IPriorityAccess;
import stupidcoder.grammar.internal.PriorityManager;

import java.util.*;

public class LRGroupBuilder implements IGroupExpandAction {
    private final IGrammarAccess grammarLoader;
    private final IPriorityAccess priority;
    private final IGADataAccept receiver;
    private final List<CoreGroup> idToCore = new ArrayList<>();
    private final Map<CoreGroup, Integer> groupToId = new HashMap<>();
    private final Stack<CoreGroup> unchecked = new Stack<>();
    private final Map<LRItem, List<LRItem>> spreadMapItems = new HashMap<>();

    public static void build(IGAPriorityInit priorityReg, IGAGrammarInit grammarReg, IGADataAccept receiver) {
        new LRGroupBuilder(priorityReg, grammarReg, receiver).buildGroups();
    }

    private LRGroupBuilder(IGAPriorityInit priorityReg, IGAGrammarInit grammarReg, IGADataAccept receiver) {
        GrammarLoader l = new GrammarLoader();
        PriorityManager p = new PriorityManager(l);
        this.grammarLoader = l;
        this.receiver = receiver;
        this.priority = p;
        grammarReg.init(l);
        priorityReg.init(p);
        p.init();
    }

    public void buildGroups() {
        LRItem root = new LRItem(grammarLoader.root(), 0);
        root.forwardSymbols.add(DefaultSymbols.FILE_END);
        registerCore(Set.of(root));
        while (!unchecked.isEmpty()) {
            GroupTemp.expand(grammarLoader, this, unchecked.pop());
        }
        grammarLoader.getTerminalIdRemap().forEach(receiver::setTerminalSymbolIdRemap);
    }

    @Override
    public void onCoreExpandFinished(GroupTemp baseTemp, CoreGroup curCore, Map<Symbol, List<LRItem>> goToMap) {
        //对当前核心设置规约操作
        for (LRItem item : curCore.items) {
            outputReduceOrAccept(item, goToMap);
        }
        //设置接受状态
        goToMap.forEach((input, tempItems) -> {
            //可能进入的下一个核心状态
            List<LRItem> coreItems = getCoreItems(tempItems);
            CoreGroup nextCore = registerCore(coreItems);
            if (input.isTerminal) {
                receiver.setActionShift(curCore.id, nextCore.id, input.id);
            } else {
                receiver.setGoto(curCore.id, nextCore.id, input.id);
            }
            for (LRItem tempItem : tempItems) {
                LRItem coreItem = nextCore.getItem(tempItem.production, tempItem.point + 1);
                LRItem src = baseTemp.getSrc(tempItem);
                if (src != null) {
                    //设置向前看符号的传播路径
                    setSpread(curCore.getItem(src.production, src.point), coreItem);
                }
                int pre = coreItem.forwardSymbols.size();
                //传递向前看符号
                coreItem.forwardSymbols.addAll(tempItem.forwardSymbols);
                if (coreItem.forwardSymbols.size() > pre && spreadMapItems.containsKey(coreItem)) {
                    spreadAndEmitActions(coreItem, goToMap);
                }
            }
        });
    }
    
    private List<LRItem> getCoreItems(List<LRItem> items) {
        List<LRItem> coreItems = new ArrayList<>();
        for (LRItem t : items) {
            coreItems.add(new LRItem(t.production, t.point + 1));
        }
        return coreItems;
    }

    private CoreGroup registerCore(Collection<LRItem> items) {
        CoreGroup candidate = new CoreGroup(idToCore.size(), items);
        int coreId = groupToId.getOrDefault(candidate, -1);
        if (coreId < 0) {
            idToCore.add(candidate);
            groupToId.put(candidate, candidate.id);
            unchecked.add(candidate);
            for (LRItem item : candidate.items) {
                item.id = candidate.id;
            }
            return candidate;
        }
        return idToCore.get(coreId);
    }

    private void setSpread(LRItem from, LRItem to) {
        if (spreadMapItems.containsKey(from)) {
            spreadMapItems.get(from).add(to);
        } else {
            List<LRItem> targets = new ArrayList<>();
            targets.add(to);
            spreadMapItems.put(from, targets);
        }
    }

    private void spreadAndEmitActions(LRItem start, Map<Symbol, List<LRItem>> goToMap) {
        Stack<LRItem> itemsToSpread = new Stack<>();
        itemsToSpread.add(start);
        while (!itemsToSpread.empty()) {
            LRItem src = itemsToSpread.pop();
            for (LRItem target : spreadMapItems.get(src)) {
                if (spreadAndEmitActions(src, target, goToMap)) {
                    itemsToSpread.push(target);
                }
            }
        }
    }

    private boolean spreadAndEmitActions(LRItem src, LRItem target, Map<Symbol, List<LRItem>> goToMap) {
        boolean add = false;
        for (Symbol srcForward : src.forwardSymbols) {
            if (target.forwardSymbols.contains(srcForward)) {
                continue;
            }
            target.forwardSymbols.add(srcForward);
            outputReduceOrAccept(target, goToMap);
            add = true;
        }
        return add && spreadMapItems.containsKey(target);
    }

    private void outputReduceOrAccept(LRItem item, Map<Symbol, List<LRItem>> goToMap) {
        if (!item.reachEnd()) {
            return;
        }
        if (item.production == grammarLoader.root()) {
            receiver.setActionAccept(item.id);
            return;
        }
        for (Symbol f : item.forwardSymbols) {
            if (shouldReduce(item, f, goToMap)) {
                receiver.setActionReduce(item.id, f.id, item.production.id());
            }
        }
    }

    private boolean shouldReduce(LRItem item, Symbol forward, Map<Symbol, List<LRItem>> goToMap) {
        if (goToMap.containsKey(forward)) {
            return priority.compare(forward, item.production) < 0;
        }
        return true;
    }
}
