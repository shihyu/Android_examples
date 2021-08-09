package com.eventyay.organizer.utils;

import androidx.collection.SparseArrayCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import timber.log.Timber;

@SuppressWarnings("PMD.UseUtilityClass") // This class is already Utility class
public final class JWTUtils {

    private JWTUtils() {
        // Never Called
    }

    public static SparseArrayCompat<String> decode(String token) {
        SparseArrayCompat<String> decoded = new SparseArrayCompat<>(2);

        String[] split = token.split("\\.");
        decoded.append(0, getJson(split[0]));
        decoded.append(1, getJson(split[1]));

        return decoded;
    }

    public static long getExpiry(String token) throws JSONException {
        SparseArrayCompat<String> decoded = decode(token);

        // We are using JSONObject instead of GSON as it takes about 5 ms instead of 150 ms taken by GSON
        return Long.parseLong(new JSONObject(decoded.get(1)).get("exp").toString());
    }

    public static int getIdentity(String token) throws JSONException {
        SparseArrayCompat<String> decoded = decode(token);

        return Integer.parseInt(new JSONObject(decoded.get(1)).get("identity").toString());
    }

    public static boolean isExpired(String token) {
        try {
            return System.currentTimeMillis() / 1000 >= getExpiry(token);
        } catch (JSONException jse) {
            return true;
        }
    }

    private static String getJson(String strEncoded) {
        byte[] decodedBytes = Base64Utils.decode(strEncoded);
        try {
            return new String(decodedBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e);
            return "";
        }
    }

    public static String getAuthorization(String token) {
        return String.format("JWT %s", token);
    }

    /**
     * Base64 class because we can't test Android class and this is faster
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private static class Base64Utils {

        private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

        private static int[] toInt = new int[128];

        static {
            for (int i = 0; i < ALPHABET.length; i++) {
                toInt[ALPHABET[i]] = i;
            }
        }

        /**
         * Translates the specified Base64 string into a byte array.
         *
         * @param s the Base64 string(not null)
         * @return the byte array(not null)
         */
        static byte[] decode(String s) {
            int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
            byte[] buffer = new byte[s.length() * 3 / 4 - delta];
            int mask = 0xFF;
            int index = 0;
            for (int i = 0; i < s.length(); i += 4) {
                int c0 = toInt[s.charAt(i)];
                int c1 = toInt[s.charAt(i + 1)];
                buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c2 = toInt[s.charAt(i + 2)];
                buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c3 = toInt[s.charAt(i + 3)];
                buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
            }
            return buffer;
        }
    }
}
