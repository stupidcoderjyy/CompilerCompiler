package stupidcoder.fieldparser;

public class NoSuchFieldException extends RuntimeException{

    public NoSuchFieldException(String message) {
        super(message);
    }
}
