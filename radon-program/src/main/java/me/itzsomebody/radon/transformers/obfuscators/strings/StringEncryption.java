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

package me.itzsomebody.radon.transformers.obfuscators.strings;

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
 * Abstract class for string encryption transformers.
 *
 * @author ItzSomebody
 */
public class StringEncryption extends Transformer {
    private static final Map<String, StringEncryptionSetting> KEY_MAP = new HashMap<>();
    private List<String> exemptedStrings;
    private boolean contextCheckingEnabled;
    private boolean stringPoolingEnabled;

    static {
        Stream.of(StringEncryptionSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.STRING_ENCRYPTION;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(StringEncryptionSetting.EXEMPTED_STRINGS.getName(), getExemptedStrings());
        config.put(StringEncryptionSetting.POOL_STRINGS.getName(), isStringPoolingEnabled());
        config.put(StringEncryptionSetting.CHECK_CONTEXT.getName(), isContextCheckingEnabled());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setExemptedStrings(getValueOrDefault(StringEncryptionSetting.EXEMPTED_STRINGS.getName(), config, new ArrayList<>()));
        setStringPoolingEnabled(getValueOrDefault(StringEncryptionSetting.POOL_STRINGS.getName(), config, false));
        setContextCheckingEnabled(getValueOrDefault(StringEncryptionSetting.CHECK_CONTEXT.getName(), config, false));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            StringEncryptionSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.STRING_ENCRYPTION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.STRING_ENCRYPTION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    protected boolean excludedString(String str) {
        return getExemptedStrings().stream().anyMatch(str::contains);
    }

    private List<String> getExemptedStrings() {
        return exemptedStrings;
    }

    private void setExemptedStrings(List<String> exemptedStrings) {
        this.exemptedStrings = exemptedStrings;
    }

    private boolean isContextCheckingEnabled() {
        return contextCheckingEnabled;
    }

    private void setContextCheckingEnabled(boolean contextCheckingEnabled) {
        this.contextCheckingEnabled = contextCheckingEnabled;
    }

    private boolean isStringPoolingEnabled() {
        return stringPoolingEnabled;
    }

    private void setStringPoolingEnabled(boolean stringPoolingEnabled) {
        this.stringPoolingEnabled = stringPoolingEnabled;
    }
}
