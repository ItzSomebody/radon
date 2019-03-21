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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import me.itzsomebody.radon.DictionaryType;
import me.itzsomebody.radon.ObfuscationConfiguration;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.Exclusion;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.FileUtils;
import org.yaml.snakeyaml.Yaml;

import static me.itzsomebody.radon.utils.ConfigUtils.getValue;
import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Parses a YAML file into an {@link ObfuscationConfiguration}.
 *
 * @author ItzSomebody
 */
public class ConfigurationParser {
    private final Map<String, ConfigurationSetting> keyLookup = new HashMap<>();
    private Map<String, Object> config;

    /**
     * Loads the provided {@link InputStream} as a YAML file and verifies it as a configuration.
     *
     * @param in the {@link InputStream} of the YAML file.
     */
    @SuppressWarnings("unchecked")
    public ConfigurationParser(InputStream in) {
        Stream.of(ConfigurationSetting.values()).forEach(setting -> keyLookup.put(setting.getName(), setting));

        // Loads the YAML file into a Map.
        config = new Yaml().load(in);

        // Verifies the top-level of the configuration.
        config.forEach((k, v) -> {
            if (!keyLookup.containsKey(k))
                throw new RadonException(k + " is not a valid configuration setting.");

            ConfigurationSetting setting = ConfigurationSetting.valueOf(k.toUpperCase());
            if (!setting.getExpectedType().isAssignableFrom(v.getClass()))
                throw new InvalidConfigurationValueException(k, setting.getExpectedType(), v.getClass());
        });
    }

    /**
     * Return the input file.
     *
     * @return the input file.
     */
    private File getInput() {
        return getValue(ConfigurationSetting.INPUT.getName(), config);
    }

    /**
     * Returns the output file.
     *
     * @return the output file.
     */
    private File getOutput() {
        return getValue(ConfigurationSetting.OUTPUT.getName(), config);
    }

    /**
     * Returns the library files.
     *
     * @return the library files.
     */
    private List<File> getLibraries() {
        ArrayList<File> libraries = new ArrayList<>();
        List<?> libs = getValue(ConfigurationSetting.LIBRARIES.getName(), config);

        if (libs != null)
            libs.forEach(lib -> {
                String s = (String) lib;
                File libFile = new File(s);
                if (libFile.isDirectory())
                    FileUtils.getSubDirectoryFiles(libFile, libraries);
                else
                    libraries.add(libFile);

            });

        return libraries;
    }

    /**
     * Creates and returns a new {@link ExclusionManager}.
     *
     * @return a new {@link ExclusionManager}.
     */
    private ExclusionManager getExclusionManager() {
        ExclusionManager manager = new ExclusionManager();

        List<String> regexPatterns = getValueOrDefault(ConfigurationSetting.EXCLUSIONS.getName(), config, new ArrayList<>());
        regexPatterns.forEach(regexPattern -> manager.addExclusion(new Exclusion(regexPattern)));

        return manager;
    }

    /**
     * Returns a {@link ArrayList} of {@link Transformer}s.
     *
     * @return a {@link ArrayList} of {@link Transformer}s.
     */
    @SuppressWarnings("unchecked")
    private List<Transformer> getTransformers() {
        ArrayList<Transformer> transformers = new ArrayList<>();

        config.forEach((k, v) -> {
            ConfigurationSetting setting = keyLookup.get(k);

            if (setting != null) {
                if (!setting.getExpectedType().isInstance(v))
                    throw new InvalidConfigurationValueException(setting.getName(), setting.getExpectedType(), v.getClass());

                Transformer transformer = setting.getTransformer();

                if (transformer != null) {
                    if (v instanceof Boolean)
                        transformers.add(transformer);
                    else if (v instanceof Map) {
                        transformer.verifyConfiguration((Map<String, Object>) v);
                        transformer.setConfiguration((Map<String, Object>) v);
                        transformers.add(transformer);
                    }
                }
            }
        });

        return transformers;
    }

    /**
     * Returns the number of trash classes Radon should generate.
     *
     * @return the number of trash classes Radon should generate.
     */
    private int getnTrashClasses() {
        return getValueOrDefault(ConfigurationSetting.TRASH_CLASSES.getName(), config, 0);
    }

    /**
     * Returns the number of characters Radon should generate in random strings.
     *
     * @return the number of characters Radon should generate in random strings.
     */
    private int getRandomizedStringLength() {
        return getValueOrDefault(ConfigurationSetting.RANDOMIZED_STRING_LENGTH.getName(), config, 8);
    }

    /**
     * Returns the {@link DictionaryType}.
     *
     * @return the {@link DictionaryType}.
     */
    private DictionaryType getDictionaryType() {
        String type = getValueOrDefault(ConfigurationSetting.DICTIONARY.getName(), config, "spaces");

        return DictionaryType.valueOf(type.toUpperCase());
    }

    /**
     * Returns the level of compression Radon should apply to the output.
     *
     * @return the level of compression Radon should apply to the output.
     */
    private int getCompressionLevel() {
        return getValueOrDefault(ConfigurationSetting.COMPRESSION_LEVEL.getName(), config, 9);
    }

    /**
     * Returns true if Radon should verify the output.
     *
     * @return true if Radon should verify the output.
     */
    private boolean isVerify() {
        return getValueOrDefault(ConfigurationSetting.VERIFY.getName(), config, false);
    }

    /**
     * Creates a new {@link ObfuscationConfiguration} from the provided configuration.
     *
     * @return a new {@link ObfuscationConfiguration} from the provided configuration.
     */
    public ObfuscationConfiguration createObfuscatorConfiguration() {
        ObfuscationConfiguration configuration = new ObfuscationConfiguration();

        configuration.setInput(getInput());
        configuration.setOutput(getOutput());
        configuration.setLibraries(getLibraries());
        configuration.setTransformers(getTransformers());
        configuration.setExclusionManager(getExclusionManager());
        configuration.setnTrashClasses(getnTrashClasses());
        configuration.setRandomizedStringLength(getRandomizedStringLength());
        configuration.setDictionaryType(getDictionaryType());
        configuration.setCompressionLevel(getCompressionLevel());
        configuration.setVerify(isVerify());

        return configuration;
    }
}
