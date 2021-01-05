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

package xyz.itzsomebody.radon.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static ThreadLocalRandom instance() {
        return ThreadLocalRandom.current();
    }

    // --------
    // Integers
    // --------

    public static int randomInt(int origin, int bound) {
        return instance().nextInt(origin, bound);
    }

    public static int randomInt(int bound) {
        return instance().nextInt(bound);
    }

    public static int randomInt() {
        return instance().nextInt();
    }

    // -----
    // Longs
    // -----

    public static long randomLong(long origin, long bound) {
        return instance().nextLong(origin, bound);
    }

    public static long randomLong(long bound) {
        return instance().nextLong(bound);
    }

    public static long randomLong() {
        return instance().nextLong();
    }

    // ------
    // Floats
    // ------

    public static float randomFloat(float origin, float bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException("bound must be greater than origin");
        }

        float r = (float)((randomInt()) >>> 8) * 5.9604645E-8F;
        if (origin < bound) {
            r = r * (bound - origin) + origin;
            if (r >= bound) {
                r = Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
            }
        }

        return r;
    }

    public static float randomFloat(float bound) {
        if (bound <= 0.0F) {
            throw new IllegalArgumentException("bound must be positive");
        }

        float r = (float)((randomInt()) >>> 8) * 5.9604645E-8F;
        return r < bound ? r : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    public static float randomFloat() {
        return instance().nextFloat();
    }

    // --------
    // Doubles
    // --------

    public static double randomDouble(double origin, double bound) {
        return instance().nextDouble(origin, bound);
    }

    public static double randomDouble(double bound) {
        return instance().nextDouble(bound);
    }

    public static double randomDouble() {
        return instance().nextDouble();
    }

    // -----
    // Misc.
    // -----

    public static boolean randomBoolean() {
        return instance().nextBoolean();
    }

    public static byte[] randomBytes(int length) {
        var arr = new byte[length];
        instance().nextBytes(arr);
        return arr;
    }

    public static byte[] randomBytes() {
        var arr = new byte[randomInt(0xFFFF)];
        instance().nextBytes(arr);
        return arr;
    }
}
