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

public class AddBridgeAccess extends Transformer {
    @Override
    public void transform() {
        var count = new AtomicInteger();
        classes().stream().filter(this::notExcluded).forEach(cw -> {
            cw.methodStream().filter(mw -> notExcluded(mw) && !mw.hasVisibleAnnotations()).forEach(mw -> {
                // Adding ACC_BRIDGE to init / clinit is e-legal
                if (!mw.getMethodNode().name.startsWith("<") && !mw.isBridge()) {
                    mw.addAccessFlags(Opcodes.ACC_BRIDGE);
                    count.incrementAndGet();
                }
            });
        });
        RadonLogger.info("Added bridge access flags to " + count.get() + " methods");
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.ADD_BRIDGE_ACCESS;
    }

    @Override
    public void loadSetup(Configuration config) {
        // do nothing
    }

    @Override
    public String getConfigName() {
        return Transformers.ADD_BRIDGE_ACCESS_FLAG.getConfigName();
    }
}
