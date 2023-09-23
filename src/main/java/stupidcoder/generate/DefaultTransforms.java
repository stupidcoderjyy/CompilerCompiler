package stupidcoder.generate;

public class DefaultTransforms {
    public static final Transform PLAIN = new TransformPlain();
    public static final Transform FORMAT = new TransformFormat();
    public static final Transform CONST = new TransformConstString();
    public static final Transform COMPLEX = new TransformComplex();
}
