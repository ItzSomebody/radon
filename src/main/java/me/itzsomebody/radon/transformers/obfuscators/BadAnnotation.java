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

import java.util.ArrayList;
import java.util.Map;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Adds {@code @} annotation to all methods
 * Fernflower refuses to decompile the class.
 * Java will crash on attempt to parse annotations
 *
 * @author xDark
 */
public class BadAnnotation extends Transformer {
    @Override
    public void transform() {
        getClassWrappers().stream().filter(cw -> !excluded(cw)).forEach(cw ->
                cw.getMethods().stream().filter(mw -> !excluded(mw)).forEach(mw -> {
                    MethodNode methodNode = mw.getMethodNode();

                    if (methodNode.visibleAnnotations == null)
                        methodNode.visibleAnnotations = new ArrayList<>();
                    if (methodNode.invisibleAnnotations == null)
                        methodNode.invisibleAnnotations = new ArrayList<>();

                    methodNode.visibleAnnotations.add(new AnnotationNode("@"));
                    methodNode.invisibleAnnotations.add(new AnnotationNode("@"));
                }));
    }

    @Override
    public String getName() {
        return "Bad Annotations";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.BAD_ANNOTATIONS;
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
        throw new InvalidConfigurationValueException(ConfigurationSetting.BAD_ANNOTATION + " expects a boolean");
    }
}
