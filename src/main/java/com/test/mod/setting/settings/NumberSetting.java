package com.test.mod.setting.settings;

import com.test.mod.setting.Setting;
import com.google.gson.JsonElement;

public class NumberSetting extends Setting<Number> {
    private Number min;
    private Number max;
    private final String precisePattern;
    public boolean dragging;

    public NumberSetting(Number value, Number min, Number max, String precisePattern) {
        super(value);
        this.min = min;
        this.max = max;
        this.precisePattern = precisePattern;
    }

    @Override
    public boolean canSaveConfig() {
        return true;
    }

    @Override
    public Number getJson(JsonElement jsonElement) {
        return jsonElement.getAsNumber();
    }

    public double getMin() {
        return min.doubleValue();
    }


    public void setMin(Number min) {
        this.min = min;
    }

    public double getMax() {
        return max.doubleValue();
    }

    public void setMax(Number max) {
        this.max = max;
    }

    public String getPrecisePattern() {
        return precisePattern;
    }
}
