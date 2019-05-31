package me.itzsomebody.radon.dictionaries;

public interface Dictionary {
    String randomString(int length);

    String uniqueRandomString(int length);

    String nextUniqueString();

    String lastUniqueString();

    String getDictionaryName();

    void reset();

    Dictionary copy();
}
