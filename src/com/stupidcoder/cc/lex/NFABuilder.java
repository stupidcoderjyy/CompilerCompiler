package com.stupidcoder.cc.lex;

public class NFABuilder {
    private NFANode start, end;

    public NFABuilder and(NFABuilder other) {
        if (isEmpty()) {
            start = other.start;
            end = other.end;
            return this;
        }
        end.addEpsilonEdge(other.start);
        end = other.end;
        return this;
    }

    public NFABuilder andAtom(ICharPredicate predicate) {
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

    public NFABuilder star() {
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

    public NFABuilder quest() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(newEnd);
        newStart.addEpsilonEdge(newEnd);

        start = newStart;
        end = newEnd;
        return this;
    }


    public NFABuilder plus() {
        NFANode newStart = new NFANode();
        NFANode newEnd = new NFANode();
        newStart.addEpsilonEdge(start);
        end.addEpsilonEdge(start);
        end.addEpsilonEdge(newEnd);

        start = newStart;
        end = newEnd;
        return this;
    }

    public NFABuilder or(NFABuilder other) {
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

    public NFANode getStart() {
        return start;
    }

    public NFANode getEnd() {
        return end;
    }

    public boolean isEmpty() {
        return start == null || end == null;
    }
}
