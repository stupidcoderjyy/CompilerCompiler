package com.stupidcoder.cc.lex;

import com.stupidcoder.cc.util.input.ILexerInput;

public class NFARegexParser {
    private final ILexerInput input;

    private NFARegexParser(ILexerInput input) {
        this.input = input;
    }

    public static NFA parse(ILexerInput input) {
        return new NFARegexParser(input).parseExpr();
    }

    private NFA parseExpr() {
        NFA expr = new NFA();
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case '(' -> expr.and(parseExpr());
                case ')' -> {
                    //规定：右括号在递归返回后处理
                    if (input.available()) {
                        switch (input.next()) {
                            case '*' -> expr.star();
                            case '+' -> expr.plus();
                            case '?' -> expr.quest();
                            default -> input.retract();
                        }
                    }
                    return expr;
                }
                case '|' -> expr.or(parseExpr());
                default -> expr.and(parseSeq());
            }
        }
        return expr;
    }

    public NFA parseSeq() {
        NFA seq = new NFA();
        input.retract();
        LOOP:
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case '(':
                case ')':
                case '|':
                    input.retract();
                    break LOOP;
                case '\\':
                    input.next();
                    //把下一个字符当成atom处理
                default:
                    NFA atom = parseAtom();
                    if (input.available()) {
                        switch (input.next()) {
                            case '*' -> atom.star();
                            case '+' -> atom.plus();
                            case '?' -> atom.quest();
                            default -> input.retract();
                        }
                    }
                    seq.and(atom);
                    break;
            }
        }
        return seq;
    }

    private NFA parseAtom() {
        input.retract();
        byte b = input.next();
        ICharPredicate predicate = b == '[' ?
                parseClazz() :
                ICharPredicate.single(b);
        return new NFA().andAtom(predicate);
    }

    private static final int CLEAR = 0;

    private static final int FIND_ATOM = 1;

    private static final int FIND_MINUS = 2;

    private ICharPredicate parseClazz() {
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
