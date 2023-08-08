package com.stupidcoder.cc.lex.core;

import java.util.*;

public class NFA {
    protected NFANode start, end;

    public NFA and(NFA other) {
        if (isEmpty()) {
            start = other.start;
            end = other.end;
            return this;
        }
        end.addEpsilonEdge(other.start);
        end = other.end;
        return this;
    }

    public NFA andAtom(ICharPredicate predicate) {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addCharEdge(predicate, newEnd);
        if (isEmpty()) {
            start = newStart;
            end = newEnd;
            return this;
        }
        end.addEpsilonEdge(newStart);
        end = newEnd;
        return this;
    }

    public NFA star() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(start);
        end.addEpsilonEdge(newEnd);
        newStart.addEpsilonEdge(newEnd);

        start = newStart;
        end = newEnd;
        return this;
    }

    public NFA quest() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(newEnd);
        newStart.addEpsilonEdge(newEnd);

        start = newStart;
        end = newEnd;
        return this;
    }


    public NFA plus() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(start);
        end.addEpsilonEdge(newEnd);

        start = newStart;
        end = newEnd;
        return this;
    }

    public NFA or(NFA other) {
        if (isEmpty()) {
            start = other.start;
            end = other.end;
            return this;
        }
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start, other.start);
        end.addEpsilonEdge(newEnd);
        other.end.addEpsilonEdge(newEnd);
        start = newStart;
        end = newEnd;
        return this;
    }

    public boolean isEmpty() {
        return start == null || end == null;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "null";
        } else {
            return start.toString() + ", " + end.toString();
        }
    }

    public void print() {
        if (isEmpty()) {
            return;
        }
        Stack<NFANode> unchecked = new Stack<>();
        Set<NFANode> printed = new HashSet<>();
        unchecked.push(start);
        while (!unchecked.empty()) {
            NFANode node = unchecked.pop();
            System.out.println(node);
            switch (node.edgeType) {
                case NFANode.DOUBLE_EPSILON:
                    if (!printed.contains(node.next2)) {
                        unchecked.push(node.next2);
                        printed.add(node.next2);
                    }
                case NFANode.SINGLE_EPSILON:
                case NFANode.CHAR:
                    if (!printed.contains(node.next1)) {
                        unchecked.push(node.next1);
                        printed.add(node.next1);
                    }
                    break;
            }
        }
    }
}
