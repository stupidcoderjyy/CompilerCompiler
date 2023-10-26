package stupidcoder.generate.project.java.importParser;

public class ImportParsingException extends RuntimeException{
    public ImportParsingException(String className, Exception e) {
        super("Failed to parse imports for class \"" + className + "\": " + e.getMessage());
    }
}
