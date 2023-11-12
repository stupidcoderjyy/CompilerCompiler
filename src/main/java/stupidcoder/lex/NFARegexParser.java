package stupidcoder.lex;

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
    private String regex;
    private final List<String> nodeIdToToken = new ArrayList<>();
    private final Set<Character> singles = new HashSet<>();

    public void register(String regex, String token) {
        if (regex.isEmpty()) {
            return;
        }
        this.regex = regex;
        input = new StringInput(regex);
        NFA regexNfa = expr(false);
        setAcceptedNode(regexNfa, token);
        if (nfa == null) {
            nfa = regexNfa;
        } else {
            NFANode newStart = new NFANode();
            newStart.addEpsilonEdge(regexNfa.start, nfa.start);
            nfa.start = newStart;
        }
    }

    public void registerSingle(char ... chs) {
        for (char ch : chs) {
            if (ch > 128) {
                throw new IllegalArgumentException("ASCII only");
            }
            if (singles.contains(ch)) {
                continue;
            }
            singles.add(ch);
            register("\\" + ch, "single");
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

    private NFA expr(boolean group) {
        NFA result = new NFA();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case ')' -> {
                    if (!group) {
                        err("unclosed group");
                    }
                    return result;
                }
                case '|' -> {
                    if (!input.available()) {
                        err("missing expr");
                    }
                    input.read();
                    result.or(seq());
                }
                default -> result.and(seq());
            }
        }
        if (group) {
            throw new RuntimeException("failed to parse regex(unclosed group):" + regex);
        }
        return result;
    }

    private NFA seq() {
        input.retract();
        NFA result = new NFA();
        while (input.available()) {
            int b = input.read();
            switch (b) {
                case '(' -> result.and(checkClosure(expr(true)));
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
        ICharPredicate predicate = null;
        switch (b) {
            case '[' -> predicate = clazz();
            case '\\' -> {
                if (!input.available()) {
                    err("incomplete skipping");
                }
                predicate = ICharPredicate.single(input.read());
            }
            case '@' -> predicate = escape();
            case '*', '?', '+' -> err("invalid closure symbol");
            default -> predicate = ICharPredicate.single(b);
        }
        if (predicate == null) {
            err("invalid predicate");
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
                case '[' -> {
                    ICharPredicate clazz = clazz();
                    if (clazz == null) {
                        return null;
                    }
                    result = ICharPredicate.or(result, clazz);
                }
                case ']' -> {
                    return result;
                }
                default -> {
                    ICharPredicate p = minClazzPredicate();
                    if (p == null) {
                        return null;
                    }
                    result = ICharPredicate.or(result, p);
                }
            }
        }
        return result;
    }

    private ICharPredicate minClazzPredicate() {
        input.retract();
        int b = input.read();
        if (b == '^') {
            Set<Integer> excluded = new HashSet<>();
            LOOP:
            while (input.available()) {
                int b1 = input.read();
                switch (b1) {
                    case '[', ']' -> {
                        input.retract();
                        break LOOP;
                    }
                    case '\\' -> {}
                    default -> excluded.add(b1);
                }
            }
            return c -> !excluded.contains(c);
        }
        if (!input.available()) {
            return null;
        }
        int b1 = input.read();
        if (b1 == '-') {
            if (!input.available()) {
                return null;
            }
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

    private ICharPredicate escape() {
        if (!input.available()) {
            return null;
        }
        return switch (input.read()) {
            case 'D', 'd' -> ASCII::isDigit;
            case 'W', 'w' -> ASCII::isWord;
            case 'U', 'u' -> ASCII::isAlnum;
            case 'H', 'h' -> ASCII::isHex;
            case 'A', 'a' -> ASCII::isAlpha;
            case '.' -> ch -> true;
            default -> null;
        };
    }

    private void err(String cause) {
        throw new RuntimeException("failed to parse regex(" + cause + "):" + regex);
    }
}
