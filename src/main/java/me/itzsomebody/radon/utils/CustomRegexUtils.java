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

package me.itzsomebody.radon.utils;

/**
 * Custom-defined Regex rules.
 *
 * @author ItzSomebody
 */
public class CustomRegexUtils {
    /**
     * Returns true/false based on if input is matched to this specific rule.
     *
     * @param customregex a {@link String} which is used as a "custom regex"
     *                    statement.
     * @param string      a {@link String} to try to match.
     * @return true/false based on if input is matched to this specific rule.
     */
    public static boolean isMatched(String customregex, String string) {
        return (customregex.equals(string)
                || (customregex.contains("*")
                && string.contains(customregex.split("\\*")[0])));
    }
}
