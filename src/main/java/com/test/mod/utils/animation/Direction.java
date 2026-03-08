package com.test.mod.utils.animation;

public enum Direction {
    FORWARDS,
    BACKWARDS;


    public boolean forwards() {
        return this == FORWARDS;
    }

    public boolean backwards() {
        return this == BACKWARDS;
    }
}
