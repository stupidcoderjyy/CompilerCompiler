
$f[project]{"#ifndef %s_SYNTAXANALYZER_H", L}
$f[project]{"#define %s_SYNTAXANALYZER_H", L}

#include "functional"
#include "Compiler.h"

class Lexer;

class SyntaxAnalyzer{
public:
    typedef std::function<Property*()> PropertySupplier;
private:
    int** actions{};
    int** goTo{};
    int* terminalRemap;
    Production** productions;
    PropertySupplier* suppliers;
    Lexer* lexer;
    std::vector<Symbol*> symbols{};

public:
    explicit SyntaxAnalyzer(Lexer* lexer);
    void run();
    virtual ~SyntaxAnalyzer();
private:
    void initActions();
    void initGoTo();
    void initOthers();
    void initGrammar();
};

$f[propertyClasses]{"class Property%s;", RL}

$r[propertyCount]{
    $c[propertyClassDef]{%
        $f{"class Property%s : public Property{"}
        public:
        $f{"void onReduced(Production *p, Property **properties) override;", I}
        private:
        $r{
            $c{
                $f{"void reduce%d("} +
                $r{
                    $f{"Property%s* p%d"},
                    %postfix:",",
                    %last-postfix:");",
                    %single-postfix:");"
                } +
                $f{" //%s%n"}
            , IL0}
        , L0}
    };
    %, I0L},
    %postfix:"\r\n",
    %single-postfix:"",
    %last-postfix:""
}

#endif
