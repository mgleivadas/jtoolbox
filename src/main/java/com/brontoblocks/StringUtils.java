package com.brontoblocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.brontoblocks.ArgCheck.nonEmpty;
import static java.util.stream.IntStream.range;

public class StringUtils {

    public static Stream<String> toCharStream(String input) {
        return range(0, input.length())
                .mapToObj(i -> Character.valueOf(input.charAt(i)).toString());
    }

    public static List<String> splitBy(String input, int n) {
        List<String> list = new ArrayList<>();
        int j = 0;
        for (int i = 0; j < input.length();) {
            j = Math.min(i + n, input.length());
            list.add(input.substring(i, j));
            i = j;
        }
        return list;
    }

    public static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static int hammingDistance(String val1, String val2) {
        int distance = 0;

        String smaller = val1.length() < val2.length() ? val1 : val2;
        String larger = val1.length() >= val2.length() ? val1 : val2;

        for (int i = 0; i < smaller.length(); i++) {
            if (smaller.charAt(i) != larger.charAt(i)) {
                distance++;
            }
        }

        distance += larger.length() - smaller.length();
        return distance;
    }

    public static String reverse(String value) {
        return (new StringBuilder(value)).reverse().toString();
    }

    public static int lastIndexOf(String value, String pattern) {
        var reversedValue = reverse(nonEmpty("value", value));
        var reversedPattern = reverse(nonEmpty("pattern", pattern));

        var patternPosReversed = reversedValue.indexOf(reversedPattern);

        if (patternPosReversed < 0) {
            return -1;
        } else {

            // this is indicates how many chars are consumed until we reach pattern (pattern inclusive)
            var totalCharsConsumedIncludingPattern = patternPosReversed + pattern.length();
            return value.length() - totalCharsConsumedIncludingPattern;
        }
    }
}
