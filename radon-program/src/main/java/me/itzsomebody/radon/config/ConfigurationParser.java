/*
 * Copyright (C) 2018 ItzSomebody
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import me.itzsomebody.radon.DictionaryType;
import me.itzsomebody.radon.ObfuscationConfiguration;
import me.itzsomebody.radon.exceptions.IllegalConfigurationValueException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.Exclusion;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.miscellaneous.Crasher;
import me.itzsomebody.radon.transformers.miscellaneous.Expiration;
import me.itzsomebody.radon.transformers.miscellaneous.Watermarker;
import me.itzsomebody.radon.transformers.obfuscators.AntiTamper;
import me.itzsomebody.radon.transformers.obfuscators.HideCode;
import me.itzsomebody.radon.transformers.obfuscators.MemberShuffler;
import me.itzsomebody.radon.transformers.obfuscators.flow.FlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.references.ReferenceObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.renamer.Renamer;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryption;
import me.itzsomebody.radon.transformers.optimizers.Optimizer;
import me.itzsomebody.radon.transformers.shrinkers.Shrinker;
import me.itzsomebody.radon.utils.FileUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * Parses a YAML file into an {@link ObfuscationConfiguration}.
 *
 * @author ItzSomebody
 */
public class ConfigurationParser {
    private static final Set<String> VALID_KEYS = new HashSet<>();
    private Map<String, Object> config;

    static {
        Stream.of(ConfigurationSetting.values()).forEach(setting -> VALID_KEYS.add(setting.getName()));
    }

    /**
     * Returns the specified value from the provided map.
     *
     * @param key the key to lookup the value.
     * @param map the map to lookup.
     * @param <T> generic-typing because ItzSomebody is lazy.
     * @return the specified value from the provided map.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(String key, Map<String, Object> map) {
        return (T) map.get(key);
    }

    // Laziness v2.0
    private static <T> T getValueOrDefault(String key, Map<String, Object> map, T defaultVal) {
        T t = getValue(key, map);

        if (t == null)
            return defaultVal;
        else
            return t;
    }

    /**
     * Loads the provided {@link InputStream} as a YAML file and verifies it as a configuration.
     *
     * @param in the {@link InputStream} of the YAML file.
     */
    public ConfigurationParser(InputStream in) {
        // Loads the YAML file into a Map.
        config = new Yaml().load(in);

        // Verifies the top-level of the configuration.
        // TODO: Verify all levels of the configuration.
        config.forEach((k, v) -> {
            if (!VALID_KEYS.contains(k))
                throw new RadonException(k + " is not a valid configuration setting.");

            ConfigurationSetting setting = ConfigurationSetting.valueOf(k.toUpperCase());
            if (!setting.getExpectedType().isAssignableFrom(v.getClass()))
                throw new IllegalConfigurationValueException(k, setting.getExpectedType(), v.getClass());
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
                if (libFile.isDirectory()) {
                    FileUtils.getSubDirectoryFiles(libFile, libraries);
                } else {
                    libraries.add(libFile);
                }
            });

        return libraries;
    }

    private ExclusionManager getExclusionManager() {
        ExclusionManager manager = new ExclusionManager();

        List<String> regexPatterns = getValueOrDefault(ConfigurationSetting.EXCLUSIONS.getName(), config, new ArrayList<>());
        regexPatterns.forEach(regexPattern -> manager.addExclusion(new Exclusion(regexPattern)));

        return manager;
    }

    private StringEncryption createStringEncryptionTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.STRING_ENCRYPTION.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        StringEncryption transformer = new StringEncryption();

        transformer.setExemptedStrings(getValueOrDefault("exempted", setup, new ArrayList<>()));
        transformer.setStringPoolingEnabled(getValueOrDefault("pool_strings", setup, false));
        transformer.setContextCheckingEnabled(getValueOrDefault("check_context", setup, false));

        return transformer;
    }

    private FlowObfuscation createFlowObfuscationTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.FLOW_OBFUSCATION.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        FlowObfuscation transformer = new FlowObfuscation();

        transformer.setCombineTryWithCatchEnabled(getValueOrDefault("combine_try_with_catch", setup, false));
        transformer.setFakeCatchBlocksEnabled(getValueOrDefault("fake_catch_blocks", setup, false));
        transformer.setInsertBogusJumpsEnabled(getValueOrDefault("insert_bogus_jumps", setup, false));
        transformer.setMutilateNullCheckEnabled(getValueOrDefault("mutilate_null_check", setup, false));
        transformer.setRearrangeFlowEnabled(getValueOrDefault("rearrange_flow", setup, false));
        transformer.setReplaceGotoEnabled(getValueOrDefault("replace_goto", setup, false));

        return transformer;
    }

    private ReferenceObfuscation createReferenceObfuscationTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.REFERENCE_OBFUSCATION.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        ReferenceObfuscation transformer = new ReferenceObfuscation();

        transformer.setHideFieldsWithIndyEnabled(getValueOrDefault("hide_fields_with_indy", setup, false));
        transformer.setHideMethodsWithIndyEnabled(getValueOrDefault("hide_methods_with_indy", setup, false));
        transformer.setHideFieldsWithReflectionEnabled(getValueOrDefault("hide_fields_with_reflection", setup, false));
        transformer.setHideMethodsWithReflectionEnabled(getValueOrDefault("hide_methods_with_reflection", setup, false));
        transformer.setIgnoreJava8ClassesForReflectionEnabled(getValueOrDefault("ignore_java8_classes_for_reflection",
                setup, false));

        return transformer;
    }

    private NumberObfuscation createNumberObfuscationTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.NUMBER_OBFUSCATION.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        NumberObfuscation transformer = new NumberObfuscation();

        transformer.setArithmeticOperationsEnabled(getValueOrDefault("arithmetic_operations", setup, false));
        transformer.setBitwiseOperationsEnabled(getValueOrDefault("bitwise_operations", setup, false));
        transformer.setContextCheckingEnabled(getValueOrDefault("context_checking", setup, false));
        transformer.setDoubleTamperingEnabled(getValueOrDefault("double_tampering", setup, false));
        transformer.setFloatTamperingEnabled(getValueOrDefault("float_tampering", setup, false));
        transformer.setIntegerTamperingEnabled(getValueOrDefault("integer_tampering", setup, false));
        transformer.setLongTamperingEnabled(getValueOrDefault("long_tampering", setup, false));

        return transformer;
    }

    private AntiTamper createAntiTamperTransformer() {
        String mode = getValue(ConfigurationSetting.ANTI_TAMPER.getName(), config);

        if (mode == null)
            return null;

        AntiTamper transformer = new AntiTamper();

        transformer.setType(mode);

        return transformer;
    }

    // TODO: Virtual Machine
    // TODO: Resource encryption
    // TODO: Resource renamer

    private HideCode createHideCodeTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.HIDE_CODE.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        HideCode transformer = new HideCode();

        transformer.setHideClassesEnabled(getValueOrDefault("hide_classes", setup, false));
        transformer.setHideFieldsEnabled(getValueOrDefault("hide_fields", setup, false));
        transformer.setHideMethodsEnabled(getValueOrDefault("hide_methods", setup, false));

        return transformer;
    }

    private Crasher createCrasherTransformer() {
        boolean enabled = getValueOrDefault(ConfigurationSetting.CRASHER.getName(), config, false);

        if (!enabled)
            return null;

        return new Crasher();
    }

    private Expiration createExpirationTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.EXPIRATION.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        Expiration transformer = new Expiration();

        transformer.setExpires(getValueOrDefault("expire_time", setup, 0L));
        transformer.setInjectJOptionPaneEnabled(getValueOrDefault("inject_joptionpane", setup, false));
        transformer.setMessage(getValueOrDefault("expiration_message", setup, "Your trial has expired!"));

        return transformer;
    }

    private Watermarker createWatermarkerTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.WATERMARK.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        Watermarker transformer = new Watermarker();

        transformer.setKey(getValue("key", setup));
        transformer.setMessage(getValue("message", setup));

        return transformer;
    }

    private Optimizer createOptimizerTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.OPTIMIZER.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        Optimizer transformer = new Optimizer();

        transformer.setInlineGotoGotosEnabled(getValueOrDefault("inline_goto_goto", setup, false));
        transformer.setInlineGotoReturnEnabled(getValueOrDefault("inline_goto_return", setup, false));
        transformer.setRemoveNopsEnabled(getValueOrDefault("remove_nops", setup, false));

        return transformer;
    }

    private Shrinker createShrinkerTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.SHRINKER.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        Shrinker transformer = new Shrinker();

        transformer.setRemoveDeprecatedEnabled(getValueOrDefault("remove_deprecated", setup, false));
        transformer.setRemoveInnerClassesEnabled(getValueOrDefault("remove_inner_classes", setup, false));
        transformer.setRemoveInvisibleAnnotationsEnabled(getValueOrDefault("remove_invisible_annotations", setup, false));
        transformer.setRemoveInvisibleParametersAnnotationsEnabled(getValueOrDefault("remove_invisible_parameter_annotations", setup, false));
        transformer.setRemoveInvisibleTypeAnnotationsEnabled(getValueOrDefault("remove_invisible_type_annotations", setup, false));
        transformer.setRemoveLineNumbersEnabled(getValueOrDefault("remove_line_numbers", setup, false));
        transformer.setRemoveLocalVarsEnabled(getValueOrDefault("remove_local_variables", setup, false));
        transformer.setRemoveOuterMethodEnabled(getValueOrDefault("remove_outer_method", setup, false));
        transformer.setRemoveSignatureEnabled(getValueOrDefault("remove_signature", setup, false));
        transformer.setRemoveSourceDebugEnabled(getValueOrDefault("remove_source_debug", setup, false));
        transformer.setRemoveSourceFileEnabled(getValueOrDefault("remove_source_file", setup, false));
        transformer.setRemoveSyntheticEnabled(getValueOrDefault("remove_synthetic", setup, false));
        transformer.setRemoveUnknownAttributesEnabled(getValueOrDefault("remove_unknown_attributes", setup, false));
        transformer.setRemoveVisibleAnnotationsEnabled(getValueOrDefault("remove_visible_annotations", setup, false));
        transformer.setRemoveVisibleParametersAnnotationsEnabled(getValueOrDefault("remove_visible_parameter_annotations", setup, false));
        transformer.setRemoveVisibleTypeAnnotationsEnabled(getValueOrDefault("remove_visible_type_annotations", setup, false));

        return transformer;
    }

    private MemberShuffler createShufflerTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.MEMBER_SHUFFLER.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        MemberShuffler transformer = new MemberShuffler();

        transformer.setShuffleFieldsEnabled(getValueOrDefault("shuffle_fields", setup, false));
        transformer.setShuffleMethodsEnabled(getValueOrDefault("shuffle_methods", setup, false));

        return transformer;
    }

    private Renamer createRenamerTransformer() {
        Map<String, Object> setup = getValue(ConfigurationSetting.RENAMER.getName(), config);
        if (setup == null)
            return null;

        boolean enabled = getValue("enabled", setup);
        if (!enabled)
            return null;

        Renamer transformer = new Renamer();

        transformer.setAdaptTheseResources(getValueOrDefault("adapt_resources", setup, new String[0]));
        transformer.setOverloadEnabled(getValueOrDefault("overload", setup, false));
        transformer.setRepackageName(getValueOrDefault("repackage_name", setup, null));

        return transformer;
    }

    private List<Transformer> getTransformers() {
        ArrayList<Transformer> transformers = new ArrayList<>();

        transformers.add(createStringEncryptionTransformer());
        transformers.add(createFlowObfuscationTransformer());
        transformers.add(createReferenceObfuscationTransformer());
        transformers.add(createNumberObfuscationTransformer());
        transformers.add(createAntiTamperTransformer());
        transformers.add(createHideCodeTransformer());
        transformers.add(createCrasherTransformer());
        transformers.add(createExpirationTransformer());
        transformers.add(createWatermarkerTransformer());
        transformers.add(createOptimizerTransformer());
        transformers.add(createShrinkerTransformer());
        transformers.add(createShufflerTransformer());
        transformers.add(createRenamerTransformer());

        return transformers;
    }

    private DictionaryType getDictionaryType() {
        String type = getValueOrDefault(ConfigurationSetting.DICTIONARY.getName(), config, "spaces");

        return DictionaryType.valueOf(type.toUpperCase());
    }

    private int getRandomizedStringLength() {
        return getValueOrDefault(ConfigurationSetting.RANDOMIZED_STRING_LENGTH.getName(), config, 8);
    }

    private int getCompressionLevel() {
        return getValueOrDefault(ConfigurationSetting.COMPRESSION_LEVEL.getName(), config, 9);
    }

    private boolean isVerify() {
        return getValueOrDefault(ConfigurationSetting.VERIFY.getName(), config, false);
    }

    private int getnTrashClasses() {
        return getValueOrDefault(ConfigurationSetting.TRASH_CLASSES.getName(), config, 0);
    }

    public ObfuscationConfiguration createObfuscatorConfiguration() {
        ObfuscationConfiguration configuration = new ObfuscationConfiguration();

        configuration.setInput(getInput());
        configuration.setOutput(getOutput());
        configuration.setLibraries(getLibraries());
        configuration.setExclusions(getExclusionManager());
        configuration.setTransformers(getTransformers());

        return configuration;
    }
}
