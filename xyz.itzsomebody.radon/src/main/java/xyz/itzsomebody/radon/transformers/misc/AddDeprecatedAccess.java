/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import org.objectweb.asm.Opcodes;
import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.concurrent.atomic.AtomicInteger;

public class AddDeprecatedAccess extends Transformer {
    private boolean doClasses;
    private boolean doMethods;
    private boolean doFields;

    @Override
    public void transform() {
        var classCount = new AtomicInteger();
        var methodCount = new AtomicInteger();
        var fieldCount = new AtomicInteger();
        if (doClasses || doFields || doMethods) {
            classes().stream().filter(this::notExcluded).forEach(cw -> {
                if (doFields) {
                    cw.fieldStream().filter(this::notExcluded).forEach(fw -> {
                        if (!fw.isDeprecated()) {
                            fw.addAccessFlags(Opcodes.ACC_DEPRECATED);
                            fieldCount.incrementAndGet();
                        }
                    });
                }
                if (doMethods) {
                    cw.methodStream().filter(this::notExcluded).forEach(mw -> {
                        if (!mw.isDeprecated()) {
                            mw.addAccessFlags(Opcodes.ACC_DEPRECATED);
                            methodCount.incrementAndGet();
                        }
                    });
                }
                if (doClasses) {
                    if (!cw.isDeprecated()) {
                        cw.addAccessFlags(Opcodes.ACC_DEPRECATED);
                        classCount.incrementAndGet();
                    }
                }
            });
        }
        RadonLogger.info(String.format("Added deprecated access flags to %d classes, %d methods, and %d fields", classCount.get(), methodCount.get(), fieldCount.get()));
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.ADD_DEPRECATED_ACCESS;
    }

    @Override
    public void loadSetup(Configuration config) {
        doClasses = config.getOrDefault(getLocalConfigPath() + ".add_to_classes",false);
        doMethods = config.getOrDefault(getLocalConfigPath() + ".add_to_methods",false);
        doFields = config.getOrDefault(getLocalConfigPath() + ".add_to_fields",false);
    }

    @Override
    public String getConfigName() {
        return Transformers.ADD_DEPRECATED_ACCESS_FLAG.getConfigName();
    }
}
