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

public class RandomUtils {
    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static int getRandomInt() {
        return random.nextInt();
    }

    public static int getRandomInt(int bounds) {
        return random.nextInt(1, bounds);
    }

    public static int getRandomInt(int origin, int bounds) {
        return random.nextInt(origin, bounds);
    }

    public static int getRandomIntNoOrigin(int bounds) {
        return random.nextInt(bounds);
    }

    public static long getRandomLong() {
        return random.nextLong();
    }

    public static long getRandomLong(long bounds) {
        return random.nextLong(1, bounds);
    }

    public static float getRandomFloat() {
        return random.nextFloat();
    }

    public static double getRandomDouble() {
        return random.nextDouble();
    }
}
