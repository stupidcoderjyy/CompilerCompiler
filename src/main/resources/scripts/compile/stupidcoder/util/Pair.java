package stupidcoder.util;

public class Pair<L,R> {
    public L l;
    public R r;

    private Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public static <L,R> Pair<L,R> of(L l, R r) {
        return new Pair<>(l,r);
    }

    @Override
    public String toString() {
        return "<" + l + "," + r + ">";
    }

    public Pair<L, R> copy(){
        return new Pair<>(l, r);
    }

    public void set(L l, R r) {
        this.l = l;
        this.r = r;
    }
}
