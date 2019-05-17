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

package me.itzsomebody.radon.transformers.obfuscators.shuffler;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Randomizes the order of methods and fields in a class.
 *
 * @author ItzSomebody
 */
public class MemberShuffler extends Transformer {
    private static final Map<String, MemberShufflerSetting> KEY_MAP = new HashMap<>();
    private boolean shuffleMethodsEnabled;
    private boolean shuffleFieldsEnabled;

    static {
        Stream.of(MemberShufflerSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            if (isShuffleMethodsEnabled()) {
                Collections.shuffle(classWrapper.getClassNode().methods);
                counter.addAndGet(classWrapper.getClassNode().methods.size());
            }

            if (isShuffleFieldsEnabled() && classWrapper.getClassNode().fields != null) {
                Collections.shuffle(classWrapper.getClassNode().fields);
                counter.addAndGet(classWrapper.getClassNode().fields.size());
            }
        });

        Main.info(String.format("Shuffled %d members.", counter.get()));
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.SHUFFLER;
    }

    @Override
    public String getName() {
        return "Member Shuffler";
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(MemberShufflerSetting.SHUFFLE_FIELDS.getName(), isShuffleFieldsEnabled());
        config.put(MemberShufflerSetting.SHUFFLE_METHODS.getName(), isShuffleMethodsEnabled());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setShuffleFieldsEnabled(getValueOrDefault(MemberShufflerSetting.SHUFFLE_FIELDS.getName(), config, false));
        setShuffleMethodsEnabled(getValueOrDefault(MemberShufflerSetting.SHUFFLE_METHODS.getName(), config, false));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            MemberShufflerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.MEMBER_SHUFFLER.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.MEMBER_SHUFFLER.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private boolean isShuffleMethodsEnabled() {
        return shuffleMethodsEnabled;
    }

    private void setShuffleMethodsEnabled(boolean shuffleMethodsEnabled) {
        this.shuffleMethodsEnabled = shuffleMethodsEnabled;
    }

    private boolean isShuffleFieldsEnabled() {
        return shuffleFieldsEnabled;
    }

    private void setShuffleFieldsEnabled(boolean shuffleFieldsEnabled) {
        this.shuffleFieldsEnabled = shuffleFieldsEnabled;
    }
}
