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

package me.itzsomebody.radon.transformers.obfuscators.flow;

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
 * Abstract class for flow obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class FlowObfuscation extends Transformer {
    private static final Map<String, FlowObfuscationSetting> KEY_MAP = new HashMap<>();
    private static final Map<FlowObfuscation, FlowObfuscationSetting> FLOW_SETTING_MAP = new HashMap<>();
    private final List<FlowObfuscation> flowObfuscators = new ArrayList<>();

    static {
        FlowObfuscationSetting[] values = FlowObfuscationSetting.values();
        Stream.of(values).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
        Stream.of(values).forEach(setting -> FLOW_SETTING_MAP.put(setting.getFlowObfuscation(), setting));
    }

    @Override
    public void transform() {
        flowObfuscators.forEach(flowObfuscator -> {
            flowObfuscator.init(radon);
            flowObfuscator.transform();
        });
    }

    @Override
    public String getName() {
        return "Flow Obfuscation";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.FLOW_OBFUSCATION;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        flowObfuscators.forEach(obfuscator -> config.put(obfuscator.getFlowObfuscationSetting().getName(), true));

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        Stream.of(FlowObfuscationSetting.values()).filter(setting -> config.containsKey(setting.getName())
                && (Boolean) config.get(setting.getName())).forEach(setting -> flowObfuscators.add(setting.getFlowObfuscation()));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            FlowObfuscationSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.FLOW_OBFUSCATION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.FLOW_OBFUSCATION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private FlowObfuscationSetting getFlowObfuscationSetting() {
        return FLOW_SETTING_MAP.get(this);
    }
}
