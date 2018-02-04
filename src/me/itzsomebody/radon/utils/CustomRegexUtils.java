package me.itzsomebody.radon.utils;

/**
 * Custom-defined Regex rules.
 *
 * @author ItzSomebody
 */
public class CustomRegexUtils {
    public static boolean isMatched(String customregex, String string) {
        return (customregex.equals(string) || customregex.contains("*") && string.contains(customregex.split("\\*")[0]));
    }
}
