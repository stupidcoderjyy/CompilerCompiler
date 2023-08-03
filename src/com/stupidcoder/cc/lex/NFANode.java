package com.stupidcoder.cc.lex;

public class NFANode {
    private static int nodeCount = 0;

    private static final byte NO_EDGE = 0;
    private static final byte SINGLE_EPSILON = 1;
    private static final byte DOUBLE_EPSILON = 2;
    private static final byte CHAR = 3;

    private byte edgeType = NO_EDGE;
    private int id;
    private NFANode next1;
    private NFANode next2;
    private ICharPredicate predicate;

    protected NFANode() {
        id = nodeCount++;
    }

    public void addEpsilonEdge(NFANode next) {
        if (edgeType == NO_EDGE) {
            next1 = next;
            edgeType = SINGLE_EPSILON;
        } else if (edgeType == SINGLE_EPSILON) {
            next2 = next;
            edgeType = DOUBLE_EPSILON;
        }
    }

    public void addEpsilonEdge(NFANode next1, NFANode next2) {
        this.next1 = next1;
        this.next2 = next2;
        edgeType = DOUBLE_EPSILON;
    }

    public void addCharEdge(ICharPredicate predicate,NFANode next) {
        this.predicate = predicate;
        next1 = next;
        edgeType = CHAR;
    }
}
