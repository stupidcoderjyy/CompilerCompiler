package stupidcoder.compile.grammar;

public interface IPriorityRegistry {
    /**
     * 注册『终结符号』之间的优先级关系。在语法分析器中，如果遇到了移入-规约冲突，向前看符号的优先级高于
     * 移入符号的优先级时执行规约，否则执行移入
     * @param largerTerminal 高优先级符号的词素
     * @param smallerTerminal 低优先级符号的词素
     */
    void registerPriority(String largerTerminal, String smallerTerminal, int difference);
}
