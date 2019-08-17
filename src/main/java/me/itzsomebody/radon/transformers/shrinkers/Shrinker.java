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
import java.util.List;
import java.util.stream.Stream;
import me.itzsomebody.radon.config.Configuration;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

import static me.itzsomebody.radon.config.ConfigurationSetting.SHRINKER;

/**
 * Abstract class for shrinking transformers.
 *
 * @author ItzSomebody
 */
public class Shrinker extends Transformer {
    private final List<Shrinker> shrinkers = new ArrayList<>();

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
    public void setConfiguration(Configuration config) {
        Stream.of(ShrinkerSetting.values()).filter(setting -> {
            String path = SHRINKER + "." + setting.getName();

            if (config.contains(path)) {
                return config.get(path);
            }

            return false;
        }).forEach(setting -> shrinkers.add(setting.getShrinker()));
    }
}
