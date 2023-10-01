package javagen;

import org.junit.jupiter.api.Test;
import stupidcoder.generate.generators.java.JProjectBuilder;
import stupidcoder.generate.sources.SourceCached;

public class TestJavaGen {

    @Test
    public void testImport() {
        JProjectBuilder builder = new JProjectBuilder("generate/java", "testJavaGen");
        builder.addClazzImport("Test", "Test", "Test1");
        builder.excludeClazz("Test1");
        builder.excludeClazz("Test2");
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
