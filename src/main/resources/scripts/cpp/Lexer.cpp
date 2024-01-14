
#include "Lexer.h"
#include "MemUtil.h"
#include "CompilerInput.h"

$f[statesCount]{"#define statesCount %d", L};

Lexer::Lexer(CompilerInput *input):
        accepted(new bool[statesCount]),
        tokens(new TokenSupplier[statesCount]),
        input(input){
    goTo = allocateArray(statesCount, 128);
    init();
}

void Lexer::init() {
    $arr[goTo]{"goTo", "int", $f{"%s"}, I}

    $arr[accepted]{"accepted", "", $f{"%s"}, I}

    $arr[op]{"tokens", "TokenSupplier",
        $c{%
            [](const QString& lexeme, CompilerInput* in) {
            $f{"return (new Token%s())->onMatched(lexeme, in);"}
            }
        %},
        I
    }
}

Token *Lexer::run() {
    input->skip(' ');
    input->mark();
    if (!input->available()) {
        return TokenFileEnd::get();
    }
    $f[startState]{"int state = %d;", LI}
    int lastAccepted = -2;
    int extraLoadedBytes = 0;
    while (input->available()){
        int b = input->read();
        state = goTo[state][b];
        if (state == 0) {
            extraLoadedBytes++;
            break;
        }
        if (accepted[state]) {
            lastAccepted = state;
            extraLoadedBytes = 0;
        } else {
            extraLoadedBytes++;
        }
    }
    if (lastAccepted < 0 || !tokens[lastAccepted]) {
        input->approach('\r', ' ', '\t');
        throw input->errorMarkToForward("unexpected symbol");
    }
    input->retract(extraLoadedBytes);
    input->mark();
    return tokens[lastAccepted](input->capture(), input);
}

Lexer::~Lexer() {
    delete[] accepted;
    delete[] tokens;
    freeArray(goTo, statesCount);
}

$r[cppTokensCount]{
    $c[tokenImpl]{%
        Token *$f{"%s"}::onMatched(const QString &lexeme, CompilerInput *input) {
            $f{"return this;", LI}
        }
    %, L},
    %postfix:"\r\n",
    %last-postfix:"",
    %single-postfix:""
}

$s[keyWordEnabled]{
    "",
    "QMap<QString, int> TokenId::keyWords = init();"
}