package stupidcoder.compile.properties;

import stupidcoder.compile.common.syntax.IProperty;
import stupidcoder.compile.common.syntax.PropertyTerminal;
import stupidcoder.compile.common.Production;

$f[name]{"public class %s implements IProperty {"}

    @Override
    public void onReduced(Production p, IProperty... properties) {
        $s[reduceCall]{
             $c{%
                 reduce0(
                 $r{
                     $c{
                         $s{
                             $f{"(Property%s)"},
                             $f{"(PropertyTerminal)"}
                         } +
                         $f{"properties[%d]"}
                     , I3},
                     %postfix:$f{",%n"},
                     %single-postfix:$f{"%n"},
                     %last-postfix:$f{"%n"}
                 , L0I0}
                 );
             %, LI2},
             $c{%
                 switch (p.id()) {
                 $r{
                     $c{%
                         $f{"case %d -> reduce%d("}
                         $r{
                             $c{
                                 $s{
                                     $f{"(Property%s)"},
                                     $f{"(PropertyTerminal)"}
                                 } +
                                 $f{"properties[%d]"}
                             , I5},
                             %postfix:",\r\n",
                             %single-postfix:"",
                             %last-postfix:""
                         , I0}
                         );
                     %, LI3}
                 , L0I0}
                 }
             %, LI2}
         }
    }

    $r[reduceFunc]{
        $c{%
            $f{"//%s"}
            $f{"private void reduce%d("}
            $r{
                $c{
                    $s{
                        $f{"Property%s"},
                        $f{"PropertyTerminal"}
                     } +
                    $f{" p%d"}
                , I3},
                %postfix:$f{",%n"},
                %single-postfix:$f{") {%n"},
                %last-postfix:$f{") {%n"}
            , L0I0}
            $f{"", I2}
            }
        %, LI1},
        %postfix:"\r\n",
        %last-postfix:"",
        %single-postfix:""
    }
}
