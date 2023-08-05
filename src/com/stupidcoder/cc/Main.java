package com.stupidcoder.cc;

import com.stupidcoder.cc.lex.DFABuilder;
import com.stupidcoder.cc.lex.NFA;
import com.stupidcoder.cc.lex.NFANode;
import com.stupidcoder.cc.lex.NFARegexParser;
import com.stupidcoder.cc.util.input.StringInput;

import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        NFA nfa = NFARegexParser.parse(new StringInput("[0-9]+(L|l)?|0(x|X)[0-9A-Fa-f]+"));
        nfa.print();
    }
}
