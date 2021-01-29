/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import org.objectweb.asm.tree.ClassNode;
import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.dictionaries.DictionaryFactory;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.asm.ResourceNameRemapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceRenamer extends Transformer {
    private final Map<String, String> mappings = new HashMap<>();
    private Dictionary dictionary;

    @Override
    public void transform() {
        RadonLogger.info("Generating mappings");
        long current = System.currentTimeMillis();
        generateMappings();
        RadonLogger.info(String.format("Finished generating mappings [%dms]", (System.currentTimeMillis() - current)));

        RadonLogger.info("Applying mappings");
        current = System.currentTimeMillis();
        applyMappings();
        RadonLogger.info(String.format("Finished applying mappings [%dms]", (System.currentTimeMillis() - current)));
    }

    private void generateMappings() {
        resourceMap().keySet().stream().filter(this::notExcluded).forEach(resourceName -> {
            String newName;
            do {
                newName = dictionary.next();
            } while (resourceMap().containsKey(newName));

            mappings.put(resourceName, newName);
        });
    }

    private void applyMappings() {
        new ArrayList<>(classes()).forEach(classWrapper -> {
            var classNode = classWrapper.getClassNode();
            var copy = new ClassNode();
            classNode.accept(new ResourceNameRemapper(copy, mappings, classWrapper.getName()));
            classWrapper.setClassNode(copy);
        });
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.RESOURCE_RENAMER;
    }

    @Override
    public void loadSetup(Configuration config) {
        String dictionaryName = config.get(getLocalConfigPath() + ".dictionary");
        dictionary = dictionaryName == null ? DictionaryFactory.defaultDictionary() : DictionaryFactory.forName(dictionaryName);
    }

    @Override
    public String getConfigName() {
        return Transformers.RESOURCE_RENAMER.getConfigName();
    }
}
