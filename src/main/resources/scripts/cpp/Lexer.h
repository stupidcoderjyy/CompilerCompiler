
$f[project]{"#ifndef %s_LEXER_H", L}
$f[project]{"#define %s_LEXER_H", L}

#include "Compiler.h"

class CompilerInput;

class Lexer {
    friend class SyntaxAnalyzer;
public:
    typedef std::function<Token*(const QString&, CompilerInput*)> TokenSupplier;
private:
    bool* accepted;
    int** goTo{};
    TokenSupplier* tokens;
    CompilerInput* input;
public:
    explicit Lexer(CompilerInput* input);
    Token* run();
    virtual ~Lexer();
private:
    void init();
};

$r[tokensCount]{
    $c[tokenDef]{%
        $f{"class %s : public Token{"}
        public:
            $c{%
            int type() override {
                $f{"return %d;", LI2}
            }
            Token *onMatched(const QString &lexeme, CompilerInput *input) override;
            %, LI}
        };
    %, L},
    L0,
    %postfix:"\r\n",
    %last-postfix:""
}

$s[keyWordEnabled]{
    "",
    $c[tokenIdDef]{%
        class TokenId : public Token{
        private:
            $f{"static QMap<QString, int> keyWords;", LI}
        public:
            $f{"QString data{};", LI}
        public:
            $c{%
                int type() override {
                    $f{"return keyWords.value(data, %d)", LI2}
                }
                Token *onMatched(const QString &lexeme, CompilerInput *input) override {
                $c{%
                    data = lexeme;
                    return this;
                %, LI2}
                }
            %, LI}
        private:
            $c{%
                static QMap<QString, int> init() {
                    $f{"keyWords.insert(\"%s\", %d);", LRI2}
                }
            %, LI}
        }
    %, L}
}

#endif