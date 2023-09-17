package stupidcoder.fieldparser.internal;

public class Field {
    public int type;
    public Object value;
    public String name;

    @Override
    public String toString() {
        return name + ":" + value;
    }
}
