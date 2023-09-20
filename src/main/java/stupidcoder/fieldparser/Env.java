package stupidcoder.fieldparser;

import stupidcoder.common.token.IToken;
import stupidcoder.fieldparser.internal.DFA;
import stupidcoder.fieldparser.internal.Field;
import stupidcoder.fieldparser.internal.IActions;
import stupidcoder.util.input.IInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Env implements IActions {
    private static final int STRING = 1;
    private static final int STRING_ARR = 2;
    private final Map<String, Field> fieldMap = new HashMap<>();

    public static Env fromInput(IInput input) {
        Env env = new Env();
        new VarParser(env).run(new DFA(input));
        return env;
    }

    public String getStringField(String name) {
        return (String) getField(name, STRING);
    }

    public String[] getStringArr(String name) {
        return (String[]) getField(name, STRING_ARR);
    }

    private Object getField(String name, int type) {
        try {
            if (!fieldMap.containsKey(name)) {
                throw new NoSuchFieldException("name");
            }
            Field f = fieldMap.get(name);
            if (f.type != type) {
                throw new NoSuchFieldException(name + "(unmatched type)");
            }
            return f.value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return fieldMap.toString();
    }

    private Field temp;
    private List<String> strings = new ArrayList<>();

    /**
     * $start$ → stmts
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionA(List<IToken> tokens) {
    }

    /**
     * stmts → stmt
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionB(List<IToken> tokens) {
    }

    /**
     * stmts → stmts stmt
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionC(List<IToken> tokens) {
    }
    /**
     * stmt → @id : val ;
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionD(List<IToken> tokens) {
        temp.name = tokens.get(0).toString();
        fieldMap.put(temp.name, temp);
    }

    /**
     * val → @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionE(List<IToken> tokens) {
        temp = new Field();
        temp.value = tokens.get(0).toString();
        temp.type = STRING;
    }

    /**
     * val → list
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionF(List<IToken> tokens) {
    }

    /**
     * list → [ strings ]
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionG(List<IToken> tokens) {
        temp = new Field();
        temp.value = strings;
        temp.type = STRING_ARR;
        strings = new ArrayList<>();
    }

    /**
     * strings → @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionH(List<IToken> tokens) {
        strings.add(tokens.get(0).toString());
    }

    /**
     * strings → strings , @string
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionI(List<IToken> tokens) {
        strings.add(tokens.get(1).toString());
    }

    /**
     * strings → ε
     *
     * @param tokens 产生式中所有的终结符号
     */
    @Override
    public void actionJ(List<IToken> tokens) {
    }
}
