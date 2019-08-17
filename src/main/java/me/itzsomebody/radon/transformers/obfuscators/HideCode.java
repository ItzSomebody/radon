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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.config.Configuration;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.ASMUtils;

import static me.itzsomebody.radon.config.ConfigurationSetting.HIDE_CODE;

/**
 * Adds a synthetic modifier and bridge modifier if possible to attempt to hide code against some lower-quality
 * decompilers.
 *
 * @author ItzSomebody
 */
public class HideCode extends Transformer {
    private boolean hideClassesEnabled;
    private boolean hideMethodsEnabled;
    private boolean hideFieldsEnabled;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(cw -> !excluded(cw)).forEach(cw -> {
            if (isHideClassesEnabled()) {
                if (!cw.getAccess().isSynthetic() && !ASMUtils.hasAnnotations(cw.getClassNode())) {
                    cw.setAccessFlags(cw.getAccessFlags() | ACC_SYNTHETIC);
                    counter.incrementAndGet();
                }
            }
            if (isHideMethodsEnabled()) {
                cw.getMethods().stream().filter(mw -> !excluded(mw) && !ASMUtils.hasAnnotations(mw.getMethodNode())).forEach(mw -> {
                    boolean atLeastOnce = false;

                    if (!mw.getAccess().isSynthetic()) {
                        mw.setAccessFlags(mw.getAccessFlags() | ACC_SYNTHETIC);
                        atLeastOnce = true;
                    }
                    if (!mw.getName().startsWith("<") && !mw.getAccess().isBridge()) {
                        mw.setAccessFlags(mw.getAccessFlags() | ACC_BRIDGE);
                        atLeastOnce = true;
                    }

                    if (atLeastOnce)
                        counter.incrementAndGet();

                });
            }
            if (isHideFieldsEnabled()) {
                cw.getFields().stream().filter(fw -> !excluded(fw) && !ASMUtils.hasAnnotations(fw.getFieldNode())).forEach(fw -> {
                    if (!fw.getAccess().isSynthetic()) {
                        fw.setAccessFlags(fw.getAccessFlags() | ACC_SYNTHETIC);
                        counter.incrementAndGet();
                    }
                });
            }
        });

        Main.info(String.format("Hid %d members.", counter.get()));
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.HIDE_CODE;
    }

    @Override
    public String getName() {
        return "Hide code";
    }

    @Override
    public void setConfiguration(Configuration config) {
        setHideClassesEnabled(config.getOrDefault(HIDE_CODE + ".hide_classes", false));
        setHideFieldsEnabled(config.getOrDefault(HIDE_CODE + ".hide_fields", false));
        setHideMethodsEnabled(config.getOrDefault(HIDE_CODE + ".hide_methods", false));
    }

    private boolean isHideClassesEnabled() {
        return hideClassesEnabled;
    }

    private void setHideClassesEnabled(boolean hideClassesEnabled) {
        this.hideClassesEnabled = hideClassesEnabled;
    }

    private boolean isHideMethodsEnabled() {
        return hideMethodsEnabled;
    }

    private void setHideMethodsEnabled(boolean hideMethodsEnabled) {
        this.hideMethodsEnabled = hideMethodsEnabled;
    }

    private boolean isHideFieldsEnabled() {
        return hideFieldsEnabled;
    }

    private void setHideFieldsEnabled(boolean hideFieldsEnabled) {
        this.hideFieldsEnabled = hideFieldsEnabled;
    }
}
