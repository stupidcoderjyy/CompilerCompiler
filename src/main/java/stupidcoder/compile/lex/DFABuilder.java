package stupidcoder.compile.lex;

import stupidcoder.Config;
import stupidcoder.ConsoleUtil;
import stupidcoder.util.ASCII;
import stupidcoder.util.ArrayUtil;

import java.util.*;

public class DFABuilder {
    private static final boolean printDebug = Config.getBool(Config.LEXER_DEBUG_INFO);
    private final Map<NFANodeSet, Integer> nodeSetToState = new HashMap<>();
    private final List<NFANodeSet> stateToNodeSet = new ArrayList<>();
    private List<String> nodeToToken;
    private int statesCount = 1;
    private final List<Boolean> accepted = new ArrayList<>();
    private final List<int[]> goTo = new ArrayList<>();
    private final List<String> tokens = new ArrayList<>();
    private final IDfaSetter setter;

    private DFABuilder(IDfaSetter setter) {
        this.setter = setter;
    }

    public static void build(IDfaSetter setter, NFARegexParser parser) {
        new DFABuilder(setter).build(parser.getNfa().start, parser.getNodeIdToToken());
    }

    private void build(NFANode startNode, List<String> nfaNodeToToken) {
        this.nodeToToken = nfaNodeToToken;
        stateToNodeSet.add(null);
        transfer(startNode);
        minimize();
        accepted.clear();
        goTo.clear();
        tokens.clear();
    }

    private void transfer(NFANode startNode) {
        Stack<Integer> unchecked = new Stack<>();
        createState(new NFANodeSet(epsilonClosure(Set.of(startNode))), unchecked);
        while (!unchecked.empty()) {
            int curState = unchecked.pop();
            NFANodeSet curGroup = stateToNodeSet.get(curState);
            for (byte b = 0 ; b >= 0 ; b ++) {
                Set<NFANode> nextNodes = next(curGroup.nfaNodes, b);
                if (nextNodes.isEmpty()) {
                    continue;
                }
                NFANodeSet candidate = new NFANodeSet(nextNodes);
                int targetState = nodeSetToState.containsKey(candidate) ?
                        nodeSetToState.get(candidate) :
                        createState(candidate, unchecked);
                goTo.get(curState)[b] = targetState;
            }
        }
        stateToNodeSet.clear();
        nodeSetToState.clear();
    }

    private int createState(NFANodeSet g, Stack<Integer> unchecked) {
        int newId = statesCount++;
        nodeSetToState.put(g, newId);
        stateToNodeSet.add(g);
        unchecked.push(newId);
        ensureSize(statesCount);
        for (NFANode node : g.nfaNodes) {
            if (node.accepted) {
                accepted.set(newId, true);
                tokens.set(newId, nodeToToken.get(node.id));
                return newId;
            }
        }
        return newId;
    }

    private void ensureSize(int size) {
        ArrayUtil.resize(accepted, size, () -> false);
        ArrayUtil.resize(goTo, size, () -> new int[128]);
        ArrayUtil.resize(tokens, size, () -> null);
    }

    private static Set<NFANode> epsilonClosure(Set<NFANode> nodes) {
        if (nodes.isEmpty()) {
            return nodes;
        }
        Set<NFANode> result = nodes instanceof HashSet ? nodes : new HashSet<>(nodes);
        Stack<NFANode> unchecked = new Stack<>();
        unchecked.addAll(result);
        while (!unchecked.empty()) {
            NFANode cur = unchecked.pop();
            switch (cur.edgeType) {
                case NFANode.DOUBLE_EPSILON:
                    if (!result.contains(cur.next2)) {
                        unchecked.push(cur.next2);
                        result.add(cur.next2);
                    }
                case NFANode.SINGLE_EPSILON:
                    if (!result.contains(cur.next1)) {
                        unchecked.push(cur.next1);
                        result.add(cur.next1);
                    }
                    break;
            }
        }
        return result;
    }

    private static Set<NFANode> next(Set<NFANode> begin, byte b) {
        Set<NFANode> result = new HashSet<>();
        begin.stream()
                .filter(n -> n.edgeType == NFANode.CHAR && n.predicate.accept(b))
                .forEach(n -> result.add(n.next1));
        return epsilonClosure(result);
    }

    private static class NFANodeSet {
        protected final Set<NFANode> nfaNodes;
        private final int hash;

        public NFANodeSet(Set<NFANode> nfaNodes) {
            this.nfaNodes = nfaNodes;
            this.hash = nfaNodes.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NFANodeSet && ((NFANodeSet) obj).nfaNodes.equals(nfaNodes);
        }

        @Override
        public String toString() {
            return nfaNodes.toString();
        }
    }

    private final List<Set<Integer>> groups = new ArrayList<>();
    private final Stack<Integer> unchecked = new Stack<>();
    private final Stack<Integer> suspiciousGroups = new Stack<>();
    private boolean splitInRound = false;
    private int groupCount;
    private int[] stateToGroup;

    private void minimize() {
        stateToGroup = new int[statesCount];
        groupCount = 1;
        initGroup();
        splitGroups();
        outputData();
        stateToGroup = null;
        groups.clear();
    }

    private void splitGroups() {
        do {
            unchecked.addAll(suspiciousGroups);
            suspiciousGroups.clear();
            splitInRound = false;
            while (!unchecked.empty()) {
                int curGroupId = unchecked.pop();
                Set<Integer> curGroup = groups.get(curGroupId);
                if (curGroup.size() == 1) {
                    continue;
                }
                if (split(curGroup)) {
                    suspiciousGroups.add(curGroupId);
                }
            }
        } while (splitInRound && !suspiciousGroups.empty());
    }

    private void initGroup() {
        groups.add(null);
        Map<String, Set<Integer>> acceptedGroups = new HashMap<>();
        Set<Integer> nonAcceptedGroup = new HashSet<>();
        for (int i = 1 ; i < statesCount ; i ++) {
            if (accepted.get(i)) {
                String token = tokens.get(i);
                Set<Integer> group = acceptedGroups.getOrDefault(token, new HashSet<>());
                group.add(i);
                acceptedGroups.putIfAbsent(token, group);
            } else {
                nonAcceptedGroup.add(i);
            }
        }
        acceptedGroups.forEach((token, group) -> createGroup(group));
        createGroup(nonAcceptedGroup);
    }

    private boolean split(Set<Integer> curGroup) {
        Set<Integer> newGroup = null;
        int std = curGroup.iterator().next();
        curGroup.remove(std);
        List<Integer> removed = new ArrayList<>();
        for (byte b = 0 ; b >= 0 ; b ++) {
            if (curGroup.isEmpty()) {
                break;
            }
            int stdTarget = stateToGroup[goTo.get(std)[b]];
            for (int s : curGroup) {
                int sTarget = stateToGroup[goTo.get(s)[b]];
                if (stdTarget != sTarget) {
                    if (newGroup == null) {
                        newGroup = new HashSet<>();
                    }
                    newGroup.add(s);
                    removed.add(s);
                }
            }
            removed.forEach(curGroup::remove);
            removed.clear();
        }
        curGroup.add(std);
        if (newGroup != null) {
            splitInRound = true;
            createGroup(newGroup);
        }
        return curGroup.size() > 1;
    }

    private void createGroup(Set<Integer> states) {
        int groupId = groupCount++;
        groups.add(states);
        for (int state : states) {
            stateToGroup[state] = groupId;
        }
        unchecked.add(groupId);
    }

    private void outputData() {
        int[] delegates = new int[groupCount];
        for (int i = 1 ; i < groups.size() ; i ++) {
            delegates[i] = groups.get(i).iterator().next();
        }
        for (int group = 1 ; group < groupCount ; group++) {
            if (printDebug) {
                printState(group, accepted.get(delegates[group]));
            }
            int delegate = delegates[group];
            for (byte b = 0 ; b >= 0 ; b ++) {
                int dest = goTo.get(delegate)[b];
                if (dest > 0) {
                    setter.setGoTo(group, b, stateToGroup[dest]);
                    if (printDebug) {
                        int targetGroup = stateToGroup[dest];
                        printGoto(b, targetGroup, accepted.get(delegates[targetGroup]));
                    }
                }
            }
            if (accepted.get(delegates[group])) {
                setter.setAccepted(group, tokens.get(delegates[group]));
            }
            if (printDebug) {
                System.out.println();
            }
        }
        setter.setStartState(stateToGroup[1]);
        setter.setDfaStatesCount(groupCount);
        setter.setOthers(tokens);
    }

    private void printState(int state, boolean accept) {
        if (accept) {
            ConsoleUtil.printHighlightBlue("  ");
        } else {
            ConsoleUtil.printHighlightYellow("  ");
        }
        System.out.println(" state " + state);
    }

    private void printGoto(int input, int target, boolean accept) {
        System.out.print(" ");
        String info = ASCII.isCtrl(input) ? Integer.toString(input) : (input + "('" + (char) input + "')");
        System.out.print(info + " ".repeat(10 - info.length()));
        System.out.print("→    ");
        if (accept) {
            ConsoleUtil.printHighlightWhite(" " + target + " ");
        } else {
            System.out.print(" " + target + " ");
        }
        System.out.println();
    }
}
