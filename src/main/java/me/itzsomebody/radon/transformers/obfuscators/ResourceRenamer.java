/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Renames bundled JAR resources to make their purpose less obvious.
 *
 * @author ItzSomebody
 */
public class ResourceRenamer extends Transformer {
    private Map<String, String> mappings;

    @Override
    public void transform() {
        mappings = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.getMethodNode();

                    Stream.of(methodNode.instructions.toArray()).filter(insn -> insn instanceof LdcInsnNode
                            && ((LdcInsnNode) insn).cst instanceof String).forEach(insn -> {
                        String s = (String) ((LdcInsnNode) insn).cst;
                        String resourceName;

                        if (s.startsWith("/"))
                            resourceName = s.substring(1);
                        else
                            resourceName = classWrapper.getOriginalName().substring(0, classWrapper.getOriginalName().lastIndexOf('/') + 1) + s;

                        if (getResources().containsKey(resourceName))
                            if (mappings.containsKey(resourceName))
                                ((LdcInsnNode) insn).cst = mappings.get(resourceName);
                            else {
                                String newName = '/' + uniqueRandomString();
                                ((LdcInsnNode) insn).cst = newName;
                                mappings.put(resourceName, newName);
                            }
                    });
                }));

        new HashMap<>(getResources()).forEach((name, b) -> {
            if (mappings.containsKey(name)) {
                getResources().remove(name);
                getResources().put(mappings.get(name).substring(1), b);

                counter.incrementAndGet();
            }
        });

        Main.info("Renamed " + counter.get() + " resources");
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.RESOURCE_RENAMER;
    }

    @Override
    public String getName() {
        return "Resource Renamer";
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.RESOURCE_RENAMER + " expects a boolean");
    }
}
