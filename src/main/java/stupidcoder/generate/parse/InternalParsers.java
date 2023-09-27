package stupidcoder.generate.parse;

import stupidcoder.generate.parse.parsers.*;

public class InternalParsers {
    public static final Parser ARG_COMPLEX_PARAGRAPH = new ParserComplexArg0Paragraph();
    public static final Parser ARG_COMPLEX_LINE = new ParserComplexArg0Line();
    public static final Parser ARG_COMMON_OUTPUT_CONFIG = new ParserArgConfig();
    public static final Parser ARG_COMMON_OUTPUT_FIELD = new ParserOutputField();
    public static final Parser UNIT_COMPLEX = new ParserUnitComplex();
    public static final Parser UNIT_CONST = new ParserUnitConst();
    public static final Parser UNIT_REPEAT = new ParserUnitRepeat();
    public static final Parser UNIT_FORMAT = new ParserUnitFormat();
    public static final Parser UNIT_SWITCH = new ParserUnitSwitch();
    public static final Parser UNIT = new ParserUnit();
}
