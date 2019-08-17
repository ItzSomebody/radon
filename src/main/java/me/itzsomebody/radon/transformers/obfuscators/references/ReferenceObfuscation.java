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

package me.itzsomebody.radon.transformers.obfuscators.references;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import me.itzsomebody.radon.config.Configuration;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

import static me.itzsomebody.radon.config.ConfigurationSetting.REFERENCE_OBFUSCATION;

/**
 * Abstract class for reference obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class ReferenceObfuscation extends Transformer {
    private final List<ReferenceObfuscation> referenceObfuscators = new ArrayList<>();

    @Override
    public void transform() {
        referenceObfuscators.forEach(tranformer -> {
            tranformer.init(radon);
            tranformer.transform();
        });
    }

    @Override
    public String getName() {
        return "Reference obfuscation";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.REFERENCE_OBFUSCATION;
    }

    @Override
    public void setConfiguration(Configuration config) {
        Stream.of(ReferenceObfuscationSetting.values()).filter(setting -> {
            String path = REFERENCE_OBFUSCATION + "." + setting.getName();

            if (config.contains(path)) {
                return config.get(path);
            }

            return false;
        }).forEach(setting -> referenceObfuscators.add(setting.getReferenceObfuscation()));
    }
}
