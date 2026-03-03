package com.test.mod.ui.system.utils.state;

public class GLStateStack {
    private static final GLState[] S = new GLState[16];
    private static int p = -1;

    static {
        for (int i = 0; i < 16; i++) S[i] = new GLState();
    }

    public static void push() {
        if (p < 15) S[++p].push();
    }

    public static void pop() {
        if (p >= 0) S[p--].pop();
    }

    public static void clear() {
        p = -1;
    }
}