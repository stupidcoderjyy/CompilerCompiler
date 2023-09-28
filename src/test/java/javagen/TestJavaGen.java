package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;

public class TestJavaGen {

    @Test
    public void test1() {
        JProjectBuilder builder = new JProjectBuilder("srcJavaGen", "javaGen");
        SourceCached src = new SourceCached("src");
        for (int i = 0; i < 10; i++) {
            src.writeInt(i);
        }
        builder.registerClazzSrc("TestClazz", src);
        builder.gen();
    }

    @Test
    public void test2() {
        JProjectBuilder builder = new JProjectBuilder("srcJavaGen", "javaGen");
        builder.excludePkg("excluded");
        builder.excludeClazz("TestClazz");
        for (int i = 0 ; i < 2 ; i ++) {
            builder.registerClazz(
                    "stupidcoder.instance.I" + i,
                    "stupidcoder/excluded/TestTemplate.java");
            SourceCached src = new SourceCached("src");
            src.writeInt(i);
            builder.registerClazzSrc("I" + i, src);
        }
        builder.gen();
    }
}
