package com.test.mod.utils.animation.impl.ease;

import com.test.mod.utils.animation.Animation;
import com.test.mod.utils.animation.Direction;

public class TestAnimation extends Animation {
    public TestAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public TestAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double t) {
        float zeta = 0.7f;
        float w0   = 12.0f;
        float wd   = w0 * (float)Math.sqrt(1.0f - zeta*zeta);

        float spring = (float)(
                1 - Math.exp(-zeta * w0 * t) *
                        (Math.cos(wd * t) + (zeta*w0/wd)*Math.sin(wd * t))
        );

        float exponent = 0.75f;

        return (float)Math.pow(spring, exponent);
    }


}
