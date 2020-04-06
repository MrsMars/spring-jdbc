package com.aoher.util;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class NameGenerator {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String NAME_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER;

    private static SecureRandom random = new SecureRandom();

    public static  String randomName(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        IntStream.range(0, length).map(i -> random.nextInt(NAME_ALLOW_BASE.length())).forEach(rndCharAt -> {
            char rndChar = NAME_ALLOW_BASE.charAt(rndCharAt);
            sb.append(rndChar);
        });
        return sb.toString();
    }
}
