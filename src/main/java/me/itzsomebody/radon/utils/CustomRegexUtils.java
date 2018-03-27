package me.itzsomebody.radon.utils;

/**
 * Custom-defined Regex rules.
 *
 * @author ItzSomebody
 */
public class CustomRegexUtils {
    /**
     * Returns true/false based on if input is matched to this specific rule.
     *
     * @param customregex a {@link String} which is used as a "custom regex"
     *                    statement.
     * @param string      a {@link String} to try to match.
     * @return true/false based on if input is matched to this specific rule.
     */
    public static boolean isMatched(String customregex, String string) {
        return (customregex.equals(string)
                || (customregex.contains("*")
                && string.contains(customregex.split("\\*")[0])));
    }
}
