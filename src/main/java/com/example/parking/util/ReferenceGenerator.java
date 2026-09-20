package com.example.parking.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Generation des references fonctionnelles (reservation, session, facture). */
public final class ReferenceGenerator {

    private static final DateTimeFormatter DATE_PART = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceGenerator() {
    }

    public static String reservation() {
        return generate("RES");
    }

    public static String session() {
        return generate("SES");
    }

    public static String invoice() {
        return generate("FAC");
    }

    public static String subscription() {
        return generate("ABO");
    }

    public static String generate(String prefix) {
        StringBuilder suffix = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffix.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return prefix + "-" + LocalDate.now().format(DATE_PART) + "-" + suffix;
    }
}
