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

import java.util.concurrent.ThreadLocalRandom;

/**
 * Number utils.
 *
 * @author ItzSomebody
 */
public class NumberUtils {
    /**
     * Gets a random {@link Integer} from {@link ThreadLocalRandom#nextInt()}.
     *
     * @return a random {@link Integer} from
     * {@link ThreadLocalRandom#nextInt()}.
     */
    public static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    /**
     * Gets a random {@link Integer} from
     * {@link ThreadLocalRandom#nextInt(int)}.
     *
     * @param bounds {@link Integer} used to define the bounds of the random.
     * @return a random {@link Integer} from
     * {@link ThreadLocalRandom#nextInt(int)}.
     */
    public static int getRandomInt(int bounds) {
        return ThreadLocalRandom.current().nextInt(bounds);
    }

    /**
     * Gets a random {@link Long} from {@link ThreadLocalRandom#nextLong()}.
     *
     * @return a random {@link Integer} from
     * {@link ThreadLocalRandom#nextLong()}.
     */
    public static long getRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    /**
     * Gets a random {@link Long} from
     * {@link ThreadLocalRandom#nextLong(long)}.
     *
     * @param bounds {@link Long} used to define the bounds of the random.
     * @return a random {@link Integer} from
     * {@link ThreadLocalRandom#nextLong(long)}.
     */
    public static long getRandomLong(int bounds) {
        return ThreadLocalRandom.current().nextLong(bounds);
    }
}
