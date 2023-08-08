package com.stupidcoder.cc.lex.core;

public interface IDfaSetter {
    void setAccepted(int i);
    void setGoTo(int start, byte input, int target);
    void setStartState(int i);
    void setToken(int i, String token);
    void setDfaStatesCount(int count);
}
