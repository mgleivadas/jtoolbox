package com.brontoblocks;

import java.util.ArrayList;
import java.util.List;

public class IntArrayUtils {

    public static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int j : arr) {
            list.add(j);
        }
        return list;
    }
}
