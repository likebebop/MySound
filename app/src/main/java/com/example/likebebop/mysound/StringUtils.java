package com.example.likebebop.mysound;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.Locale;

/**
 * @author Choi Yunjae (LP10134, yunjae.choi@linecorp.com)
 */
public class StringUtils {
    public static final String EMPTY = "";

    public static String withoutTagPrefix(String tagName) {
        if (!TextUtils.isEmpty(tagName) && tagName.startsWith("#")) {
            return tagName.substring(1, tagName.length());
        }
        return tagName;
    }

    public static String withTagPrefix(String tagName) {
        if (!TextUtils.isEmpty(tagName) && !tagName.startsWith("#")) {
            return new StringBuilder().append("#").append(tagName).toString();
        }
        return tagName;
    }

    public static String getUrlEncoded(String text) {
        String urlEncoded = text;

        try {
            urlEncoded = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return urlEncoded;
    }


    /**
     * <p>
     * Checks if a String is empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p>
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * String. That functionality is available in isBlank().
     * </p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    /**
     * <p>
     * Checks if a String is whitespace, empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    @NonNull
    public static String toHexString(@NonNull byte[] byteArray) {
        String[] hexStrings = new String[byteArray.length];
        for (int i = 0; i < byteArray.length; ++i) {
            hexStrings[i] = String.format(Locale.US, "%02x", byteArray[i]);
        }
        return TextUtils.join(" ", hexStrings);
    }

    // https://wiki.linecorp.com/display/LINEDOCS/The+7+Ways+of+Counting+Characters_ko
    public static int getGraphemeLength(String value) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(value);
        int count = 0;
        while (it.next() != BreakIterator.DONE) {
            count++;
        }
        return count;
    }

    public static String getMaxLengthStringExceptNewline(String str, int max) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(str);
        int count = 0;
        int start = 0;
        while (it.next() != BreakIterator.DONE) {
            int end = it.current();
            String curS = str.substring(start, end);
            if (!curS.equals("\n")) {
                count++;
            }
            if (count == max) {
                return str.substring(0, end);
            }
            start = end;
        }
        return str;
    }

    public static int getGraphemeLengthExceptNewLine(String value) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(value);
        int count = 0;
        int start = 0;
        while (it.next() != BreakIterator.DONE) {
            int end = it.current();
            String curS = value.substring(start, end);
            if (!curS.equals("\n")) {
                count++;
            }
            start = end;
        }
        return count;
    }

    public static boolean isLowerAscii(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= 128) {
                return false;
            }
        }
        return true;
    }
}
