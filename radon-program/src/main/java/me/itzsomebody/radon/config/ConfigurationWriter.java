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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import me.itzsomebody.radon.SessionInfo;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.miscellaneous.Crasher;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.Expiration;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.Watermarker;
import me.itzsomebody.radon.transformers.obfuscators.flow.FlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.HeavyFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.LightFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.flow.NormalFlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.HeavyInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.references.InvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.LightInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.invokedynamic.NormalInvokeDynamic;
import me.itzsomebody.radon.transformers.obfuscators.HideCode;
import me.itzsomebody.radon.transformers.shrinkers.LineNumberRemover;
import me.itzsomebody.radon.transformers.shrinkers.LocalVariableRemover;
import me.itzsomebody.radon.transformers.obfuscators.MemberShuffler;
import me.itzsomebody.radon.transformers.shrinkers.SourceDebugRemover;
import me.itzsomebody.radon.transformers.shrinkers.SourceFileRemover;
import me.itzsomebody.radon.transformers.obfuscators.numbers.HeavyNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.LightNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NormalNumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.renamer.Renamer;
import me.itzsomebody.radon.transformers.obfuscators.strings.HeavyStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.LightStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.NormalStringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringPool;
import me.itzsomebody.radon.transformers.optimizers.OptimizerDelegator;
import me.itzsomebody.radon.transformers.shrinkers.ShrinkerDelegator;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Another big mess which somehow dumps a configuration file from a given {@link SessionInfo}.
 *
 * @author ItzSomebody
 */
public class ConfigurationWriter {
    private Map<String, Object> documentMap = new LinkedHashMap<>();

    public ConfigurationWriter(SessionInfo info) {
        if (info.getInput() != null) {
            documentMap.put("Input", info.getInput().getAbsolutePath());
        }
        if (info.getOutput() != null) {
            documentMap.put("Output", info.getOutput().getAbsolutePath());
        }
        if (info.getTransformers() != null) {
            for (Transformer transformer : info.getTransformers()) {
                if (transformer instanceof StringEncryption) {
                    StringEncryption encryption = (StringEncryption) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.STRING_ENCRYPTION.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                            .putIfAbsent("Enabled", true);

                    if (transformer instanceof LightStringEncryption)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                                .put("Mode", "Light");
                    else if (transformer instanceof NormalStringEncryption)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                                .put("Mode", "Normal");
                    else if (transformer instanceof HeavyStringEncryption)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                                .put("Mode", "Heavy");
                    else if (transformer instanceof StringPool)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                                .put("StringPool", true);

                    if (encryption.getExcludedStrings() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.STRING_ENCRYPTION.getValue()))
                                .putIfAbsent("Exclusions", encryption.getExcludedStrings());

                } else if (transformer instanceof InvokeDynamic) {
                    if (transformer instanceof LightInvokeDynamic)
                        documentMap.put(ConfigurationSetting.INVOKEDYNAMIC.getValue(), "Light");
                    else if (transformer instanceof NormalInvokeDynamic)
                        documentMap.put(ConfigurationSetting.INVOKEDYNAMIC.getValue(), "Normal");
                    else if (transformer instanceof HeavyInvokeDynamic)
                        documentMap.put(ConfigurationSetting.INVOKEDYNAMIC.getValue(), "Heavy");

                } else if (transformer instanceof NumberObfuscation) {
                    if (transformer instanceof LightNumberObfuscation)
                        documentMap.put(ConfigurationSetting.NUMBER_OBFUSCATION.getValue(), "Light");
                    else if (transformer instanceof NormalNumberObfuscation)
                        documentMap.put(ConfigurationSetting.NUMBER_OBFUSCATION.getValue(), "Normal");
                    else if (transformer instanceof HeavyNumberObfuscation)
                        documentMap.put(ConfigurationSetting.NUMBER_OBFUSCATION.getValue(), "Heavy");

                } else if (transformer instanceof FlowObfuscation) {
                    if (transformer instanceof LightFlowObfuscation)
                        documentMap.put(ConfigurationSetting.FLOW_OBFUSCATION.getValue(), "Light");
                    else if (transformer instanceof HeavyFlowObfuscation)
                        documentMap.put(ConfigurationSetting.FLOW_OBFUSCATION.getValue(), "Heavy");
                    else if (transformer instanceof NormalFlowObfuscation)
                        documentMap.put(ConfigurationSetting.FLOW_OBFUSCATION.getValue(), "Normal");

                } else if (transformer instanceof LocalVariableRemover) {
                    documentMap.putIfAbsent(ConfigurationSetting.LOCAL_VARIABLES.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.LOCAL_VARIABLES.getValue()))
                            .put("Enabled", true);
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.LOCAL_VARIABLES.getValue()))
                            .put("Remove", ((LocalVariableRemover) transformer).isRemove());
                } else if (transformer instanceof LineNumberRemover) {
                    documentMap.putIfAbsent(ConfigurationSetting.LINE_NUMBERS.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.LINE_NUMBERS.getValue()))
                            .put("Enabled", true);
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.LINE_NUMBERS.getValue()))
                            .put("Remove", ((LineNumberRemover) transformer).isRemove());
                } else if (transformer instanceof SourceFileRemover) {
                    documentMap.putIfAbsent(ConfigurationSetting.SOURCE_NAME.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.SOURCE_NAME.getValue()))
                            .put("Enabled", true);
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.SOURCE_NAME.getValue()))
                            .put("Remove", ((SourceFileRemover) transformer).isRemove());
                } else if (transformer instanceof SourceDebugRemover) {
                    documentMap.putIfAbsent(ConfigurationSetting.SOURCE_DEBUG.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.SOURCE_DEBUG.getValue()))
                            .put("Enabled", true);
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.SOURCE_DEBUG.getValue()))
                            .put("Remove", ((SourceDebugRemover) transformer).isRemove());
                } else if (transformer instanceof HideCode) {
                    documentMap.put(ConfigurationSetting.HIDE_CODE.getValue(), true);
                } else if (transformer instanceof MemberShuffler) {
                    documentMap.put(ConfigurationSetting.SHUFFLER.getValue(), true);
                } else if (transformer instanceof Crasher) {
                    documentMap.put(ConfigurationSetting.CRASHER.getValue(), true);
                } else if (transformer instanceof Renamer) {
                    Renamer renamer = (Renamer) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.RENAMER.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.RENAMER.getValue()))
                            .put("Enabled", true);
                    if (renamer.getSetup().getRepackageName() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.RENAMER.getValue()))
                                .put("Repackage", renamer.getSetup().getRepackageName());
                    if (renamer.getSetup().getAdaptTheseResources() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.RENAMER.getValue()))
                                .put("AdaptResources", Arrays.asList(renamer.getSetup().getAdaptTheseResources()));

                } else if (transformer instanceof OptimizerDelegator) {
                    OptimizerDelegator optimizer = (OptimizerDelegator) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.OPTIMIZER.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.OPTIMIZER.getValue())).put("Enabled", true);
                    if (optimizer.getSetup().isGotoGotoEnabled())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.OPTIMIZER.getValue()))
                                .put("RemoveGotoGoto", true);
                    if (optimizer.getSetup().isGotoReturnEnabled())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.OPTIMIZER.getValue()))
                                .put("RemoveGotoReturn", true);
                    if (optimizer.getSetup().isNopRemoverEnabled())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.OPTIMIZER.getValue()))
                                .put("RemoveNopInstructions", true);
                } else if (transformer instanceof ShrinkerDelegator) {
                    ShrinkerDelegator shrinker = (ShrinkerDelegator) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.SHRINKER.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.SHRINKER.getValue())).put("Enabled", true);
                    if (shrinker.getSetup().isRemoveAttributes())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.SHRINKER.getValue()))
                                .put("RemoveAttributes", true);
                    if (shrinker.getSetup().isRemoveDebug())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.SHRINKER.getValue()))
                                .put("RemoveDebug", true);
                    if (shrinker.getSetup().isRemoveInvisibleAnnotations())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.SHRINKER.getValue()))
                                .put("RemoveInvisibleAnnotations", true);
                    if (shrinker.getSetup().isRemoveVisibleAnnotations())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.SHRINKER.getValue()))
                                .put("RemoveVisibleAnnotations", true);
                } else if (transformer instanceof Watermarker) {
                    Watermarker watermarker = (Watermarker) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.WATERMARK.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.WATERMARK.getValue())).put("Enabled", true);
                    if (watermarker.getSetup().getMessage() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.WATERMARK.getValue()))
                                .put("Message", watermarker.getSetup().getMessage());
                    if (watermarker.getSetup().getKey() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.WATERMARK.getValue()))
                                .put("Key", watermarker.getSetup().getKey());

                } else if (transformer instanceof Expiration) {
                    Expiration expiration = (Expiration) transformer;
                    documentMap.putIfAbsent(ConfigurationSetting.EXPIRATION.getValue(),
                            new LinkedHashMap<String, Object>());
                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.EXPIRATION.getValue())).put("Enabled", true);
                    if (expiration.getSetup().getMessage() != null)
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.EXPIRATION.getValue()))
                                .put("Message", expiration.getSetup().getMessage());

                    ((LinkedHashMap) documentMap.get(ConfigurationSetting.EXPIRATION.getValue())).put("Expires",
                            new SimpleDateFormat("MM/dd/yyyy").format(new Date(expiration.getSetup().getExpires())));
                    if (expiration.getSetup().isInjectJOptionPane())
                        ((LinkedHashMap) documentMap.get(ConfigurationSetting.EXPIRATION.getValue()))
                                .put("InjectJOptionPane", expiration.getSetup().isInjectJOptionPane());
                }
            }
        }
        if (info.getDictionaryType() != null)
            documentMap.put("Dictionary", info.getDictionaryType().getValue());
        documentMap.put("TrashClasses", info.getTrashClasses());
        if (info.getLibraries() != null) {
            ArrayList<String> libs = new ArrayList<>();
            info.getLibraries().forEach(file -> libs.add(file.getAbsolutePath()));
            documentMap.put("Libraries", libs);
        }
        if (info.getExclusionManager() != null) {
            ArrayList<String> exclusions = new ArrayList<>();
            info.getExclusionManager().getExclusions().forEach(exclusion -> {
                exclusions.add(exclusion.getExclusionType().getValue() + ": " + exclusion.getPattern().toString());
            });
            documentMap.put("Exclusions", exclusions);
        }
    }

    public String dump() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(4);
        return new Yaml(options).dump(documentMap);
    }
}
