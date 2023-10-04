package stupidcoder.core.properties;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.DefaultSymbols;
import stupidcoder.common.syntax.IProperty;
import stupidcoder.common.syntax.PropertyTerminal;
import stupidcoder.compile.lex.NFARegexParser;
import stupidcoder.compile.syntax.SyntaxLoader;
import stupidcoder.core.ScriptLoader;
import stupidcoder.core.tokens.TokenId;
import stupidcoder.core.tokens.TokenTerminal;

public class PropertySymbol implements IProperty {
    private final SyntaxLoader loader;
    private final ScriptLoader env;
    private final NFARegexParser parser;
    private static int terminalId = 128;

    public PropertySymbol(SyntaxLoader loader, ScriptLoader env, NFARegexParser parser) {
        this.loader = loader;
        this.env = env;
        this.parser = parser;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        switch (p.id()) {
            case 17 -> reduce0(
                    (PropertyTerminal)properties[0]
            );
            case 18 -> reduce1(
                    (PropertyTerminal)properties[0],
                    (PropertyPriorityT)properties[1]
            );
        }
    }

    //symbol → id
    private void reduce0(
            PropertyTerminal p0) {
        TokenId t0 = p0.getToken();
        loader.addNonTerminal(t0.lexeme);
    }

    //symbol → terminal priorityT
    private void reduce1(
            PropertyTerminal p0,
            PropertyPriorityT p1) {
        TokenTerminal t0 = p0.getToken();
        switch (t0.terminalType) {
            case TokenTerminal.EPSILON -> loader.add(DefaultSymbols.EPSILON);
            case TokenTerminal.NORMAL -> {
                if (env.nameToTerminalId.containsKey(t0.lexeme)) {
                    loader.addTerminal(t0.lexeme, env.nameToTerminalId.get(t0.lexeme));
                } else {
                    int id = terminalId++;
                    loader.addTerminal(t0.lexeme, id);
                    env.nameToTerminalId.put(t0.lexeme, id);
                }
            }
            case TokenTerminal.SINGLE -> {
                parser.registerSingle(t0.ch);
                loader.addTerminal(t0.ch);
            }
        }
        if (p1.value != 0) {
            loader.setPriority(p1.value);
        }
    }
}