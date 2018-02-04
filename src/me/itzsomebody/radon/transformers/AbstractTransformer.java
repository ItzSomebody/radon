package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.CustomRegexUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTransformer implements Opcodes {
    private Map<String, ClassNode> classes;
    private Map<String, ClassNode> classPath;
    private Map<String, ClassNode> libraryClasses;
    private List<String> exemptClasses;
    private List<String> exemptMethods;
    private List<String> exemptFields;

    public void init(Map<String, ClassNode> classes,
                     List<String> exemptClasses,
                     List<String> exemptMethods,
                     List<String> exemptFields) {
        this.classes = classes;
        this.exemptClasses = exemptClasses;
        this.exemptMethods = exemptMethods;
        this.exemptFields = exemptFields;
    }

    public void init(Map<String, ClassNode> classes,
                     Map<String, ClassNode> classPath,
                     List<String> exemptClasses,
                     List<String> exemptMethods,
                     List<String> exemptFields) {
        this.classes = classes;
        this.classPath = classPath;
        this.exemptClasses = exemptClasses;
        this.exemptMethods = exemptMethods;
        this.exemptFields = exemptFields;
    }

    public Map<String, ClassNode> getClassMap() {
        return this.classes;
    }

    public Map<String, ClassNode> getClassPathMap() {
        return this.classPath;
    }

    public Collection<ClassNode> classNodes() {
        return this.classes.values();
    }

    public Collection<String> classNames() {
        return this.classes.keySet();
    }

    public long tookThisLong(long started) {
        return System.currentTimeMillis() - started;
    }

    public boolean classExempted(String name) {
        for (String string : exemptClasses) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    public boolean methodExempted(String name) {
        for (String string : exemptMethods) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    public boolean fieldExempted(String name) {
        for (String string : exemptFields) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    public ClassNode getClass(String name) {
        if (!classPath.containsKey(name)) throw new RuntimeException("Class " + name + " not in classpath.");
        return classPath.get(name);
    }

    public ClassNode getClassViaJar(String name) {
        if (!classes.containsKey(name)) throw new RuntimeException("Class " + name + " not in classpath.");
        return classes.get(name);
    }

    public Map<String, ClassNode> getLibraryClasses() {
        Map<String, ClassNode> classNodes = new HashMap<>();
        classNodes.putAll(classPath);
        classNodes.keySet().removeAll(classes.keySet());

        return classNodes;
    }

    public boolean isInherited(ClassNode clazz, MethodNode checkMethod) {
        for (MethodNode methodNode : clazz.methods) {
            if (methodNode.name.equals(checkMethod.name)
                    && methodNode.desc.equals(checkMethod.desc)) {
                return true;
            }
        }

        return false;
    }

    public void addMethod(String className, MethodNode methodNode) {
        classes.get(className).methods.add(methodNode);
    }

    public abstract void obfuscate();
}
