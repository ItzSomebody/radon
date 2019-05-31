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

package me.itzsomebody.radon.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import me.itzsomebody.radon.ObfuscationConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Dumps a configuration file from a given {@link ObfuscationConfiguration}.
 *
 * @author ItzSomebody
 */
public class ConfigurationWriter {
    private Map<String, Object> documentMap = new LinkedHashMap<>();
    private ObfuscationConfiguration configuration;

    public ConfigurationWriter(ObfuscationConfiguration configuration) {
        this.configuration = configuration;
    }

    private void putIfNonNull(String key, Object value) {
        if (key != null && value != null)
            documentMap.put(key, value);
    }

    public void setConfigurationValues() {
        putIfNonNull(ConfigurationSetting.INPUT.getName(), configuration.getInput());
        putIfNonNull(ConfigurationSetting.OUTPUT.getName(), configuration.getOutput());
        putIfNonNull(ConfigurationSetting.LIBRARIES.getName(), configuration.getLibraries());

        configuration.getTransformers().stream().filter(Objects::nonNull).forEach(transformer ->
                putIfNonNull(transformer.getExclusionType().getName(), transformer.getConfiguration()));

        putIfNonNull(ConfigurationSetting.EXCLUSIONS.getName(), configuration.getExclusionManager().getExclusions());
        putIfNonNull(ConfigurationSetting.TRASH_CLASSES.getName(), configuration.getnTrashClasses());
        putIfNonNull(ConfigurationSetting.RANDOMIZED_STRING_LENGTH.getName(), configuration.getRandomizedStringLength());
        putIfNonNull(ConfigurationSetting.DICTIONARY.getName(), configuration.getDictionary().getDictionaryName());
        putIfNonNull(ConfigurationSetting.COMPRESSION_LEVEL.getName(), configuration.getCompressionLevel());
        putIfNonNull(ConfigurationSetting.VERIFY.getName(), configuration.isVerify());
    }

    public String dump() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(4);
        return new Yaml(options).dump(documentMap);
    }
}
