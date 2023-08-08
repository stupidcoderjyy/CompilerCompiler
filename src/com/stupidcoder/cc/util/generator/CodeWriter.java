package com.stupidcoder.cc.util.generator;

import com.stupidcoder.cc.Config;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class CodeWriter {
    private final String outputRoot;
    private static CodeWriter globalInstance;
    private final String rootPackage;
    private final Map<String, XWritable> writableMap = new HashMap<>();
    private final Set<String> dirs = new HashSet<>();

    public CodeWriter(String outputRoot, String rootPackage) {
        this.outputRoot = outputRoot + "/" + rootPackage.replace('.', '/');
        this.rootPackage = rootPackage;
        dirs.add("");
    }

    public static CodeWriter getGlobalInstance() {
        if (globalInstance == null) {
            globalInstance = new CodeWriter(Config.OUTPUT_ROOT, Config.OUTPUT_ROOT_PACKAGE);
        }
        return globalInstance;
    }

    public void registerWritable(String outPath, XWritable writable) {
        if (writable instanceof XClass clazz) {
            registerClazz(clazz);
            return;
        }
        writableMap.put(outPath, writable);
    }

    public void registerCopy(String outDir, String outFile, String srcPath) {
        if (outDir.isEmpty()) {
            writableMap.put(outFile, XFile.of(srcPath));
            return;
        }
        writableMap.put(outDir + "/" + outFile, XFile.of(srcPath));
        dirs.add(outDir);
    }

    public void registerClazzCopy(String outDir, String clazzName, String srcPath) {
        String pkgName, outPath;
        if (outDir.isEmpty()) {
            pkgName = "package " + rootPackage + ";\r\n";
            outPath = clazzName + ".java";
        } else {
            pkgName = "package " + rootPackage + "." + outDir.replace('/', '.') + ";\r\n";
            outPath = outDir + "/" + clazzName + ".java";
        }
        XWritableList list = new XWritableList();
        list.append(pkgName);
        list.append(XFile.of(srcPath));
        writableMap.put(outPath, list);
        dirs.add(outDir);
    }

    public void registerClazz(XClass clazz) {
        String dir = clazz.childPackage.replace('.', '/');
        dirs.add(dir);
        String filePath = dir + "/" + clazz.name + ".java";
        writableMap.put(filePath, clazz);
        clazz.setClazzPackage(rootPackage);
    }

    public void output() {
        writableMap.forEach((file, writable) -> {
            try {
                for (String dir : dirs) {
                    if (dir.isEmpty()) {
                        new File(outputRoot).mkdirs();
                    } else {
                        new File(outputRoot + "/" + dir).mkdirs();
                    }
                }

                File f = new File(outputRoot + "/" + file);
                f.createNewFile();

                try (FileWriter writer = new FileWriter(f)) {
                    writer.write("//AutoGen " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\r\n");
                    writable.output(writer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        clear();
    }

    public void clear() {
        writableMap.clear();
        dirs.clear();
    }
}
