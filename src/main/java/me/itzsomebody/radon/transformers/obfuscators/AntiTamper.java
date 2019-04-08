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

import java.util.LinkedHashMap;
import java.util.Map;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * This applies some type of integrity-aware code. Currently, there are two
 * types of anti-tampers: passive and active. The active anti-tamper will
 * actively search for modifications to the JAR and crash the JVM. The
 * passive anti-tamper will modify its environment based on random
 * components of the program.
 *
 * @author ItzSomebody
 */
public class AntiTamper extends Transformer {
    private static final int PASSIVE = 1;
    private static final int ACTIVE = 2;
    private int type;

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Anti-Tamper";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.ANTI_TAMPER;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put("mode", ((type == PASSIVE) ? "passive" : "active"));

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setType(getValueOrDefault("mode", config, "passive"));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        // TODO
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setType(String mode) {
        if (mode.equalsIgnoreCase("passive"))
            setType(PASSIVE);
        else if (mode.equalsIgnoreCase("active"))
            setType(ACTIVE);
    }
}
