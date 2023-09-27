package stupidcoder.generate;

public class OutUnitField {
    public final String name;
    public OutUnit value;

    public OutUnitField(OutUnit parent, String name) {
        this.name = name;
        parent.fields.put(name, this);
    }
}
