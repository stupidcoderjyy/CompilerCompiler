package stupidcoder;

public class ConsoleUtil
{
    public static void printRed(String info) {
        System.out.printf("\033[31m%s\33[0m",info);
    }

    public static void printYellow(String info) {
        System.out.printf("\033[33m%s\33[0m",info);
    }

    public static void printPurple(String info) {
        System.out.printf("\033[35m%s\33[0m",info);
    }

    public static void printGreen(String info) {
        System.out.printf("\033[32m%s\33[0m",info);
    }

    public static void printBlue(String info) {
        System.out.printf("\033[34m%s\33[0m",info);
    }

    public static void printHighlightWhite(String info) {
        System.out.printf("\033[7m%s\33[0m",info);
    }

    public static void printHighlightPurple(String info) {
        System.out.printf("\033[45m%s\33[0m",info);
    }

    public static void printHighlightYellow(String info) {
        System.out.printf("\033[43m%s\33[0m",info);
    }

    public static void printHighlightBlue(String info) {
        System.out.printf("\033[44m%s\33[0m",info);
    }
}
