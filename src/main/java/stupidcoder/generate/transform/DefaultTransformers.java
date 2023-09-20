package stupidcoder.generate.transform;

import stupidcoder.generate.ITransform;

public class DefaultTransformers {
    public static final ITransform PLAIN = new PlainTransform();
    public static final ITransform FORMAT = new FormatTransform();
    public static final ITransform CONST = new ConstStringTransform();
}
