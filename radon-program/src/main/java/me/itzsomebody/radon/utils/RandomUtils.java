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
 * Used to generate various randoms.
 * TODO: rewrite this absolute garbage.
 *
 * @author ItzSomebody
 * @author freeasbird
 */
public class RandomUtils {public static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int getRandomInt(int bounds) {
        return ThreadLocalRandom.current().nextInt(1, bounds);
    }

    public static int getRandomInt(int origin, int bounds) {
        return ThreadLocalRandom.current().nextInt(origin, bounds);
    }

    public static int getRandomIntNoOrigin(int bounds) {
        return ThreadLocalRandom.current().nextInt(bounds);
    }

    public static long getRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static long getRandomLong(long bounds) {
        return ThreadLocalRandom.current().nextLong(1, bounds);
    }

    public static float getRandomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public static double getRandomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static boolean getRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
