package com.brontoblocks.utils;

public final class BooleanUtils {

    public static boolean strictParse(String val) {

        if (val.equalsIgnoreCase("TRUE"))
            return true;

        if (val.equalsIgnoreCase("FALSE"))
            return false;

        throw new IllegalStateException("Non boolean value: " + val);
    }

    public static boolean enhancedParse(String val) {

        if (val.equalsIgnoreCase("TRUE") || val.equalsIgnoreCase("T"))
            return true;

        if (val.equalsIgnoreCase("FALSE") || val.equalsIgnoreCase("F"))
            return false;

        if (val.equals("0")) {
            return false;
        }

        if (val.equals("1")) {
            return true;
        }

        throw new IllegalStateException("Non boolean value: " + val);
    }
}
