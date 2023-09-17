package stupidcoder.compile.lex;

import stupidcoder.util.ASCII;
import stupidcoder.util.ArrayUtil;
import stupidcoder.util.input.IInput;
import stupidcoder.util.input.StringInput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NFARegexParser {
    private IInput input;
    private NFA nfa;
    private final List<String> nodeIdToToken = new ArrayList<>();

    public void register(String regex, String token) {
        input = new StringInput(regex);
        NFA regexNfa = expr();
        setAcceptedNode(regexNfa, token);
        if (nfa == null) {
            nfa = regexNfa;
        } else {
            NFANode newStart = new NFANode();
            newStart.addEpsilonEdge(regexNfa.start, nfa.start);
            nfa.start = newStart;
        }
    }

    private void setAcceptedNode(NFA target, String token) {
        target.end.accepted = true;
        ArrayUtil.resize(nodeIdToToken, target.end.id + 1);
        nodeIdToToken.set(target.end.id, token);
    }

    public NFA getNfa() {
        return nfa;
    }

    public List<String> getNodeIdToToken() {
        return nodeIdToToken;
    }

    private NFA expr() {
        NFA result = new NFA();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case ')' -> {
                    return result;
                }
                case '|' -> {
                    input.read();
                    result.or(seq());
                }
                default -> result.and(seq());
            }
        }
        return result;
    }

    private NFA seq() {
        input.retract();
        NFA result = new NFA();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '(' -> result.and(checkClosure(expr()));
                case '|', ')' -> {
                    input.retract();
                    return result;
                }
                default -> result.and(atom());
            }
        }
        return result;
    }

    private NFA atom() {
        input.retract();
        int b = input.read();
        ICharPredicate predicate;
        switch (b) {
            case '[' -> predicate = clazz();
            case '\\' -> predicate = ICharPredicate.single(input.read());
            case '@' -> predicate = escape(input.read());
            default -> predicate = ICharPredicate.single(b);
        }
        return checkClosure(new NFA().andAtom(predicate));
    }

    private NFA checkClosure(NFA target) {
        if (input.available()) {
            switch (input.read()) {
                case '*' -> target.star();
                case '+' -> target.plus();
                case '?' -> target.quest();
                default -> input.retract();
            }
        }
        return target;
    }

    private ICharPredicate clazz() {
        ICharPredicate result = null;
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '[' -> result = ICharPredicate.or(result, clazz());
                case ']' -> {
                    return result;
                }
                default -> result = ICharPredicate.or(result, minClazzPredicate());
            }
        }
        return result;
    }

    private ICharPredicate minClazzPredicate() {
        input.retract();
        int b = input.read();
        if (b == '^') {
            Set<Integer> excluded = new HashSet<>();
            while (input.available()) {
                int b1 = input.read();
                if (b1 == '[' || b1 == ']') {
                    input.retract();
                    break;
                }
                excluded.add(b1);
            }
            return c -> !excluded.contains(c);
        }
        int b1 = input.read();
        if (b1 == '-') {
            int b2 = input.read();
            if (b2 == '[' || b2 == ']') {
                input.retract();
                return c -> c == b || c == '-';
            }
            return ICharPredicate.ranged(b, b2);
        }
        input.retract();
        return ICharPredicate.single(b);
    }

    private ICharPredicate escape(int b) {
        return switch (b) {
            case 'D', 'd' -> ASCII::isDigit;
            case 'W', 'w' -> ASCII::isWord;
            case 'U', 'u' -> ASCII::isAlnum;
            case 'H', 'h' -> ASCII::isHex;
            case 'A', 'a' -> ASCII::isAlpha;
            default -> ch -> true;
        };
    }
}
