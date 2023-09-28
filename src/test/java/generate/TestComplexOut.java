package generate;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.Generator;
import stupidcoder.generate.sources.SourceCached;

public class TestComplexOut {

    @Test
    public void testBasic1() {
        Generator g = new Generator();
        g.loadScript("generate/out/complex/basic1.txt", "cpx_basic1.txt");
    }

    @Test
    public void testBasic2() {
        Generator g = new Generator();
        SourceCached src1 = new SourceCached("src1");
        src1.writeInt(25);
        SourceCached src2 = new SourceCached("src2");
        src2.writeInt(100);
        g.registerSrc(src1);
        g.registerSrc(src2);
        g.loadScript("generate/out/complex/basic2.txt", "cpx_basic2.txt");
    }

    @Test
    public void testBasic3() {
        Generator g = new Generator();
        SourceCached src1 = new SourceCached("src1");
        src1.writeInt(1);
        src1.writeInt(2);
        src1.writeInt(3);
        g.registerSrc(src1);
        g.loadScript("generate/out/complex/basic3.txt", "cpx_basic3.txt");
    }

    @Test
    public void testBasic4() {
        Generator g = new Generator();
        SourceCached src1 = new SourceCached("src1");
        src1.writeInt(1);
        src1.writeInt(2);
        src1.writeInt(3);
        g.registerSrc(src1);
        g.loadScript("generate/out/complex/basic4.txt", "cpx_basic4.txt");
    }

    @Test
    public void testErr() {
        Generator g = new Generator();
        for (int i = 1 ; i <= 2 ; i ++) {
            g.loadScript(String.format("generate/out/complex/err%d.txt", i), "err.txt");
        }
    }
}
