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
