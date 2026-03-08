package com.test.mod.utils.animation.impl;

import com.test.mod.utils.animation.Animation;
import com.test.mod.utils.animation.Direction;
import lombok.Getter;

public class ContinualAnimation {
    private float output, endpoint;

    @Getter
    private Animation animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void animate(float destination, int ms) {
        output = (float) (endpoint - animation.getOutput());
        if (Math.abs(endpoint - destination) > 0.01f) {
            endpoint = destination;
            animation = new SmoothStepAnimation(ms, endpoint - output, Direction.BACKWARDS);
        }
    }

    public boolean isDone() {
        return Math.abs(output - endpoint) < 0.01f || animation.isDone();
    }

    public float getOutput() {
        output = (float) (endpoint - animation.getOutput());
        return output;
    }
    public void reset() {
        output = 0;
        endpoint = 0;
        animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    }
    public void setImmediate(float value) {
        output = value;
        endpoint = value;
        animation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    }
}
