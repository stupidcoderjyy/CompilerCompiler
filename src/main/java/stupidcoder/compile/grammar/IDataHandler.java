package stupidcoder.compile.grammar;

public interface IDataHandler {
    /**
     * 注册语法分析器的一条移入指令
     * @param from 当前状态
     * @param to 要进入的状态
     * @param inputTerminal 终结符号的id，在栈式语法分析器中，这个为栈顶的那个符号
     */
    void setActionShift(int from, int to, int inputTerminal);

    /**
     * 注册语法分析器的一条规约指令
     * @param state 当前状态
     * @param forward
     * @param productionId 被规约的产生式id
     */
    void setActionReduce(int state, int forward, int productionId);

    /**
     * 注册语法分析器的接受指令
     * @param state 目标状态
     * @param forward 向前看符号。当进入state状态，且栈顶符号为forward时语法分析器停止工作
     */
    void setActionAccept(int state, int forward);

    /**
     * 设置语法分析器的GOTO表
     * @param from 起始状态
     * @param to 目标状态
     * @param inputNonTerminal 输入的非终结符号id
     */
    void setGoto(int from, int to, int inputNonTerminal);

    /**
     * 设置终结符号id重映射。语法加载器中规定，所有符号的id都是连续的，非终结符号从0号开始往后排，终结符号排在非终结符号后面
     * @param origin 终结符号原本的id（字符的大小或者Token的id）
     * @param after 重映射后的id
     */
    void setTerminalSymbolIdRemap(int origin, int after);

    /**
     * 设置LR自动机状态个数
     * @param count 个数
     */
    void setStatesCount(int count);

    /**
     * 设置终结符号个数
     * @param count 个数
     */
    void setTerminalCount(int count);

    /**
     * 设置非终结符号个数
     * @param count 个数
     */
    void setNonTerminalCount(int count);
}
