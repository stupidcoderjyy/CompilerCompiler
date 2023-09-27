package stupidcoder.generate.parse;

import stupidcoder.generate.Generator;
import stupidcoder.generate.OutUnit;
import stupidcoder.util.ASCII;
import stupidcoder.util.input.BitClass;
import stupidcoder.util.input.CompileException;
import stupidcoder.util.input.CompilerInput;

public interface Parser {
    OutUnit parse(Generator g, CompilerInput input, OutUnit raw) throws CompileException;

    default void checkNext(CompilerInput input, int next) throws CompileException {
        input.skip(BitClass.BLANK);
        if (!input.available()) {
            throw input.errorAtForward("missing '" + (char)next + "'");
        }
        if (input.read() != next) {
            input.retract();
            throw input.errorAtForward("missing '" + (char)next + "'");
        }
    }

    default int next(CompilerInput input) {
        input.skip(BitClass.BLANK);
        return input.available() ? input.read() : -1;
    }

    default int approachNext(CompilerInput input) {
        input.skip(BitClass.BLANK);
        if (!input.available()) {
            return -1;
        }
        int b = input.read();
        input.retract();
        return b;
    }

    default String readStringArg(CompilerInput input) throws CompileException {
        checkNext(input, '\"');
        input.mark();
        if (input.approach('\"', '\r') != '\"') {
            throw input.errorMarkToForward("unclosed '\"'");
        }
        input.mark();
        input.read();
        return input.capture();
    }

    default boolean checkArgInterval(CompilerInput input) throws CompileException{
        switch (approachNext(input)) {
            case '}' -> {
                return true;
            }
            case ',' -> {
                input.read();
                input.skip(BitClass.BLANK);
                return false;
            }
            default -> {
                if (!input.available()) {
                    throw input.errorAtForward("missing '\"' or ','");
                }
                throw input.errorAtForward("unexpected symbol");
            }
        }
    }

    default String readIntString(CompilerInput input) {
        input.mark();
        while (input.available()) {
            if (!ASCII.isDigit(input.read())) {
                input.retract();
                break;
            }
        }
        input.mark();
        return input.capture();
    }
}
