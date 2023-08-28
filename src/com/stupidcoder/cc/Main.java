package com.stupidcoder.cc;


import com.stupidcoder.cc.lex.core.DFABuilder;
import com.stupidcoder.cc.lex.core.IDfaSetter;
import com.stupidcoder.cc.lex.core.NFARegexParser;

public class Main {
    public static void main(String[] args) {
        NFARegexParser parser = new NFARegexParser();
        parser.register("@d+(L|l)?|0(x|X)@h+", "integer");
        parser.getNfa().print();
        DFABuilder.build(new TestSetter(), parser);
    }

    private static class TestSetter implements IDfaSetter {

        @Override
        public void setAccepted(int i, String token) {
            System.out.printf("tokens[%d] = %s\n", i, token);
        }

        @Override
        public void setGoTo(int start, int input, int target) {
            System.out.printf("%d --'%c'-> %d\n", start, input, target);
        }

        @Override
        public void setStartState(int i) {
            System.out.println("start:" + i);
        }

        @Override
        public void setDfaStatesCount(int count) {

        }
    }
}
