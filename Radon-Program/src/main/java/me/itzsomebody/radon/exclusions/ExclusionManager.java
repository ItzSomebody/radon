/*
 * Copyright (C) 2018 ItzSomebody
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

import java.util.LinkedList;

/**
 * Class containing a {@link LinkedList<Exclusion>} of all the created exclusions.
 *
 * @author ItzSomebody
 */
public class ExclusionManager {
    private LinkedList<Exclusion> exclusions = new LinkedList<>();

    public LinkedList<Exclusion> getExclusions() {
        return this.exclusions;
    }

    public void addExclusion(Exclusion exclusion) {
        this.exclusions.add(exclusion);
    }

    public boolean isExcluded(String pattern, ExclusionType type) {
        for (Exclusion exclusion : this.exclusions) {
            if ((exclusion.getExclusionType() == type || exclusion.getExclusionType() == ExclusionType.GLOBAL)
                    && exclusion.matches(pattern)) {
                return true;
            }
        }

        return false;
    }
}
