#include "SyntaxAnalyzer.h"
#include "Compiler.h"
#include "MemUtil.h"
#include "Lexer.h"
#include "CompilerInput.h"

$c[data]{%
#define prodSize $f{"%d"}
#define remapSize $f{"%d"}
#define nonTerminalCount $f{"%d"}
#define terminalCount $f{"%d"}
#define statesCount $f{"%d"}
%, L}

SyntaxAnalyzer::SyntaxAnalyzer(Lexer* lexer):
        lexer(lexer),
        productions(new Production*[prodSize]),
        terminalRemap(allocateArray(remapSize)),
        suppliers(new PropertySupplier[nonTerminalCount]){
    goTo = allocateArray(statesCount,nonTerminalCount);
    actions = allocateArray(statesCount,terminalCount);
    initActions();
    initGoTo();
    initOthers();
    initGrammar();
}

void SyntaxAnalyzer::run() {
    auto* input = lexer->input;
    input->mark();
    QVector<int> states{};
    QVector<Property*> properties{};
    states.append(0);
    auto* token = lexer->run();
    auto* eof = TokenFileEnd::get();
    if (token == eof) {
        return;
    }
    while (true) {
        int s = states.last();
        int order = actions[s][terminalRemap[token->type()]];
        int type = order >> 16;
        int target = order & 0xFFFF;
        switch(type) {
            case 0: {
                if (token != eof) {
                    delete token;
                }
                input->recover(false);
                throw input->errorAtMark("syntax error");
            }
            case 1: {
                if (token != eof) {
                    delete token;
                }
                auto* body = new Property*{properties.takeLast()};
                suppliers[0]()->onReduced(productions[0], body);
                delete body[0];
                delete body;
                return;
            }
            case 2: {
                input->mark();
                states.append(target);
                properties.append(new PropertyTerminal(token));
                delete token;
                token = lexer->run();
                break;
            }
            case 3: {
                auto* p = productions[target];
                auto** body = new Property*[p->bodyLen]; //Property在调用完onReduced后立刻销毁
                for (int i = p->bodyLen - 1 ; i >= 0 ; i --) {
                    auto* symbol = p->body[i];
                    if (symbol->id < 0) {
                        continue; //ε
                    }
                    states.removeLast();
                    body[i] = properties.takeLast();
                    if (symbol->isTerminal) {
                        input->removeMark();
                    }
                }
                auto* pHead = suppliers[p->head->id]();
                pHead->onReduced(p, body);
                for (int i = 0 ; i < p->bodyLen ; i ++) {
                    delete body[i];
                }
                delete[] body;
                properties.append(pHead);
                states.append(goTo[states.last()][p->head->id]);
                break;
            }
        }
    }
}

SyntaxAnalyzer::~SyntaxAnalyzer() {
    for (int i = 0 ; i < prodSize ; i ++) {
        delete productions[i];
    }
    for (const auto &item: symbols) {
        delete item;
    }
    delete[] productions;
    delete[] terminalRemap;
    delete[] suppliers;
    freeArray(goTo, statesCount);
    freeArray(actions, statesCount);
}

const int ACCEPT = 0x10000;
const int SHIFT = 0x20000;
const int REDUCE = 0x30000;

void SyntaxAnalyzer::initActions() {
    $arr[actions]{"actions", "int", $f{"%s"}, I}
}

void SyntaxAnalyzer::initGoTo() {
    $arr[goTo]{"goTo", "int", $f{"%s"}, I}
}

void SyntaxAnalyzer::initOthers() {
    $f[remap]{"terminalRemap[%d] = %d;", ILR}
    $f[property]{"suppliers[%d] = [](){return new Property%s();};", ILR}
}

void SyntaxAnalyzer::initGrammar() {
    $s[syntax]{
        $f{"symbols.push_back(new Symbol(%s, %d));"},
        $c{
            $f{"productions[%d] = new Production(%d, symbols[%d], %d, new Symbol*[]{"} +
            $r{
                $f{"symbols[%d]"},
                %postfix:", ",
                %last-postfix:"",
                %single-postfix:""
            } +
            $f{"}); //%s"}
        }
    , ILR}
}

$r[prodCount]{
    $c[propertyClassImpl]{%
        $f{"//%s"}
        $f{"void Property%s::reduce%d("}
        $r{
            $f{"Property%s* p%d", I2},
            %postfix:$f{",%n"},
            %single-postfix:$f{") {%n"},
            %last-postfix:$f{") {%n"}
        , L0I0}
        $f{"", I1}
        }
    %, LI0},
    %postfix:"\r\n",
    %single-postfix:"",
    %last-postfix:""
}

$r[propertyCountCpp]{
    $c[propertyReduceFunc]{%
        $f{"void Property%s::onReduced(Production *p, Property **properties) {"}
        $s{
             $c{%
                 reduce0(
                 $r{
                     $f{"static_cast<Property%s*>(properties[%d])", I3}
                     %postfix:",\r\n",
                     %single-postfix:");\r\n",
                     %last-postfix:");\r\n"
                 , L0I0}
             %, LI},
             $c{%
                 switch (p->id) {
                 $r{
                     $c{%
                         $f{"case %d: reduce%d("}
                         $r{
                            $f{"static_cast<Property%s*>(properties[%d])", I4},
                            %postfix:",\r\n",
                            %single-postfix:");\r\n",
                            %last-postfix:");\r\n"
                         , L0I0}
                         break;
                     %, LI2}
                 , L0I0}
                 $f{"}"}
             %, LI}
        , L0}
        $f{"}%n"}
    %, L}
}