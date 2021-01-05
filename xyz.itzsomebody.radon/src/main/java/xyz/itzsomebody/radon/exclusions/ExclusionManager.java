/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.exclusions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExclusionManager {
    private final Set<Exclusion> exclusions;
    private final Map<String, Boolean> cache;

    public ExclusionManager(Map<String, String> patterns) {
        this.exclusions = new HashSet<>(patterns.size());
        this.cache = new HashMap<>();

        patterns.forEach((k, v) -> {
            var invert = false;

            if (k.startsWith("!")) {
                invert = true;
                k = k.substring(1);
            }

            exclusions.add(new Exclusion(v, Exclusion.ExclusionType.forIdentifier(k), invert));
        });
    }

    public boolean find(String other, Exclusion.ExclusionType exclusionType) {
        // fixme: flawed cache logic
        if (cache.containsKey(other)) {
            return cache.get(other);
        }

        var result = exclusions.stream().anyMatch(exclusion -> exclusion.matches(other, exclusionType));
        cache.put(other, result);
        return result;
    }
}
