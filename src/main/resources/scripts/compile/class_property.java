$class_info{}

$f[headName]{"public class Property%s implements IProperty {"}

    @Override
    public void onReduced(Production p, IProperty... properties) {
        $switch[switchId]{
            $c{%
                switch (p.id()) {
                $r{
                    $c{%
                        case $f[pId]{"%d"} -> reduce$f[funcId1]{"%d("}
                        $r{$f{"(%s) properties[%d]"}, I5, ","}
                        );
                    %, I3}
                }
                }
            %, I2},
            $c{
                $f{"reduce0("} +
                $r{$f[reduceArgs1]{"%s p%d"}, I3L0, postfix:",\r\n", last-postfix:");"}
            , I1}
        }
    }

    $r{
        $c{
            $f[funcId2]{"private void reduce%d("} +
            $r[reduceArgs2]{$f{"%s p%d"}, I3L0, postfix:",\r\n", last-postfix:"){\r\n\r\n"} +
            "}"
        , I1}
    }
}