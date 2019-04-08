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

package me.itzsomebody.radon.transformers.obfuscators.references;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Abstract class for reference obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class ReferenceObfuscation extends Transformer {
    private static final Map<String, ReferenceObfuscationSetting> KEY_MAP = new HashMap<>();
    private static final Map<ReferenceObfuscation, ReferenceObfuscationSetting> REFERENCEOBF_SETTING_MAP = new HashMap<>();
    private final List<ReferenceObfuscation> referenceObfuscators = new ArrayList<>();
    private boolean ignoreJava8ClassesForReflectionEnabled;

    static {
        ReferenceObfuscationSetting[] values = ReferenceObfuscationSetting.values();
        Stream.of(values).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
        Stream.of(values).filter(setting -> setting.getReferenceObfuscation() != null)
                .forEach(setting -> REFERENCEOBF_SETTING_MAP.put(setting.getReferenceObfuscation(), setting));
    }

    @Override
    public void transform() {
        referenceObfuscators.forEach(tranformer -> {
            tranformer.init(radon);
            tranformer.transform();
        });
    }

    @Override
    public String getName() {
        return "Reference obfuscation";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.REFERENCE_OBFUSCATION;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        referenceObfuscators.forEach(obfuscator -> config.put(obfuscator.getReferenceObfuscationSetting().getName(), true));

        config.put(ReferenceObfuscationSetting.IGNORE_JAVA8_CLASSES_FOR_REFLECTION.getName(), isIgnoreJava8ClassesForReflectionEnabled());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        Stream.of(ReferenceObfuscationSetting.values()).filter(setting -> setting.getReferenceObfuscation() != null
                && config.containsKey(setting.getName()) && (Boolean) config.get(setting.getName()))
                .forEach(setting -> referenceObfuscators.add(setting.getReferenceObfuscation()));

        setIgnoreJava8ClassesForReflectionEnabled(getValueOrDefault(ReferenceObfuscationSetting.IGNORE_JAVA8_CLASSES_FOR_REFLECTION.getName(), config, false));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            ReferenceObfuscationSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.REFERENCE_OBFUSCATION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.REFERENCE_OBFUSCATION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    protected boolean isIgnoreJava8ClassesForReflectionEnabled() {
        return ignoreJava8ClassesForReflectionEnabled;
    }

    protected void setIgnoreJava8ClassesForReflectionEnabled(boolean ignoreJava8ClassesForReflectionEnabled) {
        this.ignoreJava8ClassesForReflectionEnabled = ignoreJava8ClassesForReflectionEnabled;
    }

    private ReferenceObfuscationSetting getReferenceObfuscationSetting() {
        return REFERENCEOBF_SETTING_MAP.get(this);
    }
}
