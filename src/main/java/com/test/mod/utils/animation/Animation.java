package com.test.mod.utils.animation;

import com.test.mod.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class Animation {
    public TimeUtils timeUtils = new TimeUtils();
    @Getter
    @Setter
    protected int duration;
    protected double endPoint;
    @Getter
    protected Direction direction;

    public Animation(int ms, double endPoint) {
        this(ms, endPoint, Direction.FORWARDS);
    }

    public Animation(int ms, double endPoint, Direction direction) {
        this.duration = ms; //Time in milliseconds of how long you want the animation to take.
        this.endPoint = endPoint; //The desired distance for the animated object to go.
        this.direction = direction; //Direction in which the graph is going. If backwards, will start from endPoint and go to 0.
    }

    public boolean finished(Direction direction) {
        return isDone() && this.direction.equals(direction);
    }

    public void reset() {
        timeUtils.reset();
    }

    public boolean isDone() {
        return timeUtils.hasTimeElapsed(duration);
    }

    public Animation setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            timeUtils.setTime(System.currentTimeMillis() - (duration - Math.min(duration, timeUtils.getTime())));
        }
        return this;
    }

    protected boolean correctOutput() {
        return false;
    }

    public double getOutput() {
        if (direction.forwards()) {
            if (isDone()) {
                return endPoint;
            }

            return getEquation(timeUtils.getTime() / (double) duration) * endPoint;
        } else {
            if (isDone()) {
                return 0.0;
            }

            if (correctOutput()) {
                double revTime = Math.min(duration, Math.max(0, duration - timeUtils.getTime()));
                return getEquation(revTime / duration) * endPoint;
            }

            return (1 - getEquation(timeUtils.getTime() / (double) duration)) * endPoint;
        }
    }

    protected abstract double getEquation(double x);
}
