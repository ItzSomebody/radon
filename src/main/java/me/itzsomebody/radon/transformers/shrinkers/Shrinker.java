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

package me.itzsomebody.radon.transformers.shrinkers;

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

/**
 * Abstract class for shrinking transformers.
 *
 * @author ItzSomebody
 */
public class Shrinker extends Transformer {
    private static final Map<String, ShrinkerSetting> KEY_MAP = new HashMap<>();
    private static final Map<Shrinker, ShrinkerSetting> SHRINKER_SETTING_MAP = new HashMap<>();
    private final List<Shrinker> shrinkers = new ArrayList<>();

    static {
        ShrinkerSetting[] values = ShrinkerSetting.values();
        Stream.of(values).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
        Stream.of(values).forEach(setting -> SHRINKER_SETTING_MAP.put(setting.getShrinker(), setting));
    }

    @Override
    public void transform() {
        shrinkers.forEach(shrinker -> {
            shrinker.init(radon);
            shrinker.transform();
        });
    }

    @Override
    public String getName() {
        return "Shrinker";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.SHRINKER;
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        shrinkers.forEach(shrinker -> config.put(shrinker.getShrinkerSetting().getName(), true));

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        Stream.of(ShrinkerSetting.values()).filter(setting -> config.containsKey(setting.getName())
                && (Boolean) config.get(setting.getName())).forEach(setting -> shrinkers.add(setting.getShrinker()));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            ShrinkerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.SHRINKER.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.SHRINKER.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private ShrinkerSetting getShrinkerSetting() {
        return SHRINKER_SETTING_MAP.get(this);
    }
}
