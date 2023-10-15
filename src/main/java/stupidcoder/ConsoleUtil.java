package stupidcoder;

public class ConsoleUtil
{
    public static void printRed(Object info) {
        System.out.printf("\033[31m%s\33[0m",info);
    }

    public static void printYellow(Object info) {
        System.out.printf("\033[33m%s\33[0m",info);
    }

    public static void printPurple(Object info) {
        System.out.printf("\033[35m%s\33[0m",info);
    }

    public static void printGreen(Object info) {
        System.out.printf("\033[32m%s\33[0m",info);
    }

    public static void printBlue(Object info) {
        System.out.printf("\033[34m%s\33[0m",info);
    }

    public static void printHighlightWhite(Object info) {
        System.out.printf("\033[7m%s\33[0m",info);
    }

    public static void printHighlightPurple(Object info) {
        System.out.printf("\033[45m%s\33[0m",info);
    }

    public static void printHighlightYellow(Object info) {
        System.out.printf("\033[43m%s\33[0m",info);
    }

    public static void printHighlightBlue(Object info) {
        System.out.printf("\033[44m%s\33[0m",info);
    }
}
