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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import me.itzsomebody.radon.dictionaries.Dictionary;
import me.itzsomebody.radon.dictionaries.DictionaryFactory;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.Exclusion;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.FileUtils;

import static me.itzsomebody.radon.config.ConfigurationSetting.COMPRESSION_LEVEL;
import static me.itzsomebody.radon.config.ConfigurationSetting.CORRUPT_CRC;
import static me.itzsomebody.radon.config.ConfigurationSetting.FAKE_DIRECTORY;
import static me.itzsomebody.radon.config.ConfigurationSetting.DICTIONARY;
import static me.itzsomebody.radon.config.ConfigurationSetting.EXCLUSIONS;
import static me.itzsomebody.radon.config.ConfigurationSetting.INPUT;
import static me.itzsomebody.radon.config.ConfigurationSetting.LIBRARIES;
import static me.itzsomebody.radon.config.ConfigurationSetting.OUTPUT;
import static me.itzsomebody.radon.config.ConfigurationSetting.RANDOMIZED_STRING_LENGTH;
import static me.itzsomebody.radon.config.ConfigurationSetting.TRASH_CLASSES;
import static me.itzsomebody.radon.config.ConfigurationSetting.VERIFY;

public final class ObfuscationConfiguration {
    public static ObfuscationConfiguration from(Configuration config) {
        ObfuscationConfiguration obfConfig = new ObfuscationConfiguration();

        // INPUT / OUTPUT

        if (!config.contains(INPUT)) {
            throw new RadonException("No input file was specified in the config");
        }
        if (!config.contains(OUTPUT)) {
            throw new RadonException("No output file was specified in the config");
        }

        obfConfig.setInput(new File((String) config.get(INPUT)));
        obfConfig.setOutput(new File((String) config.get(OUTPUT)));

        // LIBRARIES

        List<File> libraries = new ArrayList<>();
        List<String> libPaths = config.getOrDefault(LIBRARIES, Collections.emptyList());
        libPaths.forEach(s -> {
            File f = new File(s);

            if (f.isDirectory()) {
                FileUtils.getSubDirectoryFiles(f, libraries);
            } else {
                libraries.add(f);
            }
        });
        obfConfig.setLibraries(libraries);

        // EXCLUSIONS

        ExclusionManager manager = new ExclusionManager();
        List<String> exclusionPatterns = config.getOrDefault(EXCLUSIONS, Collections.emptyList());
        exclusionPatterns.forEach(s -> manager.addExclusion(new Exclusion(s)));
        obfConfig.setExclusionManager(manager);

        // DICTIONARY

        try
        {
            String dictionaryName = config.getOrDefault(DICTIONARY, "alphanumeric");
            obfConfig.setDictionary(DictionaryFactory.get(dictionaryName));
        }
        catch(ClassCastException e)
        {
            // String array charset
            List<String> dictionaryCharset = config.getOrDefault(DICTIONARY, Collections.emptyList());
            obfConfig.setDictionary(DictionaryFactory.getCustom(dictionaryCharset));
        }

        // MISC.

        obfConfig.setRandomizedStringLength(config.getOrDefault(RANDOMIZED_STRING_LENGTH, 1));
        obfConfig.setCompressionLevel(config.getOrDefault(COMPRESSION_LEVEL, Deflater.BEST_COMPRESSION));
        obfConfig.setVerify(config.getOrDefault(VERIFY, false));
        obfConfig.setCorruptCrc(config.getOrDefault(CORRUPT_CRC, false));
        obfConfig.setFakeDirectories(config.getOrDefault(FAKE_DIRECTORY, false));
        obfConfig.setnTrashClasses(config.getOrDefault(TRASH_CLASSES, 0));

        // TRANSFORMERS

        List<Transformer> transformers = new ArrayList<>();
        Stream.of(ConfigurationSetting.values()).filter(setting -> setting.getTransformer() != null).forEach(setting -> {
            if (config.contains(setting)) {
                Transformer transformer = setting.getTransformer();

                if (config.get(setting) instanceof Map) {
                    transformer.setConfiguration(config);
                    transformers.add(transformer);
                } else if (config.get(setting) instanceof Boolean && (boolean) config.get(setting)) {
                    transformers.add(transformer);
                }
            }
        });

        obfConfig.setTransformers(transformers);

        return obfConfig;
    }

    private File input;
    private File output;
    private List<File> libraries;
    private ExclusionManager exclusionManager;

    private Dictionary dictionary;
    private int randomizedStringLength;
    private int compressionLevel;
    private boolean verify;
    private boolean corruptCrc;
    private boolean fakeDirectories;
    private int nTrashClasses;

    private List<Transformer> transformers;

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<File> libraries) {
        this.libraries = libraries;
    }

    public ExclusionManager getExclusionManager() {
        return exclusionManager;
    }

    public void setExclusionManager(ExclusionManager exclusionManager) {
        this.exclusionManager = exclusionManager;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getRandomizedStringLength() {
        return randomizedStringLength;
    }

    public void setRandomizedStringLength(int randomizedStringLength) {
        this.randomizedStringLength = randomizedStringLength;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public boolean isCorruptCrc() {
        return corruptCrc;
    }

    public void setCorruptCrc(boolean corruptCrc) {
        this.corruptCrc = corruptCrc;
    }

    public boolean isFakeDirectories() {
        return fakeDirectories;
    }

    public void setFakeDirectories(boolean fakeDirectories) {
        this.fakeDirectories = fakeDirectories;
    }

    public int getnTrashClasses() {
        return nTrashClasses;
    }

    public void setnTrashClasses(int nTrashClasses) {
        this.nTrashClasses = nTrashClasses;
    }

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }
}
