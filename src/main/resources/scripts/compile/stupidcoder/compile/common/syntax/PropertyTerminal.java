$head{"Production", "IToken"}

public final class PropertyTerminal implements IProperty{
    public final IToken token;

    public PropertyTerminal(IToken token) {
        this.token = token;
    }

    public <T extends IToken> T getToken() {
        return (T) token;
    }

    @Override
    public void onReduced(Production p, IProperty... properties) {
        //终结符号
    }
}
