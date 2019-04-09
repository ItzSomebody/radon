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

package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.obfuscators.virtualizer.mvm.MVMTransformer;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Translates Java bytecode instructions into a custom bytecode instruction set which
 * can (theoretically) only be understood by a custom virtual machine.
 *
 * @author ItzSomebody
 */
public class Virtualizer extends Transformer {
    private static final Map<String, VirtualizerSetting> KEY_MAP = new HashMap<>();
    private String vmType;

    static {
        Stream.of(VirtualizerSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        switch (getVMType()) {
            case "meme_vm":
                MVMTransformer transformer = new MVMTransformer();
                transformer.init(radon);
                transformer.transform();
                break;
            default:
                throw new RadonException(getVMType() + " is an unknown vm type.");
        }
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.VIRTUALIZER;
    }

    @Override
    public String getName() {
        return "Virtualizer";
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(VirtualizerSetting.VM_TYPE.getName(), getVMType());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setVMType(getValueOrDefault(VirtualizerSetting.VM_TYPE.getName(), config, "meme_vm"));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            VirtualizerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.VIRTUALIZER.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.VIRTUALIZER.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    public String getVMType() {
        return vmType;
    }

    public void setVMType(String vmType) {
        this.vmType = vmType;
    }
}
