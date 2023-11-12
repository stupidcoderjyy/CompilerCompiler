package stupidcoder.lex;

import java.util.Stack;

public class NFA {
    protected NFANode start, end;

    private boolean isEmpty() {
        return start == null || end == null;
    }

    public void and(NFA other) {
        if (isEmpty()) {
            start = other.start;
            end = other.end;
            return;
        }
        end.addEpsilonEdge(other.start);
        end = other.end;
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

    public void star() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start, newEnd);
        end.addEpsilonEdge(start, newEnd);
        start = newStart;
        end = newEnd;
    }

    public void quest() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start, newEnd);
        end.addEpsilonEdge(newEnd);
        start = newStart;
        end = newEnd;
    }

    public void plus() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(start, newEnd);
        start = newStart;
        end = newEnd;
    }

    public void or(NFA other) {
        if (isEmpty()) {
            start = other.start;
            end = other.end;
            return;
        }
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start, other.start);
        end.addEpsilonEdge(newEnd);
        other.end.addEpsilonEdge(newEnd);
        start = newStart;
        end = newEnd;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "null";
        } else {
            return start + ", " + end;
        }
    }

    public void print() {
        if (isEmpty()) {
            return;
        }
        boolean[] printed = new boolean[NFANode.nodeCount];
        Stack<NFANode> unchecked = new Stack<>();
        unchecked.push(start);
        while (!unchecked.empty()) {
            NFANode node = unchecked.pop();
            System.out.println(node);
            switch (node.edgeType) {
                case NFANode.DOUBLE_EPSILON:
                    if (!printed[node.next2.id]) {
                        unchecked.push(node.next2);
                        printed[node.next2.id] = true;
                    }
                case NFANode.SINGLE_EPSILON:
                case NFANode.CHAR:
                    if (!printed[node.next1.id]) {
                        unchecked.push(node.next1);
                        printed[node.next1.id] = true;
                    }
                    break;
            }
        }
    }
}
