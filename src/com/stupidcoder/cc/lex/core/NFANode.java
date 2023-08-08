package com.stupidcoder.cc.lex.core;

public class NFANode {
    private static int nodeCount = 0;

    protected static final byte NO_EDGE = 0;
    protected static final byte SINGLE_EPSILON = 1;
    protected static final byte DOUBLE_EPSILON = 2;
    protected static final byte CHAR = 3;

    protected byte edgeType = NO_EDGE;
    protected boolean accepted = false;
    protected final int id;
    protected NFANode next1;
    protected NFANode next2;
    protected ICharPredicate predicate;

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

    @Override
    public String toString() {
        String str = id + "(";
        switch (edgeType) {
            case NO_EDGE -> str += "null";
            case SINGLE_EPSILON -> str += "ε" + next1.id;
            case DOUBLE_EPSILON -> str += "ε" + next1.id + ",ε" + next2.id;
            case CHAR -> str += "c" + next1.id;
        }
        str += ')';
        return str;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NFANode && obj.hashCode() == id;
    }
}
