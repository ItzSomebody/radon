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

package me.itzsomebody.radon.transformers.obfuscators.ejector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.analysis.constant.ConstantAnalyzer;
import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.obfuscators.ejector.phases.AbstractEjectPhase;
import me.itzsomebody.radon.transformers.obfuscators.ejector.phases.FieldSetEjector;
import me.itzsomebody.radon.transformers.obfuscators.ejector.phases.MethodCallEjector;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Extracts parts of code to individual methods.
 *
 * @author vovanre
 */
public class Ejector extends Transformer {
    private static final Map<String, EjectorSetting> KEY_MAP = new HashMap<>();

    static {
        Stream.of(EjectorSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    private boolean ejectMethodCalls;
    private boolean ejectFieldSet;
    private boolean junkArguments;
    private int junkArgumentStrength;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream()
                .filter(classWrapper -> !excluded(classWrapper))
                .forEach(classWrapper -> processClass(classWrapper, counter));

        Main.info(String.format("Ejected %d regions.", counter.get()));
    }

    private List<AbstractEjectPhase> getPhases(EjectorContext ejectorContext) {
        List<AbstractEjectPhase> phases = new ArrayList<>();
        if (isEjectMethodCalls())
            phases.add(new MethodCallEjector(ejectorContext));
        if (isEjectFieldSet())
            phases.add(new FieldSetEjector(ejectorContext));
        return phases;
    }

    private void processClass(ClassWrapper classWrapper, AtomicInteger counter) {
        new ArrayList<>(classWrapper.getMethods()).stream()
                .filter(methodWrapper -> !excluded(methodWrapper))
                .filter(methodWrapper -> !"<init>".equals(methodWrapper.getMethodNode().name))
                .forEach(methodWrapper -> {
                    EjectorContext ejectorContext = new EjectorContext(counter, classWrapper, junkArguments, junkArgumentStrength);
                    getPhases(ejectorContext).forEach(ejectPhase -> {
                        ConstantAnalyzer constantAnalyzer = new ConstantAnalyzer();
                        try {
                            Main.info("Analyze: " + classWrapper.getOriginalName() + "::" + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescription());
                            Frame<AbstractValue>[] frames = constantAnalyzer.analyze(classWrapper.getName(), methodWrapper.getMethodNode());

                            ejectPhase.process(methodWrapper, frames);
                        } catch (AnalyzerException e) {
                            Main.severe("Can't analyze method: " + classWrapper.getOriginalName() + "::" + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescription());
                            Main.severe(e.toString());
                        }
                    });
                });
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.EJECTOR;
    }

    @Override
    public String getName() {
        return "Ejector";
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(EjectorSetting.EJECT_CALL.getName(), isEjectMethodCalls());
        config.put(EjectorSetting.EJECT_FIELD_SET.getName(), isEjectFieldSet());
        config.put(EjectorSetting.JUNK_ARGUMENT.getName(), isJunkArguments());
        config.put(EjectorSetting.JUNK_ARGUMENT_STRENGTH.getName(), getJunkArgumentStrength());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setEjectMethodCalls(getValueOrDefault(EjectorSetting.EJECT_CALL.getName(), config, false));
        setEjectFieldSet(getValueOrDefault(EjectorSetting.EJECT_FIELD_SET.getName(), config, false));
        setJunkArguments(getValueOrDefault(EjectorSetting.JUNK_ARGUMENT.getName(), config, false));
        setJunkArgumentStrength(getValueOrDefault(EjectorSetting.JUNK_ARGUMENT_STRENGTH.getName(), config, 5));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            EjectorSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.EJECTOR.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.EJECTOR.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private boolean isEjectMethodCalls() {
        return ejectMethodCalls;
    }

    private void setEjectMethodCalls(boolean ejectMethodCalls) {
        this.ejectMethodCalls = ejectMethodCalls;
    }


    private boolean isEjectFieldSet() {
        return ejectFieldSet;
    }

    private void setEjectFieldSet(boolean ejectFieldSet) {
        this.ejectFieldSet = ejectFieldSet;
    }

    private boolean isJunkArguments() {
        return junkArguments;
    }

    private void setJunkArguments(boolean junkArguments) {
        this.junkArguments = junkArguments;
    }

    private int getJunkArgumentStrength() {
        return junkArgumentStrength;
    }

    private void setJunkArgumentStrength(int junkArgumentStrength) {
        this.junkArgumentStrength = Math.min(junkArgumentStrength, 50);
    }
}
