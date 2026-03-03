package com.test.mod.setting.attribute;


import com.test.mod.setting.Setting;

/**
 * @param <T> setting type
 * @author FairCauth
 */
public record SettingAttribute<T>(Setting<?> setting, T... value) {

    @SafeVarargs
    public SettingAttribute {
    }

    public boolean get() {
        for (T t : value)
            if (t.equals(setting.getParent().getValue())) return true;
        return value.equals(setting.getParent().getValue());
    }
}
