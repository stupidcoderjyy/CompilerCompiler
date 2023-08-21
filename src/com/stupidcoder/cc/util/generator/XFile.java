package com.stupidcoder.cc.util.generator;

import com.stupidcoder.cc.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class XFile extends XWritable{
    private final File path;

    private XFile(String srcRoot, String fileName) {
        this.path = new File(srcRoot + "/" + fileName);
        try {
            if (!path.exists()) {
                throw new FileNotFoundException(path.getPath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static XFile of(String fileName) {
        return new XFile("resources", fileName);
    }

    public static XFile of(String srcRoot, String fileName) {
        return new XFile(srcRoot, fileName);
    }

    @Override
    public void output(FileWriter writer) throws IOException {
        try(Scanner scanner = new Scanner(new FileInputStream(path), StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                writeIndentations(writer);
                writer.write(scanner.nextLine());
                if (scanner.hasNextLine()) {
                    writer.write("\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
