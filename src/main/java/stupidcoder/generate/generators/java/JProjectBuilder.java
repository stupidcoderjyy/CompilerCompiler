package stupidcoder.generate.generators.java;

import stupidcoder.Config;
import stupidcoder.generate.Source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JProjectBuilder {
    private static final Pattern CLAZZ_NAME = Pattern.compile(".*\\.java");
    private static final Pattern DOT = Pattern.compile("\\.");
    private final String outputRoot;
    private final String scriptsRoot;
    private final Map<String, List<JClassGen>> nameToClazz = new HashMap<>();
    final JPkgGen rootPkgGen;

    public JProjectBuilder(String rootScriptPath, String rootOutPath) {
        this.outputRoot = rootOutPath;
        this.scriptsRoot = rootScriptPath;
        this.rootPkgGen = new JPkgGen(null);
        new File(outputRoot).mkdirs();
        init();
    }

    private void init() {
        try {
            URL url = getClass().getResource(Config.resourcePath(scriptsRoot));
            if (url == null) {
                throw new FileNotFoundException(scriptsRoot);
            }
            Path p = Path.of(url.toURI());
            Stack<JPkgGen> unchecked = new Stack<>();
            Stack<Path> paths = new Stack<>();
            Path root = initRoot(p);
            unchecked.push(rootPkgGen);
            paths.push(root);
            while (!unchecked.empty()) {
                JPkgGen parent = unchecked.pop();
                try (Stream<Path> path = Files.walk(paths.pop(), 1)){
                    path.skip(1).forEach(f -> {
                        if (Files.isDirectory(f)) {
                            unchecked.push(registerPkg(f, parent));
                            paths.push(f);
                        } else {
                            JClassGen clazz = registerClazz(parent, root, f);
                            parent.targets.put(clazz.clazzName, clazz);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPkgGen registerPkg(Path path, JPkgGen parent) {
        JPkgGen g = new JPkgGen(parent);
        String dirName = path.getFileName().toString();
        g.pkgName = parent.pkgName + "." + dirName;
        g.outPath = parent.outPath + "/" + dirName;
        rootPkgGen.childPackages.put(dirName, g);
        return g;
    }

    private Path initRoot(Path scrRoot) throws IOException {
        try(Stream<Path> path = Files.walk(scrRoot, 1)) {
            Optional<Path> first = path.filter(Files::isDirectory).skip(1).findFirst();
            if (first.isPresent()) {
                String name = first.get().getFileName().toString();
                rootPkgGen.pkgName = name;
                rootPkgGen.outPath = outputRoot + "/" + name;
                return first.get();
            }
        }
        return scrRoot;
    }

    public void gen() {
        rootPkgGen.gen();
    }

    public void registerClazzSrc(String clazzName, Source src) {
        findClass(clazzName).registerSrc(src);
    }

    public void registerPackageSrc(String pkg, Source src) {
        findPkg(pkg).registerSrc(src);
    }

    public void registerGlobalSrc(Source src) {
        rootPkgGen.registerSrc(src);
    }

    public void excludePkg(String pkg) {
        findPkg(pkg).excluded = true;
    }

    public void excludeClazz(String clazz) {
        findClass(clazz).excluded = true;
    }

    JPkgGen findPkg(String pkg) {
        if (pkg.isEmpty()) {
            return rootPkgGen;
        }
        String[] elements = DOT.split(pkg);
        JPkgGen g = rootPkgGen;
        for (String element : elements) {
            g = g.childPackages.get(element);
            if (g == null) {
                return null;
            }
        }
        return g;
    }

    JPkgGen findOrCreatePkg(String[] clazzPath) {
        JPkgGen g = rootPkgGen;
        int i;
        for (i = 1 ; i < clazzPath.length - 1 ; i ++) {
            if (!g.childPackages.containsKey(clazzPath[i])) {
                break;
            }
            g = g.childPackages.get(clazzPath[i]);
        }
        while (i < clazzPath.length - 1) {
            JPkgGen child = new JPkgGen(g);
            child.pkgName = g.pkgName + "." + clazzPath[i];
            child.outPath = g.outPath + "/" + clazzPath[i];
            g.childPackages.put(clazzPath[i], child);
            g = child;
            i++;
        }
        return g;
    }

    JClassGen findClass(String clazzName) {
        String[] elements = DOT.split(clazzName);
        JClassGen clazz = null;
        if (elements.length == 1) {
            clazz = fastFindClass(clazzName);
        } else {
            String pkg = elements[0];
            List<JClassGen> candidates = nameToClazz.get(elements[1]);
            for (JClassGen c : candidates) {
                if (c.parent.pkgName.equals(pkg)) {
                    clazz = c;
                    break;
                }
            }
        }
        if (clazz == null) {
            throw new RuntimeException("class not found: " + clazzName);
        }
        return clazz;
    }

    private JClassGen fastFindClass(String clazzName) {
        if (nameToClazz.containsKey(clazzName)) {
            List<JClassGen> gs = nameToClazz.get(clazzName);
            if (gs.size() == 1) {
                return gs.get(0);
            }
        }
        return null;
    }

    private JClassGen registerClazz(JPkgGen parent, Path root, Path clazzPath) {
        Path relative = root.relativize(clazzPath);
        String name = clazzPath.getFileName().toString();
        if (!CLAZZ_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("invalid file name: " + name);
        }
        name = name.substring(0, name.indexOf('.'));
        String scriptPath = scriptsRoot + "/" + rootPkgGen.pkgName + "/" + relative;
        String outPath = outputRoot + "/" + rootPkgGen.pkgName + "/" + relative;
        JClassGen clazz = new JClassGen(this, parent, name, outPath, scriptPath);
        if (nameToClazz.containsKey(name)) {
            nameToClazz.get(name).add(clazz);
        } else {
            List<JClassGen> list = new ArrayList<>();
            list.add(clazz);
            nameToClazz.put(name, list);
        }
        return clazz;
    }

    public void registerClazz(String clazzPath, String scriptPath) {
        scriptPath = scriptsRoot + "/" + scriptPath;
        String[] elements = DOT.split(clazzPath);
        if (elements.length == 1) {
            throw new IllegalArgumentException("invalid class path: " + clazzPath);
        }
        JPkgGen parent = findOrCreatePkg(elements);
        String name = elements[elements.length - 1];
        String outPath = parent.outPath + "/" + name + ".java";
        JClassGen clazz = new JClassGen(this, parent, name, outPath, scriptPath);
        parent.targets.put(name, clazz);
        if (nameToClazz.containsKey(name)) {
            nameToClazz.get(name).add(clazz);
        } else {
            List<JClassGen> list = new ArrayList<>();
            list.add(clazz);
            nameToClazz.put(name, list);
        }
    }
}
