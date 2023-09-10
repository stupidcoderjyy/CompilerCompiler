package stupidcoder.grammar;

import stupidcoder.common.symbol.Symbol;

public interface IProductionRegistry {
    /**
     * 调用此方法开始注册一条产生式
     * @param lexeme 产生式的头（非终结符号）的词素
     * @return 调用者自己
     */
    IProductionRegistry begin(String lexeme);

    /**
     * 向产生式体的末尾添加一个符号
     * @param s 符号对象
     * @return 调用者自己
     */
    IProductionRegistry addSymbol(Symbol s);

    /**
     * 向产生式体的末尾添加一个非终结符号
     * @param lexeme 符号的词素
     * @return 调用者自己
     */
    IProductionRegistry addNonTerminal(String lexeme);

    /**
     * 向产生式体的末尾添加一个终结符号
     * @param lexeme 符号的词素
     * @param id 符号的id
     * @return 调用者自己
     */
    IProductionRegistry addTerminal(String lexeme, int id);

    /**
     * 向产生式体的末尾添加一个终结符号。这个终结符号的词素为单个字符
     * @param ch ASCII字符
     * @return 调用者自己
     */
    IProductionRegistry addTerminal(char ch);

    /**
     * 完成产生式的注册
     */
    void finish();
}
