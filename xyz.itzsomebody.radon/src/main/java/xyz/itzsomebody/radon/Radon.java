/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package xyz.itzsomebody.radon;

import xyz.itzsomebody.radon.config.ObfConfig;
import xyz.itzsomebody.radon.exceptions.MissingClassException;
import xyz.itzsomebody.radon.exceptions.MissingResourceException;
import xyz.itzsomebody.radon.exclusions.ExclusionManager;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.utils.JarLoader;
import xyz.itzsomebody.radon.utils.JarWriter;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Radon {
    private static Radon radon;

    private final ExclusionManager exclusionManager;
    private final ObfConfig config;

    private Map<String, ClassWrapper> classes;
    private Map<String, ClassWrapper> classpath;
    private Map<String, byte[]> resources;

    public Radon(ObfConfig config) {
        this.config = config;
        this.exclusionManager = config.get(ObfConfig.Key.EXCLUSIONS.getKeyString());
        radon = this;
    }

    public <T> T getConfigValue(String key) {
        return config.get(key);
    }

    public void run() {
        // ========================== Load input & libs
        var loader = new JarLoader();
        loader.loadAsInput(getConfigValue(ObfConfig.Key.INPUT.getKeyString())); // Load input
        this.<List<String>>getConfigValue(ObfConfig.Key.LIBRARIES.getKeyString()).forEach(loader::loadAsLib); // Load libs
        this.classes = loader.getClasses();
        this.classpath = loader.getClasspath();
        this.resources = loader.getResources();

        // ========================== Transformation
        var transformers = this.<List<Transformer>>getConfigValue(ObfConfig.Key.TRANSFORMERS.getKeyString());

        // Run 'em all
        transformers.forEach(transformer -> {
            RadonLogger.info("[Transformers] Executing: " + transformer.getName() + " (" + transformer.getConfigName() + ")");
            transformer.init(this);

            // Quality way of measuring execution time /sarcasm
            long before = System.currentTimeMillis();
            transformer.transform();
            long after = System.currentTimeMillis();

            RadonLogger.info("[Transformers] Finished executing: " + transformer.getName() + " [" + (after - before) + "ms]");
        });

        // ========================== Write output
        var writer = new JarWriter();
        writer.write(getConfigValue(ObfConfig.Key.OUTPUT.getKeyString()));
    }

    private void buildHierarchy(ClassWrapper wrapper, ClassWrapper sub, Set<String> visited) {
        if (visited.add(wrapper.getName())) {
            if (wrapper.getSuperName() != null) {
                var superParent = getClasspathWrapper(wrapper.getSuperName());
                wrapper.getParents().add(superParent);
                buildHierarchy(superParent, wrapper, visited);
            }
            if (wrapper.getInterfaceNames() != null) {
                wrapper.getInterfaceNames().forEach(interfaceName -> {
                    var interfaceParent = getClasspathWrapper(interfaceName);
                    wrapper.getParents().add(interfaceParent);
                    buildHierarchy(interfaceParent, wrapper, visited);
                });
            }
        }
        if (sub != null) {
            wrapper.getChildren().add(sub);
        }
    }

    public void buildHierarchyGraph() {
        HashSet<String> visited = new HashSet<>();
        classes.values().forEach(wrapper -> buildHierarchy(wrapper, null, visited));
    }

    public static Radon getInstance() {
        return radon;
    }

    public ClassWrapper getClassWrapper(String name) {
        var wrapper = classes.get(name);

        if (wrapper == null) {
            throw MissingClassException.forInputClass(name);
        }

        return wrapper;
    }

    public Map<String, ClassWrapper> getClasses() {
        return classes;
    }

    public void setClasses(Map<String, ClassWrapper> classes) {
        this.classes = classes;
    }

    public ClassWrapper getClasspathWrapper(String name) {
        var wrapper = classpath.get(name);

        if (wrapper == null) {
            throw MissingClassException.forLibraryClass(name);
        }

        return wrapper;
    }

    public Map<String, ClassWrapper> getClasspath() {
        return classpath;
    }

    public void setClasspath(Map<String, ClassWrapper> classpath) {
        this.classpath = classpath;
    }

    public byte[] getResource(String name) {
        var resource = resources.get(name);

        if (resource == null) {
            throw new MissingResourceException(name);
        }

        return resource;
    }

    public void setResources(Map<String, byte[]> resources) {
        this.resources = resources;
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }

    public ExclusionManager getExclusionManager() {
        return exclusionManager;
    }
}
