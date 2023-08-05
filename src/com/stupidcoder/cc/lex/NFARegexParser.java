package com.stupidcoder.cc.lex;

import com.stupidcoder.cc.util.input.ILexerInput;

public class NFARegexParser {
    private final ILexerInput input;
    private NFA tempGroup;

    private NFARegexParser(ILexerInput input) {
        this.input = input;
    }

    public static NFA parse(ILexerInput input) {
        return new NFARegexParser(input).expr();
    }

    private NFA expr() {
        NFA result = new NFA();
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case ')' -> {
                    return checkClosure(result);
                }
                case '|' -> {
                    input.checkAvailable();
                    input.next();
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
            byte b = input.next();
            switch (b) {
                case '(' -> result.and(expr());
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
        byte b = input.next();
        ICharPredicate predicate;
        if (b == '[') {
            predicate = clazz();
        } else if (b == '\\' && input.available()) {
            predicate = ICharPredicate.single(input.next());
        } else {
            predicate = ICharPredicate.single(b);
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
                        result = ICharPredicate.or(result, ICharPredicate.single(b1));
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
}
