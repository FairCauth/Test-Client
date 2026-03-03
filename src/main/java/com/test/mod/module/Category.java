package com.test.mod.module;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Category {
    COMBAT,
    RENDER,
    MOVEMENT,
    MISC;

    public static List<Category> getCategories(Category... blackList) {
        return Arrays.stream(Category.values()).filter(it ->
                        !Arrays.stream(blackList).toList().contains(it)).
                collect(Collectors.toList());
    }
}
