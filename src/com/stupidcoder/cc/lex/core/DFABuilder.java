package com.stupidcoder.cc.lex.core;

import com.stupidcoder.cc.util.ArrayUtil;

import java.util.*;

public class DFABuilder {
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
        new DFABuilder(setter).build(parser.getNfa().start, parser.getTokens());
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
        createGroup(new NFANodeSet(epsilonClosure(Set.of(startNode))), unchecked);
        while (!unchecked.empty()) {
            int curState = unchecked.pop();
            NFANodeSet curGroup = stateToNodeSet.get(curState);
            for (byte b = 0 ; b >= 0 ; b ++) {
                Set<NFANode> nextNodes = next(curGroup.nfaNodes, b);
                if (nextNodes.isEmpty()) {
                    continue;
                }
                NFANodeSet candidate = new NFANodeSet(nextNodes);
                //设置 state -> state
                goTo.get(curState)[b] = nodeSetToState.containsKey(candidate) ?
                        nodeSetToState.get(candidate) :
                        createGroup(candidate, unchecked);
            }
        }
        stateToNodeSet.clear();
        nodeSetToState.clear();
    }

    private int createGroup(NFANodeSet g, Stack<Integer> unchecked) {
        int newId = statesCount++;
        //基本循环
        nodeSetToState.put(g, newId);
        stateToNodeSet.add(g);
        unchecked.push(newId);
        //设置 state -> IToken 和 state -> accepted
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
                    //继续执行下面的分支
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
        for (NFANode node : begin) {
            if (node.edgeType == NFANode.CHAR && node.predicate.accept(b)) {
                result.add(node.next1);
            }
        }
        return epsilonClosure(result);
    }

    private void ensureSize(int size) {
        ArrayUtil.resize(accepted, size, () -> false);
        ArrayUtil.resize(goTo, size, () -> new int[128]);
        ArrayUtil.resize(tokens, size, () -> null);
    }

    private final List<Set<Integer>> groups = new ArrayList<>();
    private final Stack<Integer> unchecked = new Stack<>();
    private int groupCount;
    private int[] stateToGroup;

    private void minimize() {
        stateToGroup = new int[statesCount];
        groupCount = 1;
        initGroup(); //初始组
        splitGroups(); //对组进行划分
        generateMinDfa(); //分完组之后生成新的DFA
        stateToGroup = null;
        groups.clear();
    }

    private void initGroup() {
        groups.add(null);
        Map<String, Set<Integer>> acceptedGroups = new HashMap<>();
        Set<Integer> nonAcceptedGroup = new HashSet<>();
        //初始划分规则：1）接受状态与非接受状态不等价；2）返回不同词法单元类型的接受状态不等价
        for (int i = 1; i < statesCount; i++) {
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

    private void splitGroups() {
        while (!unchecked.empty()) {
            int curGroupId = unchecked.pop();
            Set<Integer> curCroup = groups.get(curGroupId);
            if (curCroup.size() == 1) {
                continue;
            }
            Set<Integer> newGroup = checkAndSplit(curCroup); //对组中的状态进行检查
            if (newGroup != null) {
                unchecked.add(curGroupId);
                createGroup(newGroup);
            }
        }
        setter.setDfaStatesCount(groupCount);
    }

    private Set<Integer> checkAndSplit(Set<Integer> curGroup) {
        Set<Integer> newGroup = null;
        int stdState = curGroup.iterator().next();
        curGroup.remove(stdState);
        for (byte b = 0 ; b >= 0 ; b ++) {
            if (curGroup.size() == 0) {
                break;
            }
            int stdTarget = stateToGroup[goTo.get(stdState)[b]];
            List<Integer> removed = new ArrayList<>();
            for (int testState : curGroup) {
                int testTarget = stateToGroup[goTo.get(testState)[b]];
                if (stdTarget != testTarget) {
                    if (newGroup == null) {
                        newGroup = new HashSet<>();
                    }
                    newGroup.add(testState);
                    removed.add(testState);
                }
            }
            removed.forEach(curGroup::remove);
        }
        curGroup.add(stdState);
        return newGroup;
    }

    private void createGroup(Set<Integer> states) {
        int groupId = groupCount++;
        groups.add(states);
        for (Integer state : states) {
            stateToGroup[state] = groupId;
        }
        unchecked.add(groupId);
    }

    private void generateMinDfa() {
        int[] delegates = new int[groupCount];
        for (int i = 1 ; i < groups.size() ; i ++) {
            delegates[i] = groups.get(i).iterator().next();
        }
        for (int group = 1; group < groupCount; group++) {
            int delegate = delegates[group];
            //设置从group出发的goto
            for (byte b = 0 ; b >= 0 ; b ++) {
                int dest = goTo.get(delegate)[b];
                if (dest > 0) {
                    setter.setGoTo(group, b, stateToGroup[dest]);
                }
            }
            //若group为接受状态，设置接受状态和token
            if (accepted.get(delegates[group])) {
                setter.setAccepted(group);
                setter.setToken(group, tokens.get(delegates[group]));
            }
        }
        //1号结点是开始结点
        setter.setStartState(stateToGroup[1]);
    }

    private static class NFANodeSet {
        protected final Set<NFANode> nfaNodes;
        private final int hash;

        public NFANodeSet(Set<NFANode> nodes) {
            nfaNodes = new HashSet<>(nodes);
            hash = nfaNodes.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NFANodeSet &&
                    obj.hashCode() == hash &&
                    ((NFANodeSet) obj).nfaNodes.equals(nfaNodes);
        }

        @Override
        public String toString() {
            return nfaNodes.toString();
        }
    }
}
