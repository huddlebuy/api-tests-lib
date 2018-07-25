package com.perkbox.util;

public class Text {

    public static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    public static final String CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL = "<([{\\^-=$!|]})?*+.>&";

    public static final String ALL = ALPHA + CAPS + SPECIAL + NUMBERS;

    public static String generateText(int length) {
        String text = "";
        do {
            text += "random text and more ";
        } while (text.length() < length);

        return text.substring(0, length);
    }
}