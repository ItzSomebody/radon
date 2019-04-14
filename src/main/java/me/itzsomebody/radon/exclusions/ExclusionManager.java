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

package me.itzsomebody.radon.exclusions;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing a {@link List<Exclusion>} of all the created exclusions.
 *
 * @author ItzSomebody
 */
public class ExclusionManager {
    private List<Exclusion> exclusions = new ArrayList<>();

    public List<Exclusion> getExclusions() {
        return this.exclusions;
    }

    public void addExclusion(Exclusion exclusion) {
        this.exclusions.add(exclusion);
    }

    public boolean isExcluded(String pattern, ExclusionType type) {
        return exclusions.stream().anyMatch(exclusion ->
                (exclusion.getExclusionType() == type || exclusion.getExclusionType() == ExclusionType.GLOBAL)
                        && exclusion.matches(pattern));
    }
}
