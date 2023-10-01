package stupidcoder;

import javax.swing.filechooser.FileSystemView;

public class PathUtil {
    public static String desktopPath() {
        return FileSystemView.getFileSystemView().getHomeDirectory().getPath();
    }

    public static String desktopPath(String childPath) {
        return desktopPath() + "\\" + childPath;
    }
}
