package com.test.mod.setting.settings;


import com.test.mod.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ListSetting extends Setting<String> {
    private List<String> values;
    private int maxHeight = 100;

    public ListSetting(String value, List<String> values) {
        super(value);
        this.values = new ArrayList<>(values);
    }
    public void clear(){
        values.clear();
    }
    public void addValue(List<String> value) {
        values.addAll(value);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
