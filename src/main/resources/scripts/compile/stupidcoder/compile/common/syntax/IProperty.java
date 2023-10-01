$head{"Production"}

public interface IProperty {
    void onReduced(Production p, IProperty ... properties);
}
