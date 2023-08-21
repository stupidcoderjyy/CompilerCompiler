package com.stupidcoder.cc.lex.core;

import com.stupidcoder.cc.util.ASCII;
import com.stupidcoder.cc.util.ArrayUtil;
import com.stupidcoder.cc.util.input.ILexerInput;
import com.stupidcoder.cc.util.input.StringInput;

import java.util.ArrayList;
import java.util.List;

public class NFARegexParser {
    private ILexerInput input;
    private NFA nfa;
    private final List<String> nodeIdToToken = new ArrayList<>();

    public NFARegexParser() {
    }

    public void register(String regex, String token) {
        input = new StringInput(regex);
        NFA regexNfa = expr();
        setAcceptedNode(regexNfa, token);
        if (nfa == null) {
            nfa = regexNfa;
        } else {
            NFANode newStart = new NFANode();
            newStart.addEpsilonEdge(regexNfa.start);
            newStart.addEpsilonEdge(nfa.start);
            nfa.start = newStart;
        }
    }

    public NFA getNfa() {
        return nfa;
    }

    public List<String> getNodeIdToToken() {
        return nodeIdToToken;
    }

    private void setAcceptedNode(NFA target, String token) {
        target.end.accepted = true;
        ArrayUtil.resize(nodeIdToToken, target.end.id + 1);
        nodeIdToToken.set(target.end.id, token);
    }

    private NFA expr() {
        NFA result = new NFA();
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case ')' -> {
                    return result;
                }
                case '|' -> {
                    input.next();
                    result.or(seq()); //expr → seq ('|' seq)* 的后半部分
                }
                default -> result.and(seq());  //expr → seq ('|' seq)* 的第一个seq
            }
        }
        return result;
    }

    private NFA seq() {
        input.retract();
        NFA result = new NFA();
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case '(' -> result.and(checkClosure(expr())); // seq → '(' expr ')' closure?
                case '|', ')' -> {
                    input.retract();
                    return result; //回退一步，交给expr处理
                }
                default -> result.and(atom()); //seq → atom
            }
        }
        return result;
    }

    private NFA atom() {
        input.retract();
        byte b = input.next();
        ICharPredicate predicate;
        switch (b) {
            case '[' -> predicate = clazz();
            case '\\' -> predicate = ICharPredicate.single(input.next());
            case '@' -> predicate = escape(input.next());
            default -> predicate = ICharPredicate.single(b);
        }
        return checkClosure(new NFA().andAtom(predicate));
    }

    private NFA checkClosure(NFA target) {
        if (input.available()) {
            switch (input.next()) {
                case '*' -> target.star();
                case '+' -> target.plus();
                case '?' -> target.quest();
                default -> input.retract();
            }
        }
        return target;
    }

    private static final int CLEAR = 0;
    private static final int FIND_ATOM = 1;
    private static final int FIND_MINUS = 2;

    private ICharPredicate clazz() {
        ICharPredicate result = null;
        int state = CLEAR;
        byte b1 = 0;
        LOOP:
        while (input.available()) {
            byte b = input.next();
            switch (state) {
                case CLEAR -> {
                    if (b == ']') {
                        break LOOP;
                    }
                    b1 = b;
                    state = FIND_ATOM;
                }
                case FIND_ATOM -> {
                    if (b == '-') {
                        state = FIND_MINUS;
                    } else if (b == ']') {
                        break LOOP;
                    } else {
                        result = ICharPredicate.or(result, ICharPredicate.or(b, b1));
                        state = CLEAR;
                    }
                }
                case FIND_MINUS -> {
                    if (b == ']') {
                        result = ICharPredicate.or(result, ICharPredicate.single('-'));
                        break LOOP;
                    } else {
                        result = ICharPredicate.or(result, ICharPredicate.ranged(b1, b));
                    }
                    state = CLEAR;
                }
            }
        }
        return result;
    }

    private ICharPredicate escape(byte b) {
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
