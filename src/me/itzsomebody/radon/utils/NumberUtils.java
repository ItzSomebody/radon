package me.itzsomebody.radon.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utils that I can't figure out how to categorize.
 *
 * @author ItzSomebody
 */
public class NumberUtils {
    /**
     * Gets a random {@link Integer} from {@link ThreadLocalRandom#nextInt()}.
     *
     * @return a random {@link Integer} from {@link ThreadLocalRandom#nextInt()}.
     */
    public static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    /**
     * Gets a random {@link Integer} from {@link ThreadLocalRandom#nextInt(int)}.
     *
     * @param bounds {@link Integer} used to define the bounds of the random.
     * @return a random {@link Integer} from {@link ThreadLocalRandom#nextInt(int)}.
     */
    public static int getRandomInt(int bounds) {
        return ThreadLocalRandom.current().nextInt(bounds);
    }
}
