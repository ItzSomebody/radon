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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

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
import me.itzsomebody.radon.utils.RandomUtils;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Abstract class for number obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class NumberObfuscation extends Transformer {
    private static final Map<String, NumberObfuscationSetting> KEY_MAP = new HashMap<>();
    private static final Map<NumberObfuscation, NumberObfuscationSetting> NUMBEROBF_SETTING_MAP = new HashMap<>();
    private final List<NumberObfuscation> numberObfuscators = new ArrayList<>();
    private boolean integerTamperingEnabled;
    private boolean longTamperingEnabled;
    private boolean floatTamperingEnabled;
    private boolean doubleTamperingEnabled;

    protected NumberObfuscation master;

    static {
        NumberObfuscationSetting[] values = NumberObfuscationSetting.values();
        Stream.of(values).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
        Stream.of(values).filter(setting -> setting.getNumberObfuscation() != null)
                .forEach(setting -> NUMBEROBF_SETTING_MAP.put(setting.getNumberObfuscation(), setting));
    }

    @Override
    public void transform() {
        numberObfuscators.forEach(numberObfuscation -> {
            numberObfuscation.init(radon);
            numberObfuscation.initMaster(this);
            numberObfuscation.transform();
        });
    }

    @Override
    public String getName() {
        return "Number obfuscation";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.NUMBER_OBFUSCATION;
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        numberObfuscators.forEach(obfuscator -> config.put(obfuscator.getNumberObfuscationSetting().getName(), true));

        config.put(NumberObfuscationSetting.DOUBLE_TAMPERING.getName(), isDoubleTamperingEnabled());
        config.put(NumberObfuscationSetting.FLOAT_TAMPERING.getName(), isFloatTamperingEnabled());
        config.put(NumberObfuscationSetting.INTEGER_TAMPERING.getName(), isIntegerTamperingEnabled());
        config.put(NumberObfuscationSetting.LONG_TAMPERING.getName(), isLongTamperingEnabled());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        Stream.of(NumberObfuscationSetting.values()).filter(setting -> setting.getNumberObfuscation() != null
                && config.containsKey(setting.getName()) && (Boolean) config.get(setting.getName()))
                .forEach(setting -> numberObfuscators.add(setting.getNumberObfuscation()));

        setDoubleTamperingEnabled(getValueOrDefault(NumberObfuscationSetting.DOUBLE_TAMPERING.getName(), config, false));
        setFloatTamperingEnabled(getValueOrDefault(NumberObfuscationSetting.FLOAT_TAMPERING.getName(), config, false));
        setIntegerTamperingEnabled(getValueOrDefault(NumberObfuscationSetting.INTEGER_TAMPERING.getName(), config, false));
        setLongTamperingEnabled(getValueOrDefault(NumberObfuscationSetting.LONG_TAMPERING.getName(), config, false));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            NumberObfuscationSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.NUMBER_OBFUSCATION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.NUMBER_OBFUSCATION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private void initMaster(NumberObfuscation master) {
        this.master = master;
    }

    protected static int randomInt(int bounds) {
        if (bounds <= 0)
            return RandomUtils.getRandomInt(Integer.MAX_VALUE);

        return RandomUtils.getRandomInt(bounds);
    }

    protected static long randomLong(long bounds) {
        if (bounds <= 0)
            return RandomUtils.getRandomLong(Long.MAX_VALUE);

        return RandomUtils.getRandomLong(bounds);
    }

    protected static float randomFloat(float bounds) {
        if (bounds <= 0)
            return RandomUtils.getRandomFloat(Float.MAX_VALUE);

        return RandomUtils.getRandomFloat(bounds);
    }

    protected static double randomDouble(double bounds) {
        if (bounds <= 0)
            return RandomUtils.getRandomDouble(Double.MAX_VALUE);

        return RandomUtils.getRandomDouble(bounds);
    }

    protected boolean isIntegerTamperingEnabled() {
        return integerTamperingEnabled;
    }

    protected void setIntegerTamperingEnabled(boolean integerTamperingEnabled) {
        this.integerTamperingEnabled = integerTamperingEnabled;
    }

    protected boolean isLongTamperingEnabled() {
        return longTamperingEnabled;
    }

    protected void setLongTamperingEnabled(boolean longTamperingEnabled) {
        this.longTamperingEnabled = longTamperingEnabled;
    }

    protected boolean isFloatTamperingEnabled() {
        return floatTamperingEnabled;
    }

    protected void setFloatTamperingEnabled(boolean floatTamperingEnabled) {
        this.floatTamperingEnabled = floatTamperingEnabled;
    }

    protected boolean isDoubleTamperingEnabled() {
        return doubleTamperingEnabled;
    }

    protected void setDoubleTamperingEnabled(boolean doubleTamperingEnabled) {
        this.doubleTamperingEnabled = doubleTamperingEnabled;
    }

    private NumberObfuscationSetting getNumberObfuscationSetting() {
        return NUMBEROBF_SETTING_MAP.get(this);
    }
}
