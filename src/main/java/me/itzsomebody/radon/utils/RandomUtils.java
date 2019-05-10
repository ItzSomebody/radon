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

package me.itzsomebody.radon.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Used to generate various randoms.
 *
 * @author ItzSomebody
 * @author freeasbird
 */
public class RandomUtils {
    public static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int getRandomInt(int bounds) {
        return ThreadLocalRandom.current().nextInt(bounds);
    }

    public static int getRandomInt(int origin, int bounds) {
        return ThreadLocalRandom.current().nextInt(origin, bounds);
    }

    public static boolean getRandomBoolean() {
        return getRandomFloat() > 0.5;
    }

    public static <T> T getRandomElement(List<T> list) {
        return list.get(getRandomInt(list.size()));
    }

    public static long getRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static long getRandomLong(long bounds) {
        return ThreadLocalRandom.current().nextLong(bounds);
    }

    public static float getRandomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public static float getRandomFloat(float bounds) {
        return (float) ThreadLocalRandom.current().nextDouble(bounds);
    }

    public static double getRandomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static double getRandomDouble(double bounds) {
        return ThreadLocalRandom.current().nextDouble(bounds);
    }
}
