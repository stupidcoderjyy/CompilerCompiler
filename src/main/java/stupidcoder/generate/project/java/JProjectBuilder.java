package stupidcoder.generate.project.java;

import stupidcoder.Config;
import stupidcoder.generate.Source;
import stupidcoder.generate.project.java.importParser.ImportParser;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JProjectBuilder {
    private static final Pattern CLAZZ_NAME = Pattern.compile(".*\\.java");
    private static final Pattern DOT = Pattern.compile("\\.");
    private final List<IJavaProjectBuilder> builders = new ArrayList<>();
    private final String scriptsRoot;
    private final Map<String, Map<String, JClassGen>> nameToClazz = new HashMap<>();
    final ImportParser parser;
    final JPkgGen rootPkgGen;
    Path root;

    public JProjectBuilder(String rootScriptPath, String rootPkg) {
        if (rootPkg.isEmpty()) {
            throw new IllegalArgumentException("empty rootPkg");
        }
        this.scriptsRoot = rootScriptPath;
        this.rootPkgGen = new JPkgGen(rootPkg);
        this.parser = new ImportParser();
        init();
    }

    public void addBuilder(IJavaProjectBuilder builder) {
        builders.add(builder);
    }

    private void init() {
        try {
            URL url = getClass().getResource(Config.resourcePath(scriptsRoot));
            if (url == null) {
                throw new ProjectBuildingException("script root path doesn't exist:" + scriptsRoot);
            }
            root = Path.of(url.toURI());
            loadFiles(root, rootPkgGen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFiles(Path parentPath, JPkgGen parent) {
        try (Stream<Path> paths = Files.walk(parentPath, 1)){
            paths.skip(1).forEach(f -> {
                if (Files.isDirectory(f)) {
                    JPkgGen child = registerPkg(f, parent);
                    loadFiles(f, child);
                } else {
                    JClassGen clazz = registerClazz(parent, f);
                    parent.targets.put(clazz.clazzName, clazz);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPkgGen registerPkg(Path path, JPkgGen parent) {
        String dirName = path.getFileName().toString();
        JPkgGen g = new JPkgGen(parent, dirName);
        parent.childPackages.put(dirName, g);
        return g;
    }

    public void gen() {
        builders.forEach(a -> a.loadSource(this));
        rootPkgGen.gen();
    }

    public void registerClazzSrc(String clazzName, Source ... src) {
        JClassGen clazz = findClass(clazzName);
        for (Source s : src) {
            clazz.registerSrc(s);
        }
    }

    public void addClazzInternalImport(String className, String ... imported) {
        JClassGen clazz = findClass(className);
        for (String s : imported) {
            clazz.addInternalClazzImport(s);
        }
    }

    public void addClazzPkgImport(String className, String pkg) {
        findClass(className).addPkgImport(pkg);
    }

    public void excludePkg(String childPkg) {
        findPkg(childPkg).excluded = true;
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
                throw new IllegalArgumentException("pkg not found: " + rootPkgGen.pkgName + "." + pkg);
            }
        }
        return g;
    }

    JClassGen findClass(String clazzName) {
        int i = clazzName.lastIndexOf('.');
        if (i < 0) {
            Map<String, JClassGen> map = nameToClazz.get(clazzName);
            if (map == null) {
                throw new IllegalArgumentException("class not found:" + clazzName);
            }
            if (map.size() > 1) {
                throw new IllegalArgumentException("too many classes called \"" + clazzName + "\"");
            }
            return map.entrySet().iterator().next().getValue();
        } else {
            String pkg = clazzName.substring(0, i);
            Map<String, JClassGen> map = nameToClazz.get(clazzName.substring(i + 1));
            if (map == null) {
                throw new IllegalArgumentException("class not found:" + clazzName);
            }
            if (map.size() == 1) {
                var e = map.entrySet().iterator().next();
                if (!e.getKey().equals(pkg)) {
                    throw new IllegalArgumentException("class not found:" + clazzName);
                }
                return e.getValue();
            }
            if (!map.containsKey(pkg)) {
                throw new IllegalArgumentException("class not found:" + clazzName);
            }
            return map.get(pkg);
        }
    }

    private JClassGen registerClazz(JPkgGen parent, Path clazzPath) {
        Path relative = root.relativize(clazzPath);
        String name = clazzPath.getFileName().toString();
        if (!CLAZZ_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("invalid file name: " + name);
        }
        name = name.substring(0, name.indexOf('.'));
        String scriptPath = scriptsRoot + "/" + relative;
        String outPath = rootPkgGen.outPath + "/" + relative;
        JClassGen clazz = new JClassGen(this, parent, name, outPath, scriptPath);
        putClassMap(parent, clazz, name);
        return clazz;
    }

    public void registerClazz(String clazzPath, String scriptPath) {
        scriptPath = scriptsRoot + "/" + scriptPath;
        String[] elements = DOT.split(clazzPath);
        if (elements.length == 1) {
            throw new IllegalArgumentException("missing pkg: " + clazzPath);
        }
        JPkgGen parent = findOrCreatePkg(elements);
        String name = elements[elements.length - 1];
        String outPath = parent.outPath + "/" + name + ".java";
        JClassGen clazz = new JClassGen(this, parent, name, outPath, scriptPath);
        parent.targets.put(name, clazz);
        putClassMap(parent, clazz, name);
    }

    private void putClassMap(JPkgGen parent, JClassGen clazz, String name) {
        if (nameToClazz.containsKey(name)) {
            Map<String, JClassGen> map = nameToClazz.get(name);
            if (map.containsKey(parent.pkgName)) {
                throw new IllegalArgumentException("class \"" + name + "\" already exist in pkg \"" + parent.pkgName + "\"");
            }
            map.put(parent.pkgName, clazz);
        } else {
            Map<String, JClassGen> map = new HashMap<>();
            map.put(parent.pkgName, clazz);
            nameToClazz.put(name, map);
        }
    }

    private JPkgGen findOrCreatePkg(String[] clazzPath) {
        JPkgGen g = rootPkgGen;
        int i;
        for (i = 0 ; i < clazzPath.length - 1 ; i ++) {
            if (!g.childPackages.containsKey(clazzPath[i])) {
                break;
            }
            g = g.childPackages.get(clazzPath[i]);
        }
        while (i < clazzPath.length - 1) {
            JPkgGen child = new JPkgGen(g, clazzPath[i]);
            g.childPackages.put(clazzPath[i], child);
            g = child;
            i++;
        }
        return g;
    }
}
