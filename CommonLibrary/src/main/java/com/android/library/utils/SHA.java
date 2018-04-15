package com.android.library.utils;

import java.security.MessageDigest;

/**
 * Created by xielei on 15/7/29.
 */
public final class SHA {

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String getSHA(String value) {
        try {
            return new String(encodeHex(digest(value, "SHA", "UTF-8"), false));
        } catch (Exception e) {
            return "";
        }
    }

    private static byte[] digest(String value, String type, String charSet) throws Exception {
        MessageDigest md = MessageDigest.getInstance(type);
        md.update(value.getBytes(charSet));
        return md.digest();
    }

    private static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
}
