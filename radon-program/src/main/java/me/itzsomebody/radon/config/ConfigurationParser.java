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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.itzsomebody.radon.Dictionaries;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.exceptions.IllegalConfigurationKeyException;
import me.itzsomebody.radon.exceptions.IllegalConfigurationValueException;
import me.itzsomebody.radon.exclusions.Exclusion;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.miscellaneous.Crasher;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.Expiration;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.ExpirationSetup;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.Watermarker;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.WatermarkerSetup;
import me.itzsomebody.radon.transformers.obfuscators.flow.FlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.InvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.FakeTryCatch;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.HideCode;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.LineNumbers;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.LocalVariables;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.MemberShuffler;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.SourceDebug;
import me.itzsomebody.radon.transformers.obfuscators.miscellaneous.SourceName;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.renamer.Renamer;
import me.itzsomebody.radon.transformers.obfuscators.renamer.RenamerSetup;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryptionSetup;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringPool;
import me.itzsomebody.radon.transformers.optimizers.Optimizer;
import me.itzsomebody.radon.transformers.optimizers.OptimizerDelegator;
import me.itzsomebody.radon.transformers.optimizers.OptimizerSetup;
import me.itzsomebody.radon.transformers.shrinkers.Shrinker;
import me.itzsomebody.radon.transformers.shrinkers.ShrinkerDelegator;
import me.itzsomebody.radon.transformers.shrinkers.ShrinkerSetup;
import org.yaml.snakeyaml.Yaml;

/**
 * A big mess which somehow parses the configuration files.
 *
 * @author ItzSomebody
 */
public class ConfigurationParser {
    private Map<String, Object> map;
    private final static Set<String> VALID_KEYS = new HashSet<String>();

    static {
        for (ConfigurationSettings setting : ConfigurationSettings.values())
            VALID_KEYS.add(setting.getValue());
    }

    public ConfigurationParser(InputStream in) {
        this.map = (Map<String, Object>) new Yaml().load(in);
        this.map.keySet().forEach(s -> {
            if (!VALID_KEYS.contains(s))
                throw new IllegalConfigurationKeyException(s);
        });
    }

    public SessionInfo createSessionFromConfig() {
        SessionInfo info = new SessionInfo();
        info.setInput(getInput());
        info.setOutput(getOutput());
        info.setLibraries(getLibraries());
        info.setTransformers(getTransformers());
        info.setExclusions(getExclusions());
        info.setTrashClasses(getTrashClasses());
        info.setDictionaryType(getDictionary());

        return info;
    }

    private File getInput() {
        Object o = map.get(ConfigurationSettings.INPUT.getValue());
        if (!(o instanceof String))
            throw new IllegalConfigurationValueException(ConfigurationSettings.INPUT.getValue(), String.class,
                    o.getClass());

        return new File((String) o);
    }

    private File getOutput() {
        Object o = map.get(ConfigurationSettings.OUTPUT.getValue());
        if (!(o instanceof String))
            throw new IllegalConfigurationValueException(ConfigurationSettings.OUTPUT.getValue(), String.class,
                    o.getClass());

        return new File((String) o);
    }

    private List<File> getLibraries() {
        Object o = map.get(ConfigurationSettings.LIBRARIES.getValue());
        if (!(o instanceof List))
            throw new IllegalConfigurationValueException(ConfigurationSettings.LIBRARIES.getValue(), List.class,
                    o.getClass());

        ArrayList<File> libraries = new ArrayList<>();
        List<?> libs = (List) o;
        for (Object lib : libs) {
            try {
                String s = (String) lib;
                File libFile = new File(s);
                if (libFile.isDirectory()) {
                    addSubDirFiles(libFile, libraries);
                } else {
                    libraries.add(libFile);
                }
            } catch (ClassCastException e) {
                throw new IllegalConfigurationValueException(ConfigurationSettings.LIBRARIES.getValue(), String.class,
                        lib.getClass());
            }
        }

        return libraries;
    }

    /**
     * Searches sub directories for libraries
     *
     * @param file      should be directory
     * @param libraries
     * @author Richard Xing
     */
    private static void addSubDirFiles(File file, List<File> libraries) {
        if (file.isFile()) {
            System.out.println("should be a directory");
        } else {
            File[] fileLists = file.listFiles();

            for (int i = 0; i < fileLists.length; i++) {
                // 输出元素名称

                if (fileLists[i].isDirectory()) {
                    addSubDirFiles(fileLists[i], libraries);
                } else {
                    if (fileLists[i].getName().toLowerCase().endsWith(".jar")) {
                        //System.out.println(fileLists[i].getName());
                        libraries.add(fileLists[i]);
                    }
                }
            }
        }
    }

    private List<Transformer> getTransformers() {
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(getShrinkerTransformer());
        transformers.add(getOptimizerTransformer());
        transformers.add(getRenamerTransformer());
        transformers.add(getNumberObfuscationTransformer());
        transformers.add(getFakeTryCatchTransformer());
        transformers.add(getInvokeDynamicTransformer());
        List<StringEncryption> stringEncrypters = getStringEncryptionTransformers();
        if (stringEncrypters != null) {
            transformers.addAll(stringEncrypters);
        }
        transformers.add(getFlowObfuscationTransformer());

        transformers.add(getShufflerTransformer());
        transformers.add(getLocalVariablesTransformer());
        transformers.add(getLineNumbersTransformer());
        transformers.add(getSourceNameTransformer());
        transformers.add(getSourceDebugTransformer());
        transformers.add(getCrasherTransformer());
        transformers.add(getHideCodeTransformer());
        transformers.add(getExpirationTransformer());
        transformers.add(getWatermarkerTransformer());



        
        

        return transformers;
    }

    private Shrinker getShrinkerTransformer() {
        Object o = map.get(ConfigurationSettings.SHRINKER.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.SHRINKER.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> shrinkerSettings = (Map) o;
            if (!shrinkerSettings.get("Enabled"))
                return null;

            boolean attributes = shrinkerSettings.getOrDefault("RemoveAttributes", false);
            boolean debug = shrinkerSettings.getOrDefault("RemoveDebug", false);
            boolean invisibleAnnotations = shrinkerSettings.getOrDefault("RemoveInvisibleAnnotations", false);
            boolean visibleAnnotations = shrinkerSettings.getOrDefault("RemoveVisibleAnnotations", false);

            return new ShrinkerDelegator(new ShrinkerSetup(visibleAnnotations, invisibleAnnotations, attributes,
                    debug));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing shrinker setup: " + e.getMessage());
        }
    }
    
    private FakeTryCatch getFakeTryCatchTransformer() {
        Object o = map.get(ConfigurationSettings.FakeTryCatch.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Boolean))
            throw new IllegalConfigurationValueException(ConfigurationSettings.FakeTryCatch.getValue(), Boolean.class,
                    o.getClass());


        return ((boolean) o) ? new FakeTryCatch() : null;
    }

    private Optimizer getOptimizerTransformer() {
        Object o = map.get(ConfigurationSettings.OPTIMIZER.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.OPTIMIZER.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> optimizerSettings = (Map) o;
            if (!optimizerSettings.get("Enabled"))
                return null;

            boolean gotoGoto = optimizerSettings.getOrDefault("InlineGotoGoto", false);
            boolean gotoReturn = optimizerSettings.getOrDefault("InlineGotoReturn", false);
            boolean nopInstructions = optimizerSettings.getOrDefault("RemoveNopInstructions", false);

            return new OptimizerDelegator(new OptimizerSetup(nopInstructions, gotoGoto, gotoReturn));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing optimizer setup: " + e.getMessage());
        }
    }

    private Renamer getRenamerTransformer() {
        Object o = map.get(ConfigurationSettings.RENAMER.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.RENAMER.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Object> renamerSettings = (Map) o;
            if (!(boolean) renamerSettings.get("Enabled"))
                return null;

            List objects = (List) renamerSettings.getOrDefault("AdaptResources", new ArrayList<String>());
            String[] adaptThese = new String[objects.size()];

            for (int i = 0; i < objects.size(); i++) {
                adaptThese[i] = (String) objects.get(i);
            }

            String repackageName = (String) renamerSettings.get("Repackage");

            return new Renamer(new RenamerSetup(adaptThese, repackageName));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing renamer setup: " + e.getMessage());
        }
    }

    private NumberObfuscation getNumberObfuscationTransformer() {
        Object o = map.get(ConfigurationSettings.NUMBER_OBFUSCATION.getValue());
        if (o == null)
            return null;
        if (!(o instanceof String))
            throw new IllegalConfigurationValueException(ConfigurationSettings.NUMBER_OBFUSCATION.getValue(),
                    String.class, o.getClass());

        String s = (String) o;
        if (!"Light".equals(s) && !"Normal".equals(s) && !"Heavy".equals(s))
            throw new IllegalConfigurationValueException("Expected Light, Normal or Heavy as mode for number " +
                    "obfuscation. Got " + s + " instead.");

        return NumberObfuscation.getTransformerFromString(s);
    }

    private InvokeDynamic getInvokeDynamicTransformer() {
        Object o = map.get(ConfigurationSettings.INVOKEDYNAMIC.getValue());
        if (o == null)
            return null;
        if (!(o instanceof String))
            throw new IllegalConfigurationValueException(ConfigurationSettings.INVOKEDYNAMIC.getValue(), String.class,
                    o.getClass());
        String s = (String) o;
        if (!"Light".equals(s) && !"Normal".equals(s) && !"Heavy".equals(s))
            throw new IllegalConfigurationValueException("Expected Light, Normal or Heavy as mode for invokedynamic " +
                    "obfuscation. Got " + s + " instead.");


        return InvokeDynamic.getTransformerFromString(s);
    }

    private List<StringEncryption> getStringEncryptionTransformers() {
        Object o = map.get(ConfigurationSettings.STRING_ENCRYPTION.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.STRING_ENCRYPTION.getValue(), Map.class,
                    o.getClass());

        Map<String, Object> settings = (Map) o;
        if (!(boolean) settings.get("Enabled"))
            return null;

        String s = (String) settings.get("Mode");
        if (!"Light".equals(s) && !"Normal".equals(s) && !"Heavy".equals(s))
            throw new IllegalConfigurationValueException("Expected Light, Normal or Heavy as mode for string " +
                    "encryption. Got " + s + " instead.");

        boolean pool = (boolean) settings.getOrDefault("StringPool", false);
        List<String> exclusions = (List) settings.getOrDefault("Exclusions", new ArrayList<String>());

        ArrayList<StringEncryption> things = new ArrayList<>();
        things.add(StringEncryption.getTransformerFromString(s, new StringEncryptionSetup(exclusions)));
        if (pool)
            things.add(new StringPool(new StringEncryptionSetup(exclusions)));

        return things;
    }

    private FlowObfuscation getFlowObfuscationTransformer() {
        Object o = map.get(ConfigurationSettings.FLOW_OBFUSCATION.getValue());
        if (o == null)
            return null;
        if (!(o instanceof String))
            throw new IllegalConfigurationValueException(ConfigurationSettings.FLOW_OBFUSCATION.getValue(),
                    String.class, o.getClass());

        String s = (String) o;
        if (!"Light".equals(s) && !"Normal".equals(s) && !"Heavy".equals(s))
            throw new IllegalConfigurationValueException("Expected Light, Normal or Heavy as mode for flow " +
                    "obfuscation. Got " + s + " instead.");


        return FlowObfuscation.getTransformerFromString(s);
    }

    private MemberShuffler getShufflerTransformer() {
        Object o = map.get(ConfigurationSettings.SHUFFLER.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Boolean))
            throw new IllegalConfigurationValueException(ConfigurationSettings.SHUFFLER.getValue(), Boolean.class,
                    o.getClass());


        return ((boolean) o) ? new MemberShuffler() : null;
    }

    private LocalVariables getLocalVariablesTransformer() {
        Object o = map.get(ConfigurationSettings.LOCAL_VARIABLES.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.LOCAL_VARIABLES.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> settings = (Map) o;
            if (!settings.get("Enabled")) {
                return null;
            }

            return new LocalVariables(settings.getOrDefault("Remove", false));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing local variable obfuscation setup: "
                    + e.getMessage());
        }
    }

    private LineNumbers getLineNumbersTransformer() {
        Object o = map.get(ConfigurationSettings.LINE_NUMBERS.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.LINE_NUMBERS.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> settings = (Map) o;
            if (!settings.get("Enabled"))
                return null;

            return new LineNumbers(settings.getOrDefault("Remove", false));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing line number obfuscation setup: "
                    + e.getMessage());
        }
    }

    private SourceName getSourceNameTransformer() {
        Object o = map.get(ConfigurationSettings.SOURCE_NAME.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.SOURCE_NAME.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> settings = (Map) o;
            if (!settings.get("Enabled"))
                return null;

            return new SourceName(settings.getOrDefault("Remove", false));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing source name obfuscation setup: "
                    + e.getMessage());
        }
    }

    private SourceDebug getSourceDebugTransformer() {
        Object o = map.get(ConfigurationSettings.SOURCE_DEBUG.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.SOURCE_DEBUG.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Boolean> settings = (Map) o;
            if (!settings.get("Enabled"))
                return null;

            return new SourceDebug(settings.getOrDefault("Remove", false));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing source debug obfuscation setup: "
                    + e.getMessage());
        }
    }

    private Crasher getCrasherTransformer() {
        Object o = map.get(ConfigurationSettings.CRASHER.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Boolean))
            throw new IllegalConfigurationValueException(ConfigurationSettings.CRASHER.getValue(), Boolean.class,
                    o.getClass());

        return ((Boolean) o) ? new Crasher() : null;
    }

    private HideCode getHideCodeTransformer() {
        Object o = map.get(ConfigurationSettings.HIDE_CODE.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Boolean))
            throw new IllegalConfigurationValueException(ConfigurationSettings.HIDE_CODE.getValue(), Boolean.class,
                    o.getClass());

        return ((Boolean) o) ? new HideCode() : null;
    }

    private Expiration getExpirationTransformer() {
        Object o = map.get(ConfigurationSettings.EXPIRATION.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.EXPIRATION.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Object> settings = (Map) o;
            if (!(boolean) settings.get("Enabled"))
                return null;

            boolean injectJOptionPane = (Boolean) settings.get("InjectJOptionPane");
            String expirationMessage = (String) settings.get("Message");
            long expirationDate = new SimpleDateFormat("MM/dd/yyyy").parse((String) settings.get("Expires")).getTime();

            return new Expiration(new ExpirationSetup(expirationMessage, expirationDate, injectJOptionPane));
        } catch (ClassCastException | ParseException e) {
            throw new IllegalConfigurationValueException("Error while parsing expiration setup: " + e.getMessage());
        }
    }

    private Watermarker getWatermarkerTransformer() {
        Object o = map.get(ConfigurationSettings.WATERMARK.getValue());
        if (o == null)
            return null;
        if (!(o instanceof Map))
            throw new IllegalConfigurationValueException(ConfigurationSettings.WATERMARK.getValue(), Map.class,
                    o.getClass());

        try {
            Map<String, Object> settings = (Map) o;
            if (!(Boolean) settings.get("Enabled"))
                return null;

            String message = (String) settings.get("Message");
            String key = (String) settings.get("Key");

            return new Watermarker(new WatermarkerSetup(message, key));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing watermark setup: " + e.getMessage());
        }
    }

    private ExclusionManager getExclusions() {
        ExclusionManager exclusions = new ExclusionManager();
        Object o = map.get(ConfigurationSettings.EXCLUSIONS.getValue());
        if (o == null)
            return exclusions;
        if (!(o instanceof List))
            throw new IllegalConfigurationValueException(ConfigurationSettings.EXCLUSIONS.getValue(), List.class,
                    o.getClass());

        try {
            List<String> list = (List) o;
            list.forEach(s -> exclusions.addExclusion(new Exclusion(s)));
        } catch (ClassCastException e) {
            throw new IllegalConfigurationValueException("Error while parsing exclusion setup: " + e.getMessage());
        }

        return exclusions;
    }

    private int getTrashClasses() {
        Object o = map.get(ConfigurationSettings.TRASH_CLASSES.getValue());
        if (o == null)
            return -1;
        if (!(o instanceof Integer))
            throw new IllegalConfigurationValueException(ConfigurationSettings.TRASH_CLASSES.getValue(), Integer.class,
                    o.getClass());
        return (int) o;
    }

    private Dictionaries getDictionary() {
        Object o = map.get(ConfigurationSettings.DICTIONARY.getValue());
        if (o == null)
            return Dictionaries.ALPHABETICAL;
        if (!(o instanceof String) && !(o instanceof Integer))
            throw new IllegalConfigurationValueException(ConfigurationSettings.DICTIONARY.getValue(), String.class,
                    o.getClass());

        if (o instanceof String)
            return Dictionaries.stringToDictionary((String) o);
        else
            return Dictionaries.intToDictionary((int) o);
    }
}
