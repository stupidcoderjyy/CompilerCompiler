package stupidcoder.fieldparser.internal;

import stupidcoder.common.token.IToken;

import java.util.List;

public interface IActions{
    /**
     * $start$ → stmts
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionA(List<IToken> tokens);

    /**
     * stmts → stmt
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionB(List<IToken> tokens);

    /**
     * stmts → stmts stmt
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionC(List<IToken> tokens);

    /**
     * stmt → @id : val ;
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionD(List<IToken> tokens);

    /**
     * val → @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionE(List<IToken> tokens);

    /**
     * val → list
     *
     * @param tokens 产生式中所有的终结符号
     */

    void actionF(List<IToken> tokens);

    /**
     * list → [ strings ]
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionG(List<IToken> tokens);

    /**
     * strings → @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionH(List<IToken> tokens);

    /**
     * strings → strings , @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionI(List<IToken> tokens);

    /**
     * strings → ε
     *
     * @param tokens 产生式中所有的终结符号
     */
    void actionJ(List<IToken> tokens);
}
