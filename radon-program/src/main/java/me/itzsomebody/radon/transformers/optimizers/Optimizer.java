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

package me.itzsomebody.radon.transformers.optimizers;

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
 * Optimizer.
 *
 * @author ItzSomebody
 */
public class Optimizer extends Transformer {
    private static final Map<String, OptimizerSetting> KEY_MAP = new HashMap<>();
    private static final Map<Optimizer, OptimizerSetting> OPTIMIZER_SETTING_MAP = new HashMap<>();
    private final List<Optimizer> optimizers = new ArrayList<>();

    static {
        OptimizerSetting[] values = OptimizerSetting.values();
        Stream.of(values).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
        Stream.of(values).forEach(setting -> OPTIMIZER_SETTING_MAP.put(setting.getOptimizer(), setting));
    }
    // TODO Add some more inliners cuz why not

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Optimizer";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.OPTIMIZER;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        optimizers.forEach(optimizer -> config.put(optimizer.getOptimizerSetting().getName(), true));

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        Stream.of(OptimizerSetting.values()).filter(setting -> config.containsKey(setting.getName()))
                .forEach(setting -> optimizers.add(setting.getOptimizer()));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            OptimizerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.OPTIMIZER.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.OPTIMIZER.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private OptimizerSetting getOptimizerSetting() {
        return OPTIMIZER_SETTING_MAP.get(this);
    }
}
