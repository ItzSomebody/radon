package me.itzsomebody.radon.dictionaries;

/**
 * String generation interface.
 *
 * @author ItzSomebody
 */
public interface Dictionary {
    /**
     * @param length the length the generated string should be.
     * @return generates string randomly.
     */
    String randomString(int length);

    /**
     * @param length the length the generated string should be.
     * @return generates unique string randomly.
     */
    String uniqueRandomString(int length);

    /**
     * @return next unique string.
     */
    String nextUniqueString();

    /**
     * @return last generated unique string. If non, null.
     */
    String lastUniqueString();

    /**
     * @return name of dictionary.
     */
    String getDictionaryName();

    void reset();

    Dictionary copy();
}
